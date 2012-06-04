package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Mary Anne
 * Date: 5/13/12
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Ship {

    private String[] positions;


    private int size;
    private String name;
    private boolean sunk;
    private int hits;
    private int currentSize;

    public Ship(int size,String name){
         this.size=size;
        this.name=name;
         positions = new String[size];
        hits = 0;
        sunk=false;
        currentSize=0;
    }
    public void resetPositions(){
        positions=  new String[size];
    }
    public boolean isReady(){
       return size==currentSize&&currentSize==positions.length;
    }
    public void addPosition(String tile){
        //TODO verificar que efectivamente sea una tile que corresponda
        if(currentSize<size){
            positions[currentSize++]=tile;
        }
    }


    public void addPosition(String tile,String orientation){

            //TODO verificar que efectivamente sea una tile que corresponda
            if(currentSize<size){

                String letter = tile.substring(0,1);
                int number = Integer.parseInt(tile.substring(1));

                if(orientation.equals("true")){
                   for(int i=currentSize;i<size;i++){
                          positions[currentSize]=letter+number;
                          System.out.println(positions[currentSize]);
                          currentSize++;
                          number++;
                      }
                } else{
                    String[] letters={"A","B","C","D","E","F","G","H","I","J"};
                    int letterIndex=0;
                    for(int j=0;j<letters.length;j++){
                        if(letters[j].equalsIgnoreCase(letter)){
                             letterIndex=j;
                            break;
                        }

                    }
                    System.out.println(letters[letterIndex]);

                    for(int k=currentSize;k<size;k++){
                        positions[currentSize]=letters[letterIndex]+number;
                        System.out.println(positions[currentSize]);
                        currentSize++;
                        letterIndex++;

                    }
                }

                System.out.println(orientation);
            }
        }

    public String[] getPositions() {
         return positions;
     }

     public void setPositions(String[] positionArray) {
         positions=positionArray;
     }


    public int getHits() {
        return hits;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void setHits(int hits) {
            this.hits = hits;
        }

    public boolean getSunk() {
           return sunk;
       }

    public void setSunk(boolean sunk) {
           this.sunk = sunk;
     }
}
