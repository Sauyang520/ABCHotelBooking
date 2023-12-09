// Programmer Name: Mr. Sim Sau Yang
// Program Name: HotelBookingUtil Class
// First Written on: 15 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.utils;

import com.hotelbooking.bean.BookingDetails;
import com.hotelbooking.bean.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The util class for ABC Hotel Booking System
 */
public final class HotelBookingUtil {

    private HotelBookingUtil() {
    }

    /**
     * Search for the existing file. If found, return true, else false.
     *
     * @param fileName the file to be searched
     */
    public static boolean searchFile(String fileName) {
        File dir = new File("src/main/resources/ABCHotelInfo/data");
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals(fileName)) {
                        // Found
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Read the data of the file and insert into an array.
     *
     * @param filename the filename to collect the data
     */
    public static List<String> collectData(String filename) {
        List<String> data = new ArrayList<>();
        if (searchFile(filename)) {
            try (
                    Reader reader = new FileReader("src/main/resources/ABCHotelInfo/data/" + filename);
                    BufferedReader bufferedReader = new BufferedReader(reader)
            ) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    data.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * To split the data read from the file and put it into a list
     *
     * @param dirtyData the string value read from each line of the file
     */
    public static List<String> splitData(String dirtyData, String splitter) {
        String[] firstCleanedData = dirtyData.split(splitter);
        return new ArrayList<>(Arrays.asList(firstCleanedData));
    }

    /**
     * Encapsulate the String value in an array to be ready to written into File
     *
     * @param arr The prepared data to be written
     */
    public static String encapsulate(List<String> arr, String splitter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            stringBuilder.append(arr.get(i)).append(i == arr.size() - 1 ? "" : splitter);
        }
        return stringBuilder.toString();
    }

    /**
     * Add data into file
     *
     * @param filename the file the data to be added into
     * @param data     the encapsulated string data
     * @param append   to overwrite it or to add only the data
     */
    public static void addData(String filename, String data, Boolean append) {
        try (
                Writer writer = new FileWriter("src/main/resources/ABCHotelInfo/data/" + filename, append);
                BufferedWriter bufferedWriter = new BufferedWriter(writer)
        ) {
            bufferedWriter.write(data);
            bufferedWriter.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Since the SwingFXUtils is no more usable after JDK 11 / 12 / 8... idk (JDK I use is 19)
     * Try to get a snapshot of scene and copied pixel by pixel to form an image
     *
     * @param scene    the scene of the Receipt
     * @param fileName the path to save the receipt
     */
    public static void saveSceneToPNG(Scene scene, String fileName) throws IOException {
        // Create a WritableImage object with the size of the scene
        WritableImage image = new WritableImage((int) scene.getWidth(), (int) scene.getHeight());

        // Set up the SnapshotParameters to capture the scene without any transformations
        // Render the scene onto the WritableImage
        scene.snapshot(image);

        // Create a BufferedImage from the WritableImage
        BufferedImage bufferedImage = new BufferedImage(
                (int) image.getWidth(),
                (int) image.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                bufferedImage.setRGB(x, y, image.getPixelReader().getArgb(x, y));
            }
        }

        // Create a file object with the specified file name
        File file = new File(fileName);

        // Write the image data to the file as a PNG image
        ImageIO.write(bufferedImage, "png", file);
    }

    /**
     * Save the png and close the current stage
     * @param filename the png name
     * @param event event
     * @param LOGGER logger
     */
    public static void savePNGAndClose(String filename, ActionEvent event, Logger LOGGER){
        Scene scene = ((Node) event.getSource()).getScene();
        try {
            HotelBookingUtil.saveSceneToPNG(scene, filename);
            LOGGER.info("Scene saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Failed to save scene to " + filename + " : " + e);
        }

        DataHolder.deleteInstance();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        LOGGER.trace("Close Receipt Page.");
    }

    /**
     * Create Receipt Page
     *
     * @param bookingDetails the booking details passed to create receipt
     * @param currentClass   the current page to run the create receipt page
     */
    public static void createReceipt(BookingDetails bookingDetails, Class<?> currentClass) throws IOException {
        // Create New Receipt Window
        FXMLLoader receiptLoader = new FXMLLoader();
        receiptLoader.setLocation(currentClass.getResource("/com.hotelbooking.gui/ReceiptPage.fxml"));
        DataHolder.getInstance().setBookingDetailsData(bookingDetails);
        Stage receiptstage = new Stage();
        DataHolder.getInstance().setStageData(receiptstage);
        Scene receiptScene = new Scene(receiptLoader.load());
        receiptstage.setScene(receiptScene);
        receiptstage.setTitle("Receipt");
        Image icon = new Image("/ABCHotelInfo/picture/HotelLogo.png");
        receiptstage.getIcons().add(icon);
        receiptstage.show();
        receiptstage.setResizable(false);
    }

    /**
     * Collect customer Data
     */
    public static void collectCustomers(List<Customer> customerDetails) {
        List<String> customerData = collectData("Customer.txt");
        if (customerData.size() > 0) {
            customerData.forEach(s -> {
                List<String> cleanedCData = HotelBookingUtil.splitData(s, "#");
                customerDetails.add(new Customer(cleanedCData.get(0), cleanedCData.get(1), cleanedCData.get(2)
                        , cleanedCData.get(3), cleanedCData.get(4)));
            });
        }
    }

    /**
     * Go to main page
     * @param event event
     * @param LOGGER Logger
     * @param currentClass current controller class
     * @throws IOException IOException
     */
    public static void goToMainPage(ActionEvent event, Logger LOGGER, Class<?> currentClass) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(currentClass.getResource("/com.hotelbooking.gui/MainPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("ABC Hotel Booking");
        stage.show();
        LOGGER.trace("Back to Main Page.");
    }
}