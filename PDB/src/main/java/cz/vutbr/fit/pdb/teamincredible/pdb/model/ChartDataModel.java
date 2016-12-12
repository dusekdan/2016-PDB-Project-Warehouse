package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import java.sql.Timestamp;
import java.util.List;

/**
 * Class represents chart data
 * @author Sucha
 */
public class ChartDataModel {
  public static ChartDataModel data = null;
  /**
   * Time on X-axis
   */
  public Timestamp time;
 // public int totalStoreCapacity;
  /**
   * goods in rack for time
   */
  public List<GoodInRack> goods;
  /**
   * construct instance of class, filled from DB
   * @param time
   * @param goods 
   */
    public ChartDataModel(Timestamp time, List<GoodInRack> goods) {
        this.time = time;
        this.goods = goods;
    }
  /**
   * request load from db
   * @param time 
   * @return loaded data from current time
   */
  public static ChartDataModel loadData(Timestamp time) {
      data = DatabaseD.getGraphData(time);
      
      return data;
  }
  
}
