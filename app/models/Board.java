package models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Mingus
 * Date: 07/05/12
 * Time: 05:54
 * To change this template use File | Settings | File Templates.
 */
public class Board {

    Map<String,Tile> board;

    public Board(){
         board =new HashMap<String,Tile>();

    }

    public ShootResults shoot(String tile){
        try{
            Tile myTile=board.get(tile);

            if(myTile.shot){
                return ShootResults.ALREADY_SHOT;
            }
        }catch (Exception e){

        }

        return ShootResults.WATER;
    }
}
