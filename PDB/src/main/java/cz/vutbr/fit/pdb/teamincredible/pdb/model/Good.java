package cz.vutbr.fit.pdb.teamincredible.pdb.model;
import javafx.scene.image.Image;
import oracle.ord.im.*;

/**
 * @author Dan
 * This class describes a model of GOODS entity record in database
 */
public class Good
{

    // GOODS_ID
    private int id;

    public void setId(int newId) { id = newId; }

    public int getId() { return id; }

    // GOODS_VOLUME
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

    // Property containing real image data that can be directly rendered to scene
    private Image realImageData;

    public Image getRealImageData() { return realImageData; }

    public void setRealImageData(Image imageFromStream) { realImageData = imageFromStream; }

    /**
     * Constructs Good object instance on given parameters
     *
     * @param name        String name of the good
     * @param volume      Double volume of the good
     * @param imgFilePath String path to image file of the good
     * @param price       Double price of the good
     */
    public Good(String name, double volume, String imgFilePath, double price) {
        this.name = name;
        this.volume = volume;
        this.imgFilePath = imgFilePath;
        this.price = price;
    }

    /**
     * Empty constructor, creates tabula-rasa instance of Good object
     */
    public Good() {
    }
}
