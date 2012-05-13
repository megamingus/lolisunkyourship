package models;

import org.codehaus.jackson.JsonNode;
import play.mvc.WebSocket;

import java.util.ArrayList;
import java.util.List;
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
    List<Ship> ships;
    List<String> previousShots;

    public Player(String username, WebSocket.Out<JsonNode> channel,boolean myTurn) {
        this.username = username;
        this.channel = channel;
        this.myTurn  =myTurn;
        board=new Board();
        ships = createShips();
        previousShots = new ArrayList<String>();
    }

    private List<Ship> createShips() {
        List<Ship> shipList = new ArrayList<Ship>(5);
        shipList.add(new Ship(2,"Patrol boat"));
        shipList.add(new Ship(3,"Destroyer"));
        shipList.add(new Ship(3,"Submarine"));
        shipList.add(new Ship(4,"Battleship"));
        shipList.add(new Ship(5,"Aircraft carrier"));
        return shipList;
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
       previousShots.add(tile);
       System.out.println(previousShots.get(previousShots.size()-1));
       System.out.println(previousShots.size());
       return enemy.shoot(tile);
   }

    public String enemyResult(String tile){
        switch(board.shoot(tile)){
                  case ALREADY_SHOT:return "The dogs shot the same spot again!";
                  case HIT: return "Arghh! We got hit!";
                  case SUNK: return "Abandon ship!!!";
                  case WATER:return "Hurrah!! They missed!";
                  default: return "Something went wrong in Player.shoot!";
              }
    }

    public String shoot (String tile){
      switch(board.shoot(tile)){
          case ALREADY_SHOT:return "Captain, we already shot "+tile;
          case HIT: return "Bull's eye Captain!";
          case SUNK: return "We sent them straight to Hell my Captain!";
          case WATER:return "We missed!";
          default: return "Something went wrong in Player.shoot!";
      }
    }


}
