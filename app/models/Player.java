package models;

import org.codehaus.jackson.JsonNode;
import play.mvc.WebSocket;

/**
 * Created with IntelliJ IDEA.
 * User: Mingus
 * Date: 06/05/12
 * Time: 21:06
 * To change this template use File | Settings | File Templates.
 */
public class Player {
    final String username;
    final WebSocket.Out<JsonNode> channel;

    public Player(String username, WebSocket.Out<JsonNode> channel) {
        this.username = username;
        this.channel = channel;
    }
}
