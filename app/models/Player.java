package models;

import org.codehaus.jackson.JsonNode;
import play.mvc.WebSocket;

import java.util.Map;

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
    boolean myTurn;
    Board board;
    Player enemy=null;

    public Player(String username, WebSocket.Out<JsonNode> channel,boolean myTurn) {
        this.username = username;
        this.channel = channel;
        this.myTurn  =myTurn;
        board=new Board();
    }
    public void changeTurn(){
        myTurn=!myTurn;
    }

    public void knowYourEnemy( Map<String, Player>  members){
        //esto es horrible... pero bue, tengo sueño =)
        for(Player player:members.values()){
            if(!player.equals(this)){
                this.enemy=player;
            }
        }

    }
   public String attack(String tile){
      return enemy.shoot(tile);
   }

    public String shoot (String tile){
      switch(board.shoot(tile)){
          case ALREADY_SHOT:return "Captain, we already shot "+tile;
          case HIT: return "Bull's eye Captain!";
          case SUNK: return "We send them straigt to Hell my Captain!";
          default:return " missed!";
      }
    }
}
