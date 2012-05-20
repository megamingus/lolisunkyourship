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
    final int shipNumber = 5;
    final String username;
    final WebSocket.Out<JsonNode> channel;
    boolean myTurn;
    Player enemy=null;
    List<Ship> ships;
    List<String> previousShots;
    List<String> shipPositions;
    int sunkenShips;

    public Player(String username, WebSocket.Out<JsonNode> channel,boolean myTurn) {
        this.username = username;
        this.channel = channel;
        this.myTurn  =myTurn;
        ships = createShips();
        previousShots = new ArrayList<String>();
        sunkenShips = 0;
        defaultStrategy();
        createPositionList();

    }



    private List<Ship> createShips() {
        List<Ship> shipList = new ArrayList<Ship>(shipNumber);
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
   public ShootResults attack(String tile){
       ShootResults result = enemy.shoot(tile,previousShots);
       previousShots.add(tile);
       return result;
   }




    public ShootResults shoot(String tile,List<String> previousShots){
        for(String shot:previousShots){
            if(shot.equals(tile)){
                return ShootResults.ALREADY_SHOT;
            }
        }

        for(Ship ship:ships){
            if(!ship.getSunk()){
                for(int j=0;j<ship.getPositions().length;j++){
                     if(ship.getPositions()[j].equals(tile)){
                        ship.setHits(ship.getHits()+1);
                         System.out.println("hits: "+ship.getHits()+" size:"+ship.getSize());
                         if(ship.getHits()==ship.getSize()){
                             ship.setSunk(true);
                             sunkenShips+=1;
                             if(sunkenShips==shipNumber){
                                return ShootResults.LOST_GAME;
                             }
                             return ShootResults.SUNK;
                         }
                         return ShootResults.HIT;
                     }

                }
            }

        }

        return ShootResults.WATER;

    }

   /*La lista tiene que tener los arrays en orden, del barco mas chico al barco mas grande*/
   public void addShipPositions(List<String[]> shipPositions){
       for(int i=0; i<shipPositions.size();i++){
            ships.get(i).setPositions(shipPositions.get(i));
        }

   }

    public void defaultStrategy(){
        List<String[]>strategy = new ArrayList<String[]>(shipNumber);

        String[] s1 = new String[2];
        s1[0]="A1";
        s1[1]="A2";
        strategy.add(s1);

        String[] s2 = new String[3];
        s2[0]="B3";
        s2[1]="C3";
        s2[2]="D3";
        strategy.add(s2);

        String[] s3 = new String[3];
        s3[0]="G6";
        s3[1]="G7";
        s3[2]="G8";
        strategy.add(s3);

        String[] s4 = new String[4];
        s4[0]="A5";
        s4[1]="B5";
        s4[2]="C5";
        s4[3]="D5";
         strategy.add(s4);

        String[] s5 = new String[5];
        s5[0]="J5";
        s5[1]="J6";
        s5[2]="J7";
        s5[3]="J8";
        s5[4]="J9";
        strategy.add(s5);

        addShipPositions(strategy);
    }


    private void createPositionList() {
        shipPositions=new ArrayList<String>();
        for(Ship ship : ships){
            for(String position : ship.getPositions()){
                shipPositions.add(position);
            }
        }
       }

    public List<String> getShipPositions(){
        return shipPositions;
    }

    public Ship getShip(String tile){
        for(Ship ship : ships){
            for (String position : ship.getPositions()){
                if(position.equals(tile)){
                    return ship;
                }
            }
        }
        return null;
    }

}
