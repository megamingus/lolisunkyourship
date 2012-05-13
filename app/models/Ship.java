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

    public Ship(int size,String name){
          this.size=size;
        this.name=name;
          positions = new String[size];
    }

}
