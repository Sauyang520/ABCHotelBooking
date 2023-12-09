// Programmer Name: Mr. Sim Sau Yang
// Program Name: Report Page Controller
// First Written on: 20 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.bean.BookingDetails;
import com.hotelbooking.utils.DataHolder;
import com.hotelbooking.utils.HotelBookingUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ReportPageController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportPageController.class);
    @FXML
    private TableView<BookingDetails> tableViewReport;

    @FXML
    private TableColumn<BookingDetails, String> columnId, columnName;

    @FXML
    private TableColumn<BookingDetails, Double> columnService, columnSubtotal, columnTotal, columnTourism;

    @FXML
    private Label labelReportGenerated;

    @FXML
    private Label labelTotal;
    private LocalDateTime reportGenerateTime;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<BookingDetails> bookingDetails = DataHolder.getInstance().getBookingDetailsListData();

        reportGenerateTime = LocalDateTime.now();
        labelReportGenerated.setText(dateTimeFormatter.format(reportGenerateTime));

        // Set up tableView
        columnId.setCellValueFactory(new PropertyValueFactory<>("receiptId"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        columnSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        columnService.setCellValueFactory(new PropertyValueFactory<>("serviceTax"));
        columnTourism.setCellValueFactory(new PropertyValueFactory<>("tourismTax"));
        columnTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        BigDecimal totalReport = BigDecimal.valueOf(0);
        ObservableList<BookingDetails> observableList = tableViewReport.getItems();
        for (BookingDetails bookingDetail : bookingDetails) {
            observableList.add(bookingDetail);
            tableViewReport.setItems(observableList);
            totalReport = totalReport.add(BigDecimal.valueOf(bookingDetail.getTotalPrice()));
        }

        labelTotal.setText(totalReport.toString());
        LOGGER.trace("Report Data is set.");
        LOGGER.info("Total amount calculated: " + totalReport);

        Stage stage = DataHolder.getInstance().getStageData();
        stage.setOnCloseRequest(event -> {
            LOGGER.debug("'X' Cancel Button is clicked.");
            DataHolder.deleteInstance();
            event.consume();
            saveNotice(stage);
        });
    }

    @FXML
    void save(ActionEvent event) {
        LOGGER.debug("Save Button is clicked.");
        String filename = "src/main/resources/ABCHotelInfo/report/" + dateTimeFormatter.format(reportGenerateTime).replace(":", ".") + ".png";
        HotelBookingUtil.savePNGAndClose(filename, event, LOGGER);
    }

    public void saveNotice(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Report");
        alert.setHeaderText("Your report is not saved!");
        alert.setContentText("Click OK to close.\nClick Cancel -> Save to save receipt.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            LOGGER.debug("'OK' Button is clicked.");
            DataHolder.deleteInstance();
            stage.close();
            LOGGER.trace("Close Report Page.");
        }
    }
}
