package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Menu to handle login, upgrade, register and logout 
 * @author Ekis
 *
 */
public class AccountMenu extends MenuScreen {
	private static AccountMenu me;
	
	public Popup loginPopup, registerPopup, connectingPopup;
	
	private AccountMenu(){
		super();
		
		loginPopup = createLoginPopup();
		registerPopup = createRegisterPopup();
		connectingPopup = createConnectingPopup();
		
		contents.register( "fastLoginButton",ComponentFactory.createMenuButton("Quick Login",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	quickPlay();
            }
        }));

		contents.register( "loginButton",ComponentFactory.createMenuButton("Login",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	loginPopup.show();
            }
        }));

        TextButton registerButton = ComponentFactory.createMenuButton("Register",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Popup.showGenericPopup("Register", "Please use Quick Login.");
            	//AccountMenu.registerPopup.show();
            }
        });
        
        Assets.SKIN.setEnabled(registerButton, false);
        
        contents.register( "registerButton", registerButton);

        contents.register("debugMenuButton", ComponentFactory.createMenuButton("Debug",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(DebugMenu.getInstance());
            }
        }));
        
        contents.register( "login", ComponentFactory.createLoginLabel());
        
        contents.padTop((int) (3*Main.percentHeight));
        contents.parse( 	"* spacing:"+(int)(2*Main.percentWidth)+" padding:0 align:top width:"+BUTTON_WIDTH+" height:"+BUTTON_HEIGHT+
    					"[fastLoginButton]"+
    					"---"+
    					"[loginButton]"+
    					"---"+
    					"[registerButton]"+
    					"---"+
    					"[debugMenuButton] expand:y"+
    					"---"+
    					"{*align:center [login]}");
	}
	
	public static AccountMenu getInstance() {
		if(me == null){
			me = new AccountMenu();
		}
		return me;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}

	public void clearInputs() {
		registerPopup.clearInputs();
		loginPopup.clearInputs();
	}
	
	/**
	 * Starts a Quick Play game
	 */
	public void quickPlay(){
		if(!Main.getInstance().network.isLoggedIn())
			Main.getInstance().network.quickLogin();
		else
			Popup.showGenericPopup("Quick login refused", "Already logged in.");
	}
	
	private Popup createLoginPopup(){
		Label message = new Label("Please enter your username and password.", Assets.SKIN);
		
		final TextField loginUserNameField = ComponentFactory.createTextField("Login", false);
		final TextField loginPasswordField = ComponentFactory.createTextField("Password", true);
		
		Button cancel = ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		Button okButton = ComponentFactory.createButton("Ok", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				AccountMenu am = AccountMenu.getInstance();
				String username = loginUserNameField.getText();
				String password = loginPasswordField.getText();
				
				am.clearInputs();
				
				Main.getInstance().login(username, password);
				
				Popup.currentPopup.close();
				am.clearInputs();
				am.connectingPopup.show();
			}
		});
		
		return new Popup("Login", message, loginUserNameField, loginPasswordField, new Row(okButton, cancel));
	}
	
	private Popup createRegisterPopup(){
		final TextField usernameField = ComponentFactory.createTextField("Login", false);
		final TextField passwordField = ComponentFactory.createTextField("Password", true);
		final TextField displayNameField = ComponentFactory.createTextField("Displayed name", false);
		
		Button cancelButton = ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		Button okButton = ComponentFactory.createButton("Ok", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				AccountMenu am = AccountMenu.getInstance();
				String username = usernameField.getText();
				String password = passwordField.getText();
				String displayed = displayNameField.getText();
				
				am.clearInputs();
				
				System.out.println("Sending register request "+username+":"+password+":"+displayed);
				Main.getInstance().network.register(username, password, displayed);
				
				Popup.currentPopup.close();
				am.clearInputs();
				am.connectingPopup.show();
			}
		});
		
		return new Popup("Register",
				new Label("Please enter your desired username, password and displayed name.", Assets.SKIN),
				usernameField,
				passwordField,
				displayNameField,
				new Row(okButton, cancelButton));
	}
	
	private Popup createConnectingPopup(){
		Label message = new Label("Connecting...", Assets.SKIN);
		Button cancelButton = ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		return new Popup("Connecting", message, cancelButton);
	}
}