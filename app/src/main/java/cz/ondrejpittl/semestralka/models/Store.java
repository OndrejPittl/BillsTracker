package cz.ondrejpittl.semestralka.models;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class Store {

    /**
     * ID of a store.
     */
    private int ID;

    /**
     * Name of a store.
     */
    private String name;

    /**
     * Constructor.
     */
    public Store(){}

    /**
     * Constructor. Basics initialization.
     * @param ID    store id
     * @param name  store name
     */
    public Store(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    /**
     * Getter of the ID of a store.
     * @return  store id
     */
    public int getID() {
        return ID;
    }

    /**
     * Setter of the store ID.
     * @param ID    the store id
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Getter of the name of a store.
     * @return  store name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of the store name.
     * @param name    the store id
     */
    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        //return "STORE - ID: " + this.getID() + ", NAME:" + this.getName();
        return this.getName();
    }
}
