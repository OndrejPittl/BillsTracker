package cz.ondrejpittl.semestralka.models;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class Category {

    /**
     * Category ID.
     */
    private int ID;

    /**
     * Category name.
     */
    private String name;

    /**
     * Category icon path.
     */
    private String icon;

    /**
     * Constructor.
     */
    public Category() {}

    /**
     * Construcotr. Basics initialization.
     * @param ID    a category id
     * @param name  a category name
     * @param icon  a category icon
     */
    public Category(int ID, String name, String icon) {
        this.ID = ID;
        this.name = name;
        this.icon = icon;
    }

    /**
     * Construcotr. Basics initialization.
     * @param ID    a category id
     * @param name  a category name
     */
    public Category(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    /**
     * Getter od a category id.
     * @return  id of a category
     */
    public int getID() {
        return ID;
    }

    /**
     * Setter of a category id.
     * @param ID    id of a category
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Getter od a category name.
     * @return  name of a category
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of a category name.
     * @param name    name of a category
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter od a category icon.
     * @return  icon of a category
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Setter of a category icon.
     * @param icon    icon of a category
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String toString(){
        return this.getName();
    }
}
