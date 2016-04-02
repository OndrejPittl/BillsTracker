package cz.ondrejpittl.semestralka.models;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class Store {

    private int ID;

    private String name;

    public Store(){}

    public Store(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        //return "STORE - ID: " + this.getID() + ", NAME:" + this.getName();
        return this.getName();
    }
}
