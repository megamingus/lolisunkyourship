package controllers;

import play.mvc.*;

import org.codehaus.jackson.*;

import views.html.*;

import models.*;

public class Application extends Controller {
  
    /**
     * Display the home page.
     */
    public static Result index() {
        return ok(index.render());
    }
  
    /**
     * Display the battle room.
     */
    public static Result battleRoom(String username) {


        if(username == null || username.trim().equals("")) {
            flash("error", "Please choose a valid username.");
            return redirect(routes.Application.index());
        }
        return ok(battleRoom.render(username));
    }
    
    /**
     * Handle the battle websocket.
     */
    public static WebSocket<JsonNode> battle(final String username) {
        return new WebSocket<JsonNode>() {
            
            // Called when the Websocket Handshake is done.
            public void onReady(In<JsonNode> in, Out<JsonNode> out){
                
                // Join the battle room.
                try { 
                    BattleRoom.join(username, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }
  
}
