package models;

import play.mvc.*;
import play.libs.*;
import play.libs.F.*;

import akka.util.*;
import akka.actor.*;
import akka.dispatch.*;
import static akka.pattern.Patterns.ask;

import org.codehaus.jackson.*;
import org.codehaus.jackson.node.*;

import java.util.*;

import static java.util.concurrent.TimeUnit.*;

/**
 * A room is an Actor.
 */
public class BattleRoom extends UntypedActor {
    
    // Default room.
    static ActorRef defaultRoom = Akka.system().actorOf(new Props(BattleRoom.class));
    
    // Create a Robot, just for fun.
    static {
     //   new Robot(defaultRoom);
    }
    
    /**
     * Join the default room.
     */
    public static void join(final String username, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) throws Exception{
        
        // Send the Join message to the room
        String result = (String)Await.result(ask(defaultRoom,new Join(username, out), 1000), Duration.create(1, SECONDS));
        
        if("OK".equals(result)) {
            
            // For each event received on the socket,
            in.onMessage(new Callback<JsonNode>() {
               public void invoke(JsonNode event) {
                   // Send a UserAction message to the room.
                   if(event.has("attack")){
                       defaultRoom.tell(new UserAction(username,UserAction.Action.ATTACK, event.get("attack").asText()));
                   }
                   if(event.has("text")){
                      defaultRoom.tell(new UserAction(username, UserAction.Action.CHAT ,event.get("text").asText()));
                   }
                   
               } 
            });
            
            // When the socket is closed.
            in.onClose(new Callback0() {
               public void invoke() {
                   
                   // Send a Quit message to the room.
                   defaultRoom.tell(new Quit(username));
                   
               }
            });
            
        } else {
            
            // Cannot connect, create a Json error.
            ObjectNode error = Json.newObject();
            error.put("error", result);
            
            // Send the error to the socket.
            out.write(error);
            
        }
        
    }
    
    // Members of this room.
    //Map<String, WebSocket.Out<JsonNode>> members = new HashMap<String WebSocket.Out<JsonNode>>();
    Map<String, Player> members = new HashMap<String,Player>();// WebSocket.Out<JsonNode>>();
    
    public void onReceive(Object message) throws Exception {
        
        if(message instanceof Join) {
            
            // Received a Join message
            Join join = (Join)message;

               // Check if there are 2 players already
            if( members.size()<2){
                // Check if this username is free.
                if(members.containsKey(join.username)) {
                    getSender().tell("This username is already used");
                } else {
                    members.put(join.username, new Player(join.username,join.channel,members.size()==1?true:false));
                    if(members.size()==2){
                         for(Player player:members.values()){
                             player.knowYourEnemy(members);
                         }
                    }
                    notifyAll("join", join.username, "has entered the room");
                    getSender().tell("OK");
                }
            } else{
                getSender().tell("There are 2 players already, sorry");
            }
            
        } else if(message instanceof UserAction)  {
            // Received a UserAction message
            UserAction userAction = (UserAction)message;
            if (userAction.action.equals(UserAction.Action.ATTACK)) {
                if(members.size()==2){
                    if(members.get(userAction.username).myTurn){
                        //habria que evaluar primero el disparo y dps avisar por separado a cada  uno lo qeu paso,
                        // y ver si cambiar o no el turno si es que ya disparo en ese lugar o no... pero por ahroa va... (es todo agua)
                        notifyAll("attack", userAction.username,"attacked "+userAction.text);
                        notifyPlayer("info", userAction.username, "Commander", "We " + members.get(userAction.username).attack(userAction.text),
                                "{\"tile\":\""+userAction.text+"\",\"state\":\"miss\"}");//deberia sacar water de lo qeu me de el ataque
                        notifyPlayer("info",members.get(userAction.username).enemy.username,"Commander","They "+members.get(userAction.username).attack(userAction.text),"");

                        for(Player user:members.values()){
                            user.changeTurn();
                        }
                    }else{
                        notifyPlayer("error",userAction.username,"Commander","we are still preparing the torpedos, my Captain.","");
                    }
                } else{
                    notifyPlayer("error",userAction.username,"Commander","Sr. no foes are shown in the radars.","");
                }
            }  else{
                if(userAction.text != null && !userAction.text.trim().equals(""))
                    notifyAll("talk", userAction.username, userAction.text);
            }
            
        } else if(message instanceof Quit)  {
            
            // Received a Quit message
            Quit quit = (Quit)message;
            
            members.remove(quit.username);
            
            notifyAll("quit", quit.username, "has leaved the room");
        
        } else {
            unhandled(message);
        }
        
    }

    public void notifyPlayer(String kind,String user,String from, String text,String json){

        WebSocket.Out<JsonNode> channel= members.get(user).channel;
        ObjectNode event = Json.newObject();
        event.put("kind", kind);
        event.put("user", from);
        event.put("message", text);
        event.put("data", json);

        ArrayNode m = event.putArray("members");
        for(String u: members.keySet()) {
            m.add(u);
        }

        channel.write(event);
    }

    // Send a Json event to all members
    public void notifyAll(String kind, String user, String text) {
        for(Player player: members.values()) {
            WebSocket.Out<JsonNode> channel= player.channel;
            ObjectNode event = Json.newObject();
            event.put("kind", kind);
            event.put("user", user);
            event.put("message", text);
            
            ArrayNode m = event.putArray("members");
            for(String u: members.keySet()) {
                m.add(u);
            }
            
            channel.write(event);
        }
    }
    
    // -- Messages
    
    public static class Join {
        
        final String username;
        final WebSocket.Out<JsonNode> channel;
        
        public Join(String username, WebSocket.Out<JsonNode> channel) {
            this.username = username;
            this.channel = channel;
        }
        
    }
    
    public static class UserAction {

        public enum Action { CHAT, ATTACK};

        final String username;
        final String text;
        final Action action;
        
        public UserAction(String username,Action action, String text) {
            this.username = username;
            this.action=action;
            this.text = text;
        }

        
    }
    
    public static class Quit {
        
        final String username;
        
        public Quit(String username) {
            this.username = username;
        }
        
    }
    
}
