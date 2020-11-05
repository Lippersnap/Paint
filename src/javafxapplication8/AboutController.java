/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication8;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author sammy
 */
public class AboutController implements Initializable {

    @FXML
    private Text text;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        text.setText("Paint  0.0.6 - 10/07/20\n" +
        "\n" +
        "New Features:\n" +
        "	Save Timer\n" +
        "	Logging (just to system out)\n" +
        "	Tool Tips\n" +
        "	7 Tabs\n" +
        "\n" +
        "Known Issues:\n" +
        "	First shape drawn does not always behave (Ellipse tear drop)	\n" +
        "	Colors do not change on tab switch\n" +
        "	Tabs do not open dynamically\n" +
        "	\n" +
        "Expected Next Time:\n" +
        "	Polish on UI and code\n" +
        "\n" +
        "Links:\n" +
        "	https://github.com/Lippersnap/Paint");
    } 
}
