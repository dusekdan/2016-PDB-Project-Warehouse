package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.StoreActivityRecord;
import java.io.IOException;
import javafx.fxml.Initializable;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Sucha on 03.12.2016.
 */
public class GoodsController implements Initializable {
    
    
    @FXML
    private TableView<StoreActivityRecord> goodsTableView;
    
    @FXML private TableColumn<StoreActivityRecord, String> goodsID;
    @FXML private TableColumn<StoreActivityRecord, String> stockID;
    @FXML private TableColumn<StoreActivityRecord, String> count;
    @FXML private TableColumn<StoreActivityRecord, String> valid_from;
    @FXML private TableColumn<StoreActivityRecord, String> valid_to;
    @FXML private TableColumn<StoreActivityRecord, String> detail;
    
    @FXML private Button todayBTN;
    @FXML private Button yesterdayBTN;
    @FXML private DatePicker showDateDP;
    
    @FXML
    public void showTodayChanges() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0  );
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        goodsTableView.setItems(StoreActivityRecord.loadData(today));
    }
    
    @FXML
    public void showYesterdayChanges() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0  );
        yesterday.set(Calendar.SECOND, 0);
        yesterday.set(Calendar.MILLISECOND, 0);
        yesterday.add(Calendar.DATE, -1);
          goodsTableView.setItems(
                StoreActivityRecord.loadData(yesterday));
    }
    @FXML
    public void selectedDayChanges() {
        LocalDate date = showDateDP.getValue();
        // magic: http://www.java2s.com/Tutorials/Java/Data_Type_How_to/Date_Convert/Convert_LocalDate_to_a_java_sql_TimeStamp.htm
        Date tmp = Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Calendar day = Calendar.getInstance();
        day.setTime(tmp);
         goodsTableView.setItems(
                StoreActivityRecord.loadData(day));
    }
    
    @FXML
    public void showChart() {
           try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/historyChart.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("GRAF");
            stage.setScene(new Scene(root1));  
            stage.show();
          } catch (IOException ex) {
            Logger.getLogger(GoodsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("GoodsController init");
        
        goodsID.setCellValueFactory(new PropertyValueFactory<>("goodID") );
        stockID.setCellValueFactory(new PropertyValueFactory<>("stockID") );
        count.setCellValueFactory(new PropertyValueFactory<>("count") );
        valid_from.setCellValueFactory(new PropertyValueFactory<>("from") );
        valid_to.setCellValueFactory(new PropertyValueFactory<>("to") );
        
      //  DatabaseD.InsertGoodIntoStorage(1, 1, 1000);
      //  DatabaseD.RemoveGoodFromStorage(1, 1, 500);
        StoreActivityRecord.loadData(StoreActivityRecord.currentDay);
        goodsTableView.setItems(StoreActivityRecord.getData());
        
        
    }
}
