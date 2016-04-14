package cz.ondrejpittl.semestralka.models;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class Category {

    private int ID;

    private String name;

    private String icon;


    public Category() {}

    public Category(int ID, String name, String icon) {
        this.ID = ID;
        this.name = name;
        this.icon = icon;
    }

    public Category(int ID, String name) {
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String toString(){
        return this.getName();
    }

}
