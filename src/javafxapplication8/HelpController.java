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
public class HelpController implements Initializable {
    @FXML
    private Text text;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        text.setText("Tools -\n" +
        "	Hand: Does nothing, allows clicks on the canvas.\n" +
        "	Pencil: Draws free hand lines. Hold mouse down to use.\n" +
        "	Line: Draws Straight Lines between two point. Click and drag to use.\n" +
        "	Dropper: Click anywhere on canvas to set both colors to the selection.\n" +
        "	Eraser: Click and drag to erase. Works best with larger line size.\n" +
        "	Text: When selected Text menu will appear. Select to type text and select font size.\n" +
        "       Drag anywhere to place. In the fill menu Checking the Fill sets the text \n" +
        "       color to the Fill color. Otherwise Line color is used.\n" +
        "       Select: Drag to make selection on image. Drag image to reposition. When in the\n"+
        "	desired spot press the 'Place Selection' button.\n" +
        "	\n" +
        "Shapes -\n" +
        "	All shapes are created by dragging in any direction and releasing. \n" +
        "	Fill menu will have the shapes filled or just outlined. \n" +
        "	If fill is not selected Line size will affect shape outlines.\n" +
        "\n" +
        "Colors - \n" +
        "	Clicking on the color square will bring up a color selection menu.\n" +
        "\n" +
        "File -\n" +
        "	Save as and open file will open up a system dialog. File type is automatically \n" +
        "	set to JPG. Save will save without dialog. Quit exits the program.\n" +
        "\n" +
        "Edit -\n" +
        "	Undo: Reverts back to the picture before the latest drawing.\n" +
        "	Redo: Puts any undone drawings back into the canvas.\n" +
        "\n" +
        "View - \n" +
        "	Zoom In: Increases the scale of the canvas.\n" +
        "	Zoom Out: Decreases the scale of the canvas.\n" +
        "	Zoom Reset: Puts the Scale back at 1 for the canvas.\n" +
        "\n" +
        "Help - \n" +
        "	About: Brings up window with the patch notes.\n" +
        "	Help: Opens this window."); 
    }    
    
}
