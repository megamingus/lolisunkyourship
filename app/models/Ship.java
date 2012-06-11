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



    private String[] previwPositions;

    private int size;
    private String name;
    private boolean sunk;
    private int hits;
    private int currentSize;



    public void setHeader(String header) {
        this.header = header;
    }

    private String header;


    public Ship(int size,String name){
        this.size=size;
        this.name=name;
        positions = new String[size];
        hits = 0;
        sunk=false;
        currentSize=0;
    }
    public void resetPositions(){
        currentSize=0;
        positions=  new String[size];
    }
    public boolean isReady(){
       return size==currentSize&&currentSize==positions.length;
    }
    public void addPosition(String tile){
        if(currentSize<size){
            positions[currentSize++]=tile;
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
    public int getCurrentSize() {
           return currentSize;
       }

       public void setCurrentSize(int currentSize) {
           this.currentSize = currentSize;
       }

       public String getHeader() {
           return header;
       }

    public String[] getPreviwPositions() {
        return previwPositions;
    }

    public void setPreviwPositions(String[] previwPositions) {
        this.previwPositions = previwPositions;
    }
}
