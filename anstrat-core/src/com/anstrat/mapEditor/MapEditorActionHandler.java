package com.anstrat.mapEditor;


import com.anstrat.core.Assets;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;
import com.anstrat.geography.TerrainType;
import com.anstrat.gui.GBuilding;
import com.anstrat.gui.GTile;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MapEditorActionHandler {
	
	public Object selected = null;
	public GTile selectedTile = null;
	
	/**
	 * Select building or terrain.
	 */
	public void select(Object object){
		if(object instanceof Integer || object instanceof TerrainType || object == null){
			selected = object;
			if(object!=null)
				MapEditor.getInstance().userInterface.changeSelectionType(object instanceof TerrainType);
		}
		else
			Gdx.app.error("Map Editor", String.format("ERROR: Tried to select object which is neither an Integer nor a TerrainType."));
	}

	/**
	 * Change the tile with new terrain
	 * @param tile the tile to be changed
	 * @param terrain the terrain that the tile should have
	 */
	private void changeTile(GTile gTile, TerrainType terrain) {
		gTile.tile.terrain = terrain;
		gTile.tile.coordinates = gTile.tile.coordinates; 
		gTile.setTexture(terrain);
	}
	
	/**
	 * Call this when a tile is clicked
	 * @param gTile
	 */
	public void click(GTile gTile) {
		MapEditor mapEd = MapEditor.getInstance(); 
		selectedTile = null;
		
		if(selected == null && gTile!=null)
			changeOwnerPopup(gTile);
		// Terrain
		else if (selected instanceof TerrainType) {
			TerrainType type = (TerrainType) selected;
			// remove existing building
			Building existing = mapEd.map.getBuildingByTile(gTile.tile.coordinates);
			if(existing != null){
				mapEd.gBuildings.remove(existing.id);
				mapEd.map.setBuilding(existing.tileCoordinate, null);
			}
			
			if (!(gTile.tile.terrain == type)) {
				changeTile(gTile, type);
			}
		}
		// Building
		else if (selected instanceof Integer) {
			Integer type = (Integer) selected;
			// Handle castle
			int controller = -1;
			if (type == Building.TYPE_CASTLE) {
				if (mapEd.nextPlayerToRecieveCastle == Player.PLAYER_1_ID) {
					controller = Player.PLAYER_1_ID;
					mapEd.nextPlayerToRecieveCastle = Player.PLAYER_2_ID;
				}	
				else if (mapEd.nextPlayerToRecieveCastle == Player.PLAYER_2_ID) {
					controller = Player.PLAYER_2_ID;
					mapEd.nextPlayerToRecieveCastle = Player.PLAYER_1_ID;
				}
				else 
					return;
				if (mapEd.map.getPlayersCastle(controller) != null && 
						mapEd.map.buildingList.containsKey(mapEd.map.getPlayersCastle(controller).id)) {
					GTile tile = mapEd.gMap.getTile(mapEd.map.getPlayersCastle(controller).tileCoordinate);
					tile.tile.terrain = TerrainType.FIELD;
					tile.setTexture(TerrainType.FIELD);
					mapEd.gBuildings.remove(mapEd.map.getPlayersCastle(controller).id);
					mapEd.map.buildingList.remove(mapEd.map.getPlayersCastle(controller).id);
				}
			}
			
			Building existing = mapEd.map.getBuildingByTile(gTile.tile.coordinates);
			if(existing != null){
				// Change owner if we try to place identical building on tile
				if(type==existing.type){
					changeOwnerPopup(gTile);
					return;
				}
				
				// Remove old building
				mapEd.gBuildings.remove(existing.id);
			}
				
			int id = mapEd.map.nextBuildingId++;
			Building b = new Building(type, id, controller);
				
			mapEd.map.setBuilding(gTile.tile.coordinates, b);
			mapEd.gBuildings.put(b.id, new GBuilding(b,mapEd.gMap));
		}
	}
	
	/**
	 * Shows panel for changing owner of the building occupying specified tile.
	 */
	private void changeOwnerPopup(GTile tile){
		Building building = MapEditor.getInstance().map.getBuildingByTile(tile.tile.coordinates);
		if(building!=null){
			selectedTile = tile;
			MapEditorUI ui = MapEditor.getInstance().userInterface;
			int owner = building.controllerId;
			
			for(int i=0; i<2; i++){
				TextButton b = (TextButton)ui.tblChangeOwner.findActor(String.valueOf(i));
				b.setText(owner==i?("[ "+i+" ]"):String.valueOf(i));
			}
			TextButton none = (TextButton)ui.tblChangeOwner.findActor("none");
			none.setText(owner==-1?"[ none ]":"none");
			Assets.SKIN.setEnabled(none, building.type!=Building.TYPE_CASTLE);
			ui.showChangeOwner();
		}
	}
	
	/**
	 * Changes owner of a building. 
	 */
	public void changeOwner(String newOwner){
		Building b = MapEditor.getInstance().map.getBuildingByTile(selectedTile.tile.coordinates);
		if(newOwner=="none" && b.type == Building.TYPE_CASTLE)
			return;
		b.controllerId = newOwner=="none"?-1:Integer.parseInt(newOwner);
	}

	/**
	 * Creates a new map of specified sizes.
	 * @return Whether the size was valid or not. 
	 */
	public boolean createNewMap(int width, int height){
		
		int validCode = Map.isValidSize(width);
		String errorMsg = "";
		
		switch(validCode)
		{
			case 0:
				validCode = Map.isValidSize(height);
				switch(validCode)
				{
					case 0:
						return true;
					case -1:
						errorMsg = "Map height cannot be less than "+Map.MIN_SIZE+".";
						break;
					case 1:
						errorMsg = "Map height cannot be larger than "+Map.MAX_SIZE+".";
						break;
				}
				break;
			case -1:
				errorMsg = "Map width cannot be less than "+Map.MIN_SIZE+".";
				break;
			case 1:
				errorMsg = "Map width cannot be larger than "+Map.MAX_SIZE+".";
				break;
		}
		
		Popup.showGenericPopup("Invalid size", errorMsg);
		return false;
	}
	
	/**
	 * Clears map
	 */
	public void clearMap(){
		MapEditor maped = MapEditor.getInstance();
    	maped.initMap(new Map(maped.map.getXSize(),maped.map.getYSize()));
	}
}
