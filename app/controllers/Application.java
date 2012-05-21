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
    public static Result battleRoom(String username,String key) {


        if(username == null || username.trim().equals("")) {
            flash("error", "Please choose a valid username.");
            return redirect(routes.Application.index());
        }
        if("null".equals(key)||key == null || key.trim().equals("")) {
            key=ChatRoom.generateRandomKey();
        }
        return ok(battleRoom.render(username,key));
    }
    
    /**
     * Handle the battle websocket.
     */
    public static WebSocket<JsonNode> battle(final String key,final String username) {
        return new WebSocket<JsonNode>() {
            // Called when the Websocket Handshake is done.
            public void onReady(In<JsonNode> in, Out<JsonNode> out){
                // Join the battle room.
                try { 
                    BattleRoom.join(ChatRoom.getBattleRoom(key),username, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    /**
     * Display the chat room.
     */
    public static Result chatRoom(String username) {
        if(username == null || username.trim().equals("")) {
            flash("error", "Please choose a valid username.");
            return redirect(routes.Application.index());
        }
        return ok(chatRoom.render(username));
    }

    /**
     * Handle the chat websocket.
     */
    public static WebSocket<JsonNode> chat(final String username) {
        return new WebSocket<JsonNode>() {

            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){

                // Join the chat room.
                try {
                    ChatRoom.join(username, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }
  
}
