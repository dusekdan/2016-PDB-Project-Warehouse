package cz.vutbr.fit.pdb.teamincredible.pdb.model;


import oracle.ord.im.*;

/**
 *
 * @author Dan
 */
public class Good {



    // GOODS_ID
    private int id;
    public void setId(int newId) { id = newId; }
    public int getId() { return id; }

    // GOODS_VOLUMNE
    private double volume;
    public void setVolume(double newVolume) { volume = newVolume; }
    public double getVolume() { return volume; }

    // GOODS_NAME
    private String name;
    public void setName(String newName) { name = newName; }
    public String getName() { return name; }

    // GOODS_PHOTO
    private OrdImage photo;
    public void setPhoto(OrdImage photo) { this.photo = photo;}
    public OrdImage getPhoto() { return photo; }

    // These fields should be retrieved and work with only on Database level
    // GOODS_PHOTO_SI
    // GOODS_PHOTO_AC
    // GOODS_PHOTO_CH
    // GOODS_PHOTO_PC
    // GOODS_PHOTO_TX

    // GOODS_PRICE
    private double price;
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }


    // Auxiliary variable for inserting ORDSys image
    private String imgFilePath;
    public String getImgFilePath() { return imgFilePath; }
    public void setImgFilePath(String imgFilePath) { this.imgFilePath = imgFilePath;}

    public boolean UpdateGood()
    {
        // Insert new record
        if (this.id < 1)
        {
            String insertStatement = "INSERT INTO GOODS (volume, name, photo) VALUES ()";
        }
        // Update existing
        else
        {

        }

        return false;
    }

    public Good() {
    }

    public Good(String name, double volume, String imgFilePath, double price)
    {
        this.name = name;
        this.volume = volume;
        this.imgFilePath = imgFilePath;
        this.price = price;
    }






}
