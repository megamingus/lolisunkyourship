package models;

import akka.actor.ActorRef;
import akka.actor.Props;

import akka.actor.UntypedActor;
import akka.dispatch.Await;
import akka.util.Duration;
import controllers.routes;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;


//import static models.BattleRoom.*;

import java.util.*;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A chat room is an Actor.
 */
public class  ChatRoom extends UntypedActor{

   // ActorRef defaultRoom = Akka.system().actorOf(new Props(BattleRoom.class));

    public static Map<String, ActorRef> games = new HashMap<String,ActorRef>();// WebSocket.Out<JsonNode>>();


    public static void stop(ActorRef ref){
       for(String key:games.keySet()){
           if(games.get(key).equals(ref)) {
              stop(key);
               break;
           }
       }
    }
    public static void stop(String key){
        ActorRef actor=games.get(key);
        games.remove(key);
        Akka.system().stop(actor);

    }


    public static  ActorRef getBattleRoom(String key){
        key=(null==key ||"".equals(key)||"null".equals(key))?generateRandomKey():key;
        if( !games.containsKey(key)){
            ActorRef battleRoom = Akka.system().actorOf(new Props(BattleRoom.class));
            games.put(key,battleRoom);
            return battleRoom;
        }else{
            return games.get(key);
        }
    }


  /*  public static ActorRef getNextEmptyRoom(){
        for(ActorRef a:games.values()){
           a.path();
            return a;
        }
    } */

    public static String generateRandomKey(){

        return generateString(new Random(),"abcdefghijklmnopqrstuvwxyz1234567890",10);
    }
    public static String generateString(Random rng, String characters, int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }


    // Default room.
    static ActorRef defaultRoom = Akka.system().actorOf(new Props(ChatRoom.class));

    // Create a Robot, just for fun.
    /*static {
        new Robot(defaultRoom);
    } */

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

                    if(event.has("text")){
                        defaultRoom.tell(new UserAction(username,UserAction.Action.CHAT, event.get("text").asText()));
                    }
                    if(event.has("play")){
                        defaultRoom.tell(new UserAction(username,UserAction.Action.PLAY, event.get("play").asText()));
                    }
                    /*
                    if(event.has("attack")){
                       defaultRoom.tell(new UserAction(username,UserAction.Action.ATTACK, event.get("attack").asText()));
                   }
                   if(event.has("text")){
                      defaultRoom.tell(new UserAction(username, UserAction.Action.CHAT ,event.get("text").asText()));
                   }
                     */

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


    Map<String, WebSocket.Out<JsonNode>> members = new HashMap<String, WebSocket.Out<JsonNode>>();
    Set<String> waiting2Play =new HashSet<String>();

    public void onReceive(Object message) throws Exception {

        if(message instanceof Join) {

            // Received a Join message
            Join join = (Join)message;

            // Check if this username is free.
            if(members.containsKey(join.username)) {
                getSender().tell("This username is already used");
            } else {
                members.put(join.username, join.channel);
                notifyAll("join", join.username, "has entered the room");
                getSender().tell("OK");
            }

        } else if(message instanceof UserAction)  {

            // Received a UserAction message
            UserAction userAction = (UserAction)message;
            if (userAction.action.equals(UserAction.Action.CHAT)) {
                notifyAll("talk", userAction.username, userAction.text);
            }else if (userAction.action.equals(UserAction.Action.PLAY)){
                waiting2Play.add(userAction.username);
                if(waiting2Play.size()>=2){
                    Iterator<String> it=waiting2Play.iterator();
                   String player1= it.next();
                   String player2=it.next();
                    waiting2Play.remove(player1);
                    waiting2Play.remove(player2);
                    String key=generateRandomKey();
                    notifyPlayer("connect",player1,player2,"Connecting...",Json.toJson(new Msg(routes.Application.battleRoom(player1,key).toString(),key,player1)));
                    notifyPlayer("connect",player2,player1,"Connecting...",Json.toJson(new Msg(routes.Application.battleRoom(player2,key).toString(),key,player2)));
                }else{
                    notifyPlayer("wait",userAction.username,"WarRoom","Waiting for other players...",Json.toJson(""));
                }

            }

        } else if(message instanceof Quit)  {

            // Received a Quit message
            Quit quit = (Quit)message;

            members.remove(quit.username);
            waiting2Play.remove(quit.username);

            notifyAll("quit", quit.username, "has leaved the room");

        } else {
            unhandled(message);
        }

    }
    public static class Msg {
        public String url;
        public String key;
        public  String username;
        // String text;

        public Msg(String url,String key,String username) {
            this.url=url;
            this.key = key;
            this.username=username;
            //    this.text = text;
        }


    }


    public void notifyPlayer(String kind,String user,String from, String text,JsonNode json){

        WebSocket.Out<JsonNode> channel= members.get(user);
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
        for(WebSocket.Out<JsonNode> channel: members.values()) {

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

    public static class UserAction {

        public enum Action { CHAT, ATTACK,PLAY};

        final String username;
        final String text;
        final Action action;

        public UserAction(String username,Action action, String text) {
            this.username = username;
            this.action=action;
            this.text = text;
        }


    }
    public static class Join {

        final String username;
        final WebSocket.Out<JsonNode> channel;

        public Join(String username, WebSocket.Out<JsonNode> channel) {
            this.username = username;
            this.channel = channel;
        }

    }

    public static class Quit {

        final String username;

        public Quit(String username) {
            this.username = username;
        }

    }
}
