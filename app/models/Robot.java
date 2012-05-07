package models;

import play.*;
import play.mvc.*;
import play.libs.*;

import akka.util.*;
import akka.actor.*;

import org.codehaus.jackson.*;

import static java.util.concurrent.TimeUnit.*;

public class Robot {
    
    public Robot(ActorRef chatRoom) {
        
        // Create a Fake socket out for the robot that log events to the console.
        WebSocket.Out<JsonNode> robotChannel = new WebSocket.Out<JsonNode>() {
            
            public void write(JsonNode frame) {
                Logger.of("robot").info(Json.stringify(frame));
            }
            
            public void close() {}
            
        };
        
        // Join the room
        chatRoom.tell(new BattleRoom.Join("Robot", robotChannel));
        
        // Make the robot talk every 30 seconds
        Akka.system().scheduler().schedule(
            Duration.create(30, SECONDS),
            Duration.create(30, SECONDS),
            chatRoom,
            new BattleRoom.UserAction("Robot", BattleRoom.UserAction.Action.CHAT, "I'm still alive")
        );
        
    }
    
}