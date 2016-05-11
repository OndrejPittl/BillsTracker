package cz.ondrejpittl.semestralka.models;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class Currency {

    /**
     * ID of a currency record in DB.
     */
    private int ID;

    /**
     * Name of a currency.
     */
    private String name;

    /**
     * Constructor.
     */
    public Currency() {}

    /**
     * Getter of the currency ID.
     * @return  the currency ID.
     */
    public int getID() {
        return ID;
    }

    /**
     * Setter of the currency ID.
     * @param ID    currency ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Getter of the currency name.
     * @return  the currency name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of the currency name.
     * @param name    currency name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return this.getName();
    }
}
