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
    //static ActorRef defaultRoom = Akka.system().actorOf(new Props(BattleRoom.class));
    // Create a Robot, just for fun.
    static {
     //   new Robot(defaultRoom);
    }
    
    /**
     * Join the default room.
     */
    public static void join(final ActorRef defaultRoom, final String username, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) throws Exception{
        
        // Send the Join message to the room
        String result = (String)Await.result(ask(defaultRoom,new Join(username, out), 1000), Duration.create(1, SECONDS));
        
        if("OK".equals(result)) {
            
            // For each event received on the socket,
            in.onMessage(new Callback<JsonNode>() {
               public void invoke(JsonNode event) {
                   // Send a UserAction message to the room.
                   if(event.has("attack")){
                       defaultRoom.tell(new UserAction(username,UserAction.Action.ATTACK, event.get("attack").asText(),event));
                   }
                   if(event.has("text")){
                      defaultRoom.tell(new UserAction(username, UserAction.Action.CHAT ,event.get("text").asText(),event));
                   }
                   if(event.has("resetPosition")){
                       defaultRoom.tell(new UserAction(username, UserAction.Action.RESET_SHIP ,event.get("resetPosition").asText(),event));
                       System.out.println("Resetear posiciones del barco "+event.get("resetPosition").asText()+"");
                   }
                   if(event.has("ship")){
                       defaultRoom.tell(new UserAction(username, UserAction.Action.POSSITION_SHIP ,event.get("ship").asText(),event));
                       System.out.println("Posicionar un barco "+event.get("ship").asText()+" en tile "+event.get("tile").asText());
                   }
                   if(event.has("ready")){
                       defaultRoom.tell(new UserAction(username, UserAction.Action.READY ,event.get("ready").asText(),event));
                       System.out.println("ready");
                   }
               }
            });
            
            // When the socket is closed.
            in.onClose(new Callback0() {
               public void invoke() {
                   
                   // Send a Quit message to the room.
                   defaultRoom.tell(new Quit(username,defaultRoom));
                   
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
                    members.put(join.username, new Player(join.username,join.channel, members.size() == 1));
                    if(members.size()==2){
                         for(Player player:members.values()){
                             player.knowYourEnemy(members);
                         }
                    }
                    notifyAll("join", join.username, "has entered the room");
                    // Send strategy
                   // notifyPlayer("strategy",join.username,"Commander","The fleet is positioned",Json.toJson(members.get(join.username).getShipPositions()));
                    getSender().tell("OK");
                }
            } else{
                getSender().tell("There are 2 players already, sorry");
            }
            
        } else if(message instanceof UserAction)  {
            // Received a UserAction message
            UserAction userAction = (UserAction)message;
            switch (userAction.action){
                case ATTACK:{
                    if(members.size()==2){
                        if(members.get(userAction.username).myTurn){
                            communicateResult(members.get(userAction.username).attack(userAction.text),userAction.username,userAction.text);
                            for(Player user:members.values()){
                                user.changeTurn();
                            }
                        }else{
                            notifyPlayer("error",userAction.username,"Commander","We are still preparing the torpedos, my Captain.",Json.toJson(""));
                        }
                    } else{
                        notifyPlayer("error",userAction.username,"Commander","Sir, no foes are shown on the radars.",Json.toJson(""));
                    }
                }   break;
                case READY:{
                    notifyPlayer("strategy",userAction.username,"Commander","The fleet is positioned",Json.toJson(members.get(userAction.username).getShipPositions()));
                    if(members.get(userAction.username).isReady()){
                        //ready
                        System.out.println("player ready ");
                    } else{
                        // algo fallo
                        System.out.println("algo fallo");
                    }
                } break;
                case RESET_SHIP:{
                     if(!members.get(userAction.username).isReady2Play()){
                         members.get(userAction.username).getShipByName(userAction.text).resetPositions();
                     }
                } break;
                case POSSITION_SHIP:{
                    if(!members.get(userAction.username).isReady2Play()){
                        //todo modificar el metodo addPosition (o cambiarlo) para que le llegue 1 tile y una orientacion  y sabiendo cada bote cuandto deberia medir genere las posiciones
                        members.get(userAction.username).getShipByName(userAction.text).addPosition(userAction.json.get("tile").asText());
                    }

                } break;
                default:{
                    notifyAll("talk", userAction.username, userAction.text);
                }
            }
        /*    if (userAction.action.equals(UserAction.Action.ATTACK)) {
                if(members.size()==2){
                    if(members.get(userAction.username).myTurn){
                        communicateResult(members.get(userAction.username).attack(userAction.text),userAction.username,userAction.text);
                        for(Player user:members.values()){
                            user.changeTurn();
                        }
                    }else{
                        notifyPlayer("error",userAction.username,"Commander","We are still preparing the torpedos, my Captain.",Json.toJson(""));
                    }
                } else{
                    notifyPlayer("error",userAction.username,"Commander","Sir, no foes are shown on the radars.",Json.toJson(""));
                }
            }if (userAction.action.equals(UserAction.Action.READY)) {
                notifyPlayer("strategy",userAction.username,"Commander","The fleet is positioned",Json.toJson(members.get(userAction.username).getShipPositions()));
            }else{
                if(userAction.text != null && !userAction.text.trim().equals(""))
                    notifyAll("talk", userAction.username, userAction.text);
            }     */
            
        } else if(message instanceof Quit)  {
            
            // Received a Quit message
            Quit quit = (Quit)message;
            
            members.remove(quit.username);
            
            notifyAll("quit", quit.username, "has leaved the room");
            if(members.size()<=0){
                ChatRoom.stop(quit.actor);
            }
        
        } else {
            unhandled(message);
        }
        
    }

    public void communicateResult(ShootResults result,String player,String tile){

         switch (result){
            case ALREADY_SHOT:notifyResult(player,tile,"Captain, we already shot "+tile,
                       "The grogged monkeys shot the same spot again!",Json.toJson(new msg(tile,"miss",members.get(player).enemy.username))); break;
            case HIT: notifyResult(player,tile,"Bull's eye Captain!","Arrrgh! We got hit!",Json.toJson(new msg(tile,"hit",members.get(player).enemy.username))); break;
            case SUNK: {
                notifyResult(player,tile,"We sent them straight to Hell my Captain!","Abandon ship!!!",Json.toJson(new SunkMsg(tile,"sunk",members.get(player).enemy.username,members.get(player).enemy.getShip(tile).getPositions())));
            } break;
            case WATER: notifyResult(player,tile,"We missed!","Hurrah!! They missed!",Json.toJson(new msg(tile,"miss",members.get(player).enemy.username))); break;
            case LOST_GAME: notifyResult(player,tile,"Hurrah!! VICTORY!!!","Our fleet has been defeated...Its back to scrubbing the decks for you Captain!",Json.toJson(new msg(tile,"win",members.get(player).enemy.username))); break;
            default: notifyAll("Error",player,"Something went wrong! Garrrrr"); break;
        }

    }

    public void notifyResult(String player,String messageAll,String messagePlayer1,String messagePlayer2,JsonNode json){
        notifyAll("attack", player,"attacked "+messageAll);
        //Notify the player
        notifyPlayer("info", player, "Commander", messagePlayer1,json);
        //Notify the enemy
        notifyPlayer("info",members.get(player).enemy.username,"Commander",messagePlayer2,json);
    }

    public void notifyPlayer(String kind,String user,String from, String text,JsonNode json){

        WebSocket.Out<JsonNode> channel= members.get(user).channel;
        ObjectNode event = Json.newObject();
        event.put("kind", kind);
        event.put("user", from);
        event.put("message", text);
        event.put("data", json.toString());

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

        public enum Action { CHAT, ATTACK,RESET_SHIP, POSSITION_SHIP,READY, PLAY};

        final String username;
        final String text;
        final Action action;
        final JsonNode json;
        
        public UserAction(String username,Action action, String text,JsonNode json) {
            this.username = username;
            this.action=action;
            this.text = text;
            this.json=json;
        }

        
    }
    public static class msg {
          public String tile;
        public String state;
        public  String board;
           //String text;

             public msg(String tile,String state,String board) {
                 this.tile = tile;
                 this.state=state;
                 this.board=board;
             //    this.text = text;
             }


        }

    public static class SunkMsg {
            public String tile;
           public String state;
           public  String board;
              //String text;
            public String[] shipPositions;

                public SunkMsg(String tile,String state,String board,String[] shipPositions) {
                    this.tile = tile;
                    this.state=state;
                    this.board=board;
                //    this.text = text;
                    this.shipPositions=shipPositions;
                }


           }





    public static class Quit {
        
        final String username;
        final ActorRef actor;
        
        public Quit(String username,ActorRef actor) {
            this.username = username;
            this.actor=actor;
        }
        
    }
    
}
