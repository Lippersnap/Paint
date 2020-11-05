/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication8;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.abs;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import static javafx.scene.text.Font.font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author sammy
 */
public class PrimaryController implements Initializable {

    //Initialize Variables
    Boolean shapeFill, textFill, hand, pencil, line, dropper, square, rectangle, ellipse, circle, text, eraser, roundRectangle, polygon, select, savedOnce, firstUndo, firstRedo, selected;
    Double oldMouseX, oldMouseY, scaleX, scaleY;
    File fileIn = null;
    FileChooser fileChooser = new FileChooser();
    GraphicsContext gc;
    GraphicsContext tempGraphicsContext;
    String fileType;
    String tool, filename, toLog;
    Thread main = Thread.currentThread();
    WritableImage drawingImage = null;
    Integer minutes = 5;
    Integer tens = 0;
    Integer ones = 0;
    Integer tabIndex = 0;
    Timeline saveTimer, logTimer;
    static final Logger logger = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
    Tooltip toolsList = new Tooltip("Select Tool");
    Tooltip shapesList = new Tooltip("Select Shape");
    Tooltip lineWidth = new Tooltip("Options for Line Width");
    Tooltip fillOptions = new Tooltip("Fill Options for Shape and Text");

    List<Stack<WritableImage>> undo = new ArrayList<>();
    List<Stack<WritableImage>> redo = new ArrayList<>();
    List<Canvas> canvas = new ArrayList<>();
    List<Canvas> tempCanvas = new ArrayList<>();

    //Import UI elements from FXML document
    @FXML
    private Canvas canvas0;

    @FXML
    private Canvas tempCanvas0;

    @FXML
    private Canvas canvas1;

    @FXML
    private Canvas tempCanvas1;

    @FXML
    private Canvas canvas2;

    @FXML
    private Canvas tempCanvas2;

    @FXML
    private Canvas canvas3;

    @FXML
    private Canvas tempCanvas3;

    @FXML
    private Canvas canvas4;

    @FXML
    private Canvas tempCanvas4;

    @FXML
    private Canvas canvas5;

    @FXML
    private Canvas tempCanvas5;

    @FXML
    private Canvas canvas6;

    @FXML
    private Canvas tempCanvas6;

    @FXML
    private TextField brushSizeText;

    @FXML
    private TextField textSizeText;

    @FXML
    private TextField polygonSides;

    @FXML
    private Slider brushSizeSlider;

    @FXML
    private Slider textSizeSlider;

    @FXML
    private ColorPicker lineColorPicker;

    @FXML
    private ColorPicker fillColorPicker;

    @FXML
    private CheckMenuItem setShapeFill;

    @FXML
    private CheckMenuItem setTextFill;

    @FXML
    private Label lineColorPickerLabel;

    @FXML
    private Label fillColorPickerLabel;

    @FXML
    private Label canvasXCoordinate;

    @FXML
    private Label canvasYCoordinate;

    @FXML
    private Label timerMinutes;

    @FXML
    private Label timerTens;

    @FXML
    private Label timerOnes;

    @FXML
    private Button selectedButton;

    @FXML
    private MenuButton toolBarToolSelect;

    @FXML
    private MenuButton toolBarShapeSelect;

    @FXML
    private MenuButton toolBarTextOptions;

    @FXML
    private MenuButton toolBarPolygonOptions;

    @FXML
    private MenuButton toolBarlineWidthOptions;

    @FXML
    private MenuButton toolBarFillOptions;

    @FXML
    private TextArea toolBarTextArea;

    @FXML
    private StackPane stackPane;

    @FXML
    private TabPane tabPane;

    //Event Listeners
    /**
     * Creates an open dialog, converts the file to an image and draws the image
     * to the canvas. Then renames the Tab.
     *
     * @param event
     */
    @FXML
    private void openFile(ActionEvent event) {
        fileChooser.setTitle("Open Image");
        fileIn = fileChooser.showOpenDialog(null);
        fileChooser.setInitialFileName(fileIn.getName());

        if (fileIn != null) {
            Image image = new Image(fileIn.toURI().toString());
            canvas.get(tabIndex).setHeight(image.getHeight());
            canvas.get(tabIndex).setWidth(image.getWidth());
            gc.drawImage(image, 0, 0);
        }
        savedOnce = false;
        WritableImage image = null;
        undo.get(tabIndex).add(canvas.get(tabIndex).snapshot(null, image));

        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        tab.setText(fileIn.getName() + " ");
    }

    /**
     * If no save has been done, Will open a save dialog and save a snapshot of
     * the canvas. Otherwise it will save the file to the existing file path
     * without a dialog. Then renames the Tab to the file name.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void saveFile(ActionEvent event) throws IOException {
        WritableImage toSave = new WritableImage((int) stackPane.getWidth(), (int) stackPane.getHeight());
        stackPane.snapshot(null, toSave);
        BufferedImage bImage = SwingFXUtils.fromFXImage(toSave, null);
        if (savedOnce == false) {
            fileChooser.setTitle("Save Image");
            fileIn = fileChooser.showSaveDialog(null);
            savedOnce = true;
        }
        ImageIO.write(bImage, "png", fileIn);

        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        tab.setText(fileIn.getName() + " ");
    }

    /**
     * Pretends the picture has never been saved and calls the saveFile
     * function.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void saveAsFile(ActionEvent event) throws IOException {
        savedOnce = false;
        saveFile(event);
    }

    /**
     * Terminates the program when the Quit Menu Item is pressed.
     *
     * @param event
     */
    @FXML
    private void menuQuit(ActionEvent event) {    //Event whent the quit button on the menu is pushed
        saveTimer.stop();
        logTimer.stop();
        Platform.exit();
    }

    /**
     * Creates a new window with a guide for the tools and capabilities of the
     * program when the Help Menu Item is pressed.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void menuHelp(ActionEvent event) throws IOException {   //Open a new window from helpWindow.FXML
        openNewWindow("help");
    }

    /**
     * Creates a new window with the patch notes when the About Menu Item is
     * pressed.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void menuAbout(ActionEvent event) throws IOException {   //Open a new window from helpWindow.FXML
        openNewWindow("about");
    }

    /**
     * Runs when the mouse is pressed on the canvas.
     *
     * @param evt
     */
    @FXML
    private void canvasMousePressed(MouseEvent evt) { //Event from mouse button being pushed down
        double x = evt.getX();
        double y = evt.getY();
        drawingImage = undo.get(tabIndex).pop();
        undo.get(tabIndex).add(drawingImage);

        if (pencil == true) {
            gc.moveTo(x, y);
            gc.beginPath();
        } else if (line == true) {
            gc.moveTo(x, y);
            gc.beginPath();
        } else if (hand == true) {

        } else if (dropper == true) {
            PixelReader pr = drawingImage.getPixelReader();
            Color color = pr.getColor((int) x, (int) y);
            lineColorPicker.setValue(color);
            fillColorPicker.setValue(color);
            gc.setStroke(color);
            gc.setFill(color);
        } else if (select == true) {
            tempCanvas.get(tabIndex).setLayoutX(x);
            tempCanvas.get(tabIndex).setLayoutY(y);
        }

        oldMouseX = x;
        oldMouseY = y;
    }

    /**
     * Runs when the mouse is dragged on the canvas.
     *
     * @param evt
     */
    @FXML
    private void canvasMouseDragged(MouseEvent evt) { //Event when mouse is pushed down then dragged
        double x, y;
        x = evt.getX();
        y = evt.getY();

        if (pencil == true) {
            gc.lineTo(x, y);
            gc.stroke();
        } else if (hand == true) {

        } else if (line == true) {
            gc.drawImage(drawingImage, 0, 0);
            gc.moveTo(oldMouseX, oldMouseY);
            gc.lineTo(x, y);
            gc.stroke();
            gc.closePath();
        } else if (eraser == true) {
            gc.clearRect(oldMouseX, oldMouseY, gc.getLineWidth(), gc.getLineWidth());
            oldMouseX = x;
            oldMouseY = y;
        } else {
            draw(x, y);
        }
    }

    /**
     * Runs when the mouse is released on the canvas.
     *
     * @param evt
     */
    @FXML
    private void canvasMouseReleased(MouseEvent evt) { //Event when mouse button is released
        double x, y;
        x = evt.getX();
        y = evt.getY();

        if (pencil == true) {
            gc.closePath();
            } else if (line == true) {
            gc.lineTo(x, y);
            gc.stroke();
            gc.closePath();
        } else if (select == true && selected == false) {
            tempCanvas.get(tabIndex).setVisible(true);
            WritableImage image = new WritableImage((int) canvas.get(tabIndex).getWidth(), (int) canvas.get(tabIndex).getHeight());
            canvas.get(tabIndex).snapshot(null, image);
            gc.clearRect(oldMouseX, oldMouseY, x - oldMouseX, y - oldMouseY);
            tempCanvas.get(tabIndex).setWidth(x - oldMouseX);
            tempCanvas.get(tabIndex).setHeight(y - oldMouseY);
            tempGraphicsContext.drawImage(image, oldMouseX, oldMouseY, x - oldMouseX, y - oldMouseY, 0, 0, x - oldMouseX, y - oldMouseY);
            selected = true;
            selectedButton.setVisible(true);
        } else {
            draw(x, y);
        }

        WritableImage toStack = new WritableImage((int) canvas.get(tabIndex).getWidth(), (int) canvas.get(tabIndex).getHeight());
        canvas.get(tabIndex).snapshot(null, toStack);
        undo.get(tabIndex).add(toStack);
    }

    /**
     * For the Select Tool, move the canvas when the Selection is dragged.
     *
     * @param event
     */
    @FXML
    private void tempCanvasDragged(MouseEvent event) {
        double x = event.getSceneX();
        double y = event.getSceneY();

        tempCanvas.get(tabIndex).setLayoutX(x - 50);
        tempCanvas.get(tabIndex).setLayoutY(y - 30);
    }

    /**
     * For the Select Tool, Take the image selected and draw it on the main
     * canvas. Then change UI to before state.
     *
     * @param event
     */
    @FXML
    private void tempCanvasPlaced(ActionEvent event) {
        WritableImage image = new WritableImage((int) stackPane.getWidth(), (int) stackPane.getHeight());
        stackPane.snapshot(null, image);
        gc.drawImage(image, 0, 0);
        tempCanvas.get(tabIndex).setVisible(false);
        selected = false;
        selectedButton.setVisible(false);
        undo.get(tabIndex).add(image);
    }

    /**
     * Affects UI when changes are made to the Brush Size Text Box.
     *
     * @param event
     */
    @FXML
    private void toolBarBrushSizeTyped(ActionEvent event) { //Event when a brush size is typed in the text view on the toolbar
        double size = Integer.parseInt(brushSizeText.getText());
        gc.setLineWidth(size);
        brushSizeSlider.setValue(size);
        brushSizeText.setText(brushSizeText.getText());
    }

    /**
     * Affects UI when changes are made to the Brush Size Slider.
     *
     * @param event
     */
    @FXML
    private void toolBarBrushSizeSlider(MouseEvent event) { //Event when the slider is used for brush size in the toolbar
        double size = brushSizeSlider.getValue();
        brushSizeText.setText(String.valueOf(size));
        gc.setLineWidth(size);
    }

    /**
     * Affects UI when changes are made to the Text Size Text Box.
     *
     * @param event
     */
    @FXML
    private void toolBarTextSizeTyped(ActionEvent event) { //Event when a brush size is typed in the text view on the toolbar
        double size = Integer.parseInt(textSizeText.getText());
        gc.setFont(font(size));
        textSizeSlider.setValue(size);
        textSizeText.setText(textSizeText.getText());
    }

    /**
     * Affects UI when changes are made to the Text Size Slider.
     *
     * @param event
     */
    @FXML
    private void toolBarTextSizeSlider(MouseEvent event) { //Event when the slider is used for brush size in the toolbar
        double size = textSizeSlider.getValue();
        textSizeText.setText(String.valueOf(size));
        gc.setFont(font(size));
    }

    /**
     * Sets the menu item for Shape Fill to be selected.
     *
     * @param event
     */
    @FXML
    private void toolBarShapeFill(ActionEvent event) { //Event for fill button functionality
        shapeFill = setShapeFill.isSelected();
    }

    /**
     * Sets the menu item for Text Fill to be selected.
     *
     * @param event
     */
    @FXML
    private void toolBarTextFill(ActionEvent event) { //Event for fill button functionality
        textFill = setTextFill.isSelected();
    }

    /**
     * Sets UI for Hand tool.
     *
     * @param event
     */
    @FXML
    private void brushHand(ActionEvent event) {  //Event when Pencil brush is selected
        toolOrShapeSelected("Hand");
    }

    /**
     * Sets UI for Pencil tool.
     *
     * @param event
     */
    @FXML
    private void brushPencil(ActionEvent event) {  //Event when Pencil brush is selected
        toolOrShapeSelected("Pencil");
    }

    /**
     * Sets UI for Line tool.
     *
     * @param event
     */
    @FXML
    private void brushLine(ActionEvent event) {   //Event when Line brush is selected
        toolOrShapeSelected("Line");
    }

    /**
     * Sets UI for Dropper tool.
     *
     * @param event
     */
    @FXML
    private void brushDropper(ActionEvent event) {   //Event when Dropper brush is selected
        toolOrShapeSelected("Dropper");
    }

    /**
     * Sets UI for Square Shape.
     *
     * @param event
     */
    @FXML
    private void shapeSquare(ActionEvent event) {   //Event when Square shape is selected
        toolOrShapeSelected("Square");
    }

    /**
     * Sets UI for Rectangle Shape.
     *
     * @param event
     */
    @FXML
    private void shapeRectangle(ActionEvent event) {   //Event when Rectangle shape is selected
        toolOrShapeSelected("Rectangle");
    }

    /**
     * Sets UI for Ellipse Shape.
     *
     * @param event
     */
    @FXML
    private void shapeEllipse(ActionEvent event) {   //Event when Ellipse shape is selected
        toolOrShapeSelected("Ellipse");
    }

    /**
     * Sets UI for Circle Shape.
     *
     * @param event
     */
    @FXML
    private void shapeCircle(ActionEvent event) {   //Event when Circle shape is selected
        toolOrShapeSelected("Circle");
    }

    /**
     * Sets UI for Text tool.
     *
     * @param event
     */
    @FXML
    private void brushText(ActionEvent event) {  //Event when Pencil brush is selected
        toolOrShapeSelected("Text");
    }

    /**
     * Sets UI for Eraser tool.
     *
     * @param event
     */
    @FXML
    private void brushEraser(ActionEvent event) {  //Event when Pencil brush is selected
        toolOrShapeSelected("Eraser");
    }

    /**
     * Sets UI for Round Rectangle Shape.
     *
     * @param event
     */
    @FXML
    private void shapeRoundRectangle(ActionEvent event) {   //Event when Circle shape is selected
        toolOrShapeSelected("Round Rectangle");
    }

    /**
     * Sets UI for the Polygon Shape.
     *
     * @param event
     */
    @FXML
    private void shapePolygon(ActionEvent event) {
        toolOrShapeSelected("Polygon");
    }

    /**
     * Sets UI for Select Tool.
     *
     * @param event
     */
    @FXML
    private void brushSelect(ActionEvent event) {
        toolOrShapeSelected("Select");
    }

    /**
     * Scales canvas when Menu Item Zoom In is selected.
     *
     * @param event
     */
    @FXML
    private void menuZoomIn(ActionEvent event) {   //Event when zoom in is selected
        scaleX = scaleX + .25;
        scaleY = scaleY + .25;
        canvas.get(tabIndex).setScaleX(scaleX);
        canvas.get(tabIndex).setScaleY(scaleY);
    }

    /**
     * Scales canvas when Menu Item Zoom Out is selected.
     *
     * @param event
     */
    @FXML
    private void menuZoomOut(ActionEvent event) {   //Event when zoom out is selected
        scaleX = scaleX - .25;
        scaleY = scaleY - .25;
        canvas.get(tabIndex).setScaleX(scaleX);
        canvas.get(tabIndex).setScaleY(scaleY);
    }

    /**
     * Resets the canvas Scale to 1.0 when Menu Item Zoom Reset is selected.
     *
     * @param event
     */
    @FXML
    private void menuZoomReset(ActionEvent event) {
        scaleX = 1.0;
        scaleY = 1.0;
        canvas.get(tabIndex).setScaleX(scaleX);
        canvas.get(tabIndex).setScaleY(scaleY);
    }

    /**
     * Draws a previous version of the canvas on the canvas as an Undo.
     *
     * @param event
     */
    @FXML
    private void menuUndo(ActionEvent event) {
        WritableImage fromStack = undo.get(tabIndex).pop();
        if (firstUndo == true) {
            firstUndo = false;
            redo.get(tabIndex).add(fromStack);
            fromStack = undo.get(tabIndex).pop();
            gc.drawImage(fromStack, 0, 0);
            redo.get(tabIndex).add(fromStack);
        } else {
            gc.drawImage(fromStack, 0, 0);
            redo.get(tabIndex).add(fromStack);
        }
        firstRedo = true;
    }

    /**
     * Draws a previously undone version of the canvas on the canvas as a Redo.
     *
     * @param event
     */
    @FXML
    private void menuRedo(ActionEvent event) {
        WritableImage fromStack = redo.get(tabIndex).pop();
        if (firstRedo == true) {
            firstRedo = false;
            undo.get(tabIndex).add(fromStack);
            fromStack = redo.get(tabIndex).pop();
            gc.drawImage(fromStack, 0, 0);
            undo.get(tabIndex).add(fromStack);
        } else {
            gc.drawImage(fromStack, 0, 0);
            undo.get(tabIndex).add(fromStack);
        }
        firstUndo = true;
    }

    /**
     * Sets UI and Graphics Context when a new stroke color is chosen.
     *
     * @param event
     */
    @FXML
    private void lineColorPicked(ActionEvent event) {
        Color color = lineColorPicker.getValue();
        String name = colorLabelFinder(color);
        gc.setStroke(color);
        lineColorPickerLabel.setText(name);
    }

    /**
     * Sets UI and Graphics Context when a new fill color is chosen.
     *
     * @param event
     */
    @FXML
    private void fillColorPicked(ActionEvent event) {
        Color color = fillColorPicker.getValue();
        String name = colorLabelFinder(color);
        gc.setFill(color);
        fillColorPickerLabel.setText(name);
    }

    /**
     * Shows the coordinates of the Canvas the mouse is over.
     *
     * @param event
     */
    @FXML
    private void getCoordinates(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        canvasXCoordinate.setText(Integer.toString((int) x));
        canvasYCoordinate.setText(Integer.toString((int) y));
    }

    /**
     * Handles the switching of tabs getting child information of the tabs.
     *
     * @param event
     */
    @FXML
    private void switchTabs(Event event) {
        tabIndex = tabPane.getSelectionModel().getSelectedIndex();
        gc = canvas.get(tabIndex).getGraphicsContext2D();
        gc.setImageSmoothing(false);
        tempGraphicsContext = tempCanvas.get(tabIndex).getGraphicsContext2D();
        Color lineColor = (Color)gc.getStroke();
        Color fillColor = (Color)gc.getFill();
        lineColorPicker.setValue(lineColor);
        String lineName = colorLabelFinder(lineColor);
        lineColorPickerLabel.setText(lineName);
        fillColorPicker.setValue(fillColor);
        String fillName = colorLabelFinder(fillColor);
        fillColorPickerLabel.setText(fillName);
    }

    //Functions
    /**
     * Used to set the tool or shape and any extra UI it may need.
     *
     * @param selection
     */
    private void toolOrShapeSelected(String selection) {// Handles which tool or shape is selected and how the UI reacts
        hand = false;
        pencil = false;
        line = false;
        dropper = false;
        square = false;
        rectangle = false;
        ellipse = false;
        circle = false;
        text = false;
        eraser = false;
        polygon = false;
        select = false;
        roundRectangle = false;
        toolBarToolSelect.setText("Tools");
        toolBarShapeSelect.setText("Shapes");
        toolBarTextOptions.setVisible(false);
        toolBarPolygonOptions.setVisible(false);

        switch (selection) {
            case "Hand":
                hand = true;
                toolBarToolSelect.setText("Hand");
                break;
            case "Pencil":
                pencil = true;
                toolBarToolSelect.setText("Pencil");
                break;
            case "Line":
                line = true;
                toolBarToolSelect.setText("Line");
                break;
            case "Dropper":
                dropper = true;
                toolBarToolSelect.setText("Dropper");
                break;
            case "Text":
                text = true;
                toolBarToolSelect.setText("Text");
                toolBarTextOptions.setVisible(true);
                break;
            case "Square":
                square = true;
                toolBarShapeSelect.setText("Square");
                break;
            case "Rectangle":
                rectangle = true;
                toolBarShapeSelect.setText("Rectangle");
                break;
            case "Ellipse":
                ellipse = true;
                toolBarShapeSelect.setText("Ellipse");
                break;
            case "Circle":
                circle = true;
                toolBarShapeSelect.setText("Circle");
                break;
            case "Eraser":
                eraser = true;
                toolBarToolSelect.setText("Eraser");
                break;
            case "Round Rectangle":
                roundRectangle = true;
                toolBarShapeSelect.setText("Round Rectangle");
                break;
            case "Polygon":
                polygon = true;
                toolBarShapeSelect.setText("Polygon");
                toolBarPolygonOptions.setVisible(true);
                break;
            case "Select":
                select = true;
                toolBarToolSelect.setText("Select");
            default:
                break;
        }
    }

    /**
     * Compares a color to a list of all JavaFX provided colors and returns a
     * string of the name.
     *
     * @param color
     * @return
     */
    private String colorLabelFinder(Color color) { //Function to find the name of a picked color
        String colorString;
        String colorCompare;
        colorString = color.toString();
        colorCompare = colorString.substring(0, 8);

        if (null == colorCompare) {
            return (colorCompare);
        } else {
            switch (colorCompare) {  //Searches for a color name match and displays if one is found
                case "0xf0f8ff":
                    return ("AliceBlue");
                case "0xfaebd7":
                    return ("AntiqueWhite");

                case "0x7fffd4":
                    return ("Aquamarine");

                case "0xf0ffff":
                    return ("Azure");

                case "0xf5f5dc":
                    return ("Beige");

                case "0xffe4c4":
                    return ("Bisque");

                case "0x000000":
                    return ("Black");

                case "0xffebcd":
                    return ("BlanchedAlmond");

                case "0x0000ff":
                    return ("Blue");

                case "0x8a2be2":
                    return ("BlueViolet");

                case "0xa52a2a":
                    return ("Brown");

                case "0xdeb887":
                    return ("BurlyWood");

                case "0x5f9ea0":
                    return ("CadetBlue");

                case "0x7fff00":
                    return ("Chartreuse");

                case "0xd2691e":
                    return ("Chocolate");

                case "0xff7f50":
                    return ("Coral");

                case "0x6495ed":
                    return ("CornflowerBlue");

                case "0xfff8dc":
                    return ("Cornsilk");

                case "0xdc143c":
                    return ("Crimson");

                case "0x00ffff":
                    return ("Cyan");

                case "0x00008b":
                    return ("DarkBlue");

                case "0x008b8b":
                    return ("DarkCyan");

                case "0xb8860b":
                    return ("DarkGoldenRod");

                case "0xa9a9a9":
                    return ("DarkGray");

                case "0x006400":
                    return ("DarkGreen");

                case "0xbdb76b":
                    return ("DarkKhaki");

                case "0x8b008b":
                    return ("DarkMagenta");

                case "0x556b2f":
                    return ("DarkOliveGreen");

                case "0xff8c00":
                    return ("DarkOrange");

                case "0x9932cc":
                    return ("DarkOrchid");

                case "0x8b0000":
                    return ("DarkRed");

                case "0xe9967a":
                    return ("DarkSalmon");

                case "0x8fbc8f":
                    return ("DarkSeaGreen");

                case "0x483d8b":
                    return ("DarkSlateBlue");

                case "0x2f4f4f":
                    return ("DarkSlateGray");

                case "0x00ced1":
                    return ("DarkTurquoise");

                case "0x9400d3":
                    return ("DarkViolet");

                case "0xff1493":
                    return ("DeepPink");

                case "0x00bfff":
                    return ("DeepSkyBlue");

                case "0x696969":
                    return ("DimGray");

                case "0x1e90ff":
                    return ("DodgerBlue");

                case "0xb22222":
                    return ("FireBrick");

                case "0xfffaf0":
                    return ("FloralWhite");

                case "0x228b22":
                    return ("ForestGreen");

                case "0xdcdcdc":
                    return ("Gainsboro");

                case "0xf8f8ff":
                    return ("GhostWhite");

                case "0xffd700":
                    return ("Gold");

                case "0xdaa520":
                    return ("GoldenRod");

                case "0x808080":
                    return ("Gray");

                case "0x008000":
                    return ("Green");

                case "0xadff2f":
                    return ("GreenYellow");

                case "0xf0fff0":
                    return ("HoneyDew");

                case "0xff69b4":
                    return ("HotPink");

                case "0xcd5c5c":
                    return ("IndianRed");

                case "0x4b0082":
                    return ("Indigo");

                case "0xfffff0":
                    return ("Ivory");

                case "0xf0e68c":
                    return ("Khaki");

                case "0xe6e6fa":
                    return ("Lavender");

                case "0xfff0f5":
                    return ("LavenderBlush");

                case "0x7cfc00":
                    return ("LawnGreen");

                case "0xfffacd":
                    return ("LemonChiffon");

                case "0xadd8e6":
                    return ("LightBlue");

                case "0xf08080":
                    return ("LightCoral");

                case "0xe0ffff":
                    return ("LightCyan");

                case "0xfafad2":
                    return ("LightGoldenRodYellow");

                case "0xd3d3d3":
                    return ("LightGray");

                case "0x90ee90":
                    return ("LightGreen");

                case "0xffb6c1":
                    return ("LightPink");

                case "0xffa07a":
                    return ("LightSalmon");

                case "0x20b2aa":
                    return ("LightSeaGreen");

                case "0x87cefa":
                    return ("LightSkyBlue");

                case "0x778899":
                    return ("LightSlateGray");

                case "0xb0c4de":
                    return ("LightSteelBlue");

                case "0xffffe0":
                    return ("LightYellow");

                case "0x00ff00":
                    return ("Lime");

                case "0x32cd32":
                    return ("LimeGreen");

                case "0xfaf0e6":
                    return ("Linen");

                case "0xff00ff":
                    return ("Magenta");

                case "0x800000":
                    return ("Maroon");

                case "0x66cdaa":
                    return ("MediumAquaMarine");

                case "0x0000cd":
                    return ("MediumBlue");

                case "0xba55d3":
                    return ("MediumOrchid");

                case "0x9370db":
                    return ("MediumPurple");

                case "0x3cb371":
                    return ("MediumSeaGreen");

                case "0x7b68ee":
                    return ("MediumSlateBlue");

                case "0x00fa9a":
                    return ("MediumSpringGreen");

                case "0x48d1cc":
                    return ("MediumTurquoise");

                case "0xc71585":
                    return ("MediumVioletRed");

                case "0x191970":
                    return ("MidnightBlue");

                case "0xf5fffa":
                    return ("MintCream");

                case "0xffe4e1":
                    return ("MistyRose");

                case "0xffe4b5":
                    return ("Moccasin");

                case "0xffdead":
                    return ("NavajoWhite");

                case "0x000080":
                    return ("Navy");

                case "0xfdf5e6":
                    return ("OldLace");

                case "0x808000":
                    return ("Olive");

                case "0x6b8e23":
                    return ("OliveDrab");

                case "0xffa500":
                    return ("Orange");

                case "0xff4500":
                    return ("OrangeRed");

                case "0xda70d6":
                    return ("Orchid");

                case "0xeee8aa":
                    return ("PaleGoldenRod");

                case "0x98fb98":
                    return ("PaleGreen");

                case "0xafeeee":
                    return ("PaleTurquoise");

                case "0xdb7093":
                    return ("PaleVioletRed");

                case "0xffefd5":
                    return ("PapayaWhip");

                case "0xffdab9":
                    return ("PeachPuff");

                case "0xcd853f":
                    return ("Peru");

                case "0xffc0cb":
                    return ("Pink");

                case "0xdda0dd":
                    return ("Plum");

                case "0xb0e0e6":
                    return ("PowderBlue");

                case "0x800080":
                    return ("Purple");

                case "0xff0000":
                    return ("Red");

                case "0xbc8f8f":
                    return ("RosyBrown");

                case "0x4169e1":
                    return ("RoyalBlue");

                case "0x8b4513":
                    return ("SaddleBrown");

                case "0xfa8072":
                    return ("Salmon");

                case "0xf4a460":
                    return ("SandyBrown");

                case "0x2e8b57":
                    return ("SeaGreen");

                case "0xfff5ee":
                    return ("SeaShell");

                case "0xa0522d":
                    return ("Sienna");

                case "0xc0c0c0":
                    return ("Silver");

                case "0x87ceeb":
                    return ("SkyBlue");

                case "0x6a5acd":
                    return ("SlateBlue");

                case "0x708090":
                    return ("SlateGray");

                case "0xfffafa":
                    return ("Snow");

                case "0x00ff7f":
                    return ("SpringGreen");

                case "0x4682b4":
                    return ("SteelBlue");

                case "0xd2b48c":
                    return ("Tan");

                case "0x008080":
                    return ("Teal");

                case "0xd8bfd8":
                    return ("Thistle");

                case "0xff6347":
                    return ("Tomato");

                case "0x40e0d0":
                    return ("Turquoise");

                case "0xee82ee":
                    return ("Violet");

                case "0xf5deb3":
                    return ("Wheat");

                case "0xffffff":
                    return ("White");

                case "0xf5f5f5":
                    return ("WhiteSmoke");

                case "0xffff00":
                    return ("Yellow");

                case "0x9acd32":
                    return ("YellowGreen");

                default:
                    return (colorCompare);
            }
        }

    }

    /**
     * Configures a FileChooser.
     *
     * @param fileChooser
     */
    private void configureFileChooser(final FileChooser fileChooser) {   //sets up the file chooser for open and close
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("all Images", "*.*")
        );
    }

    /**
     * Returns one set of coordinates for Graphics Context Draw Polygon.
     *
     * @param numSides
     * @param length
     * @param point
     * @param direction
     * @return
     */
    private double[] findPolygonVertices(int numSides, double length, double point, String direction) {
        double[] points = new double[numSides];
        double degrees = 360 / numSides;
        double radians = Math.toRadians(degrees);
        double radiansBetweenVertices = radians;

        for (int i = 0; i < numSides; i++) {
            if (direction.equals("x")) {
                points[i] = (length * Math.cos(radians)) + point;
            }
            if (direction.equals("y")) {
                points[i] = (length * Math.sin(radians)) + point;
            }
            radians = radians + radiansBetweenVertices;
        }

        return points;
    }

    /**
     * Take input from UI and draw the correct object on the canvas
     *
     * @param x
     * @param y
     */
    private void draw(double x, double y) {
        double sideLength = 1;
        String displayString = toolBarTextArea.getText();

        gc.drawImage(drawingImage, 0, 0);

        if (rectangle == true && shapeFill == false) {
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.strokeRect(oldMouseX, y, x - oldMouseX, oldMouseY - y);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.strokeRect(x, y, oldMouseX - x, oldMouseY - y);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.strokeRect(x, oldMouseY, oldMouseX - x, y - oldMouseY);
            } else {
                gc.strokeRect(oldMouseX, oldMouseY, x - oldMouseX, y - oldMouseY);
            }
        } else if (rectangle == true && shapeFill == true) {
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.fillRect(oldMouseX, y, x - oldMouseX, oldMouseY - y);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.fillRect(x, y, oldMouseX - x, oldMouseY - y);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.fillRect(x, oldMouseY, oldMouseX - x, y - oldMouseY);
            } else {
                gc.fillRect(oldMouseX, oldMouseY, x - oldMouseX, y - oldMouseY);
            }
        } else if (square == true && shapeFill == false) {
            if (abs(y - oldMouseY) > abs(x - oldMouseX)) {
                sideLength = abs(x - oldMouseX);
            }
            if (abs(y - oldMouseY) < abs(x - oldMouseX)) {
                sideLength = abs(y - oldMouseY);
            }
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.strokeRect(oldMouseX, y, sideLength, sideLength);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.strokeRect(x, y, sideLength, sideLength);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.strokeRect(x, oldMouseY, sideLength, sideLength);
            }
            if ((x - oldMouseX) > 0 && (y - oldMouseY) > 0) {
                gc.strokeRect(oldMouseX, oldMouseY, sideLength, sideLength);
            }
        } else if (square == true && shapeFill == true) {
            if (abs(y - oldMouseY) > abs(x - oldMouseX)) {
                sideLength = abs(x - oldMouseX);
            }
            if (abs(y - oldMouseY) < abs(x - oldMouseX)) {
                sideLength = abs(y - oldMouseY);
            }
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.fillRect(oldMouseX, y, sideLength, sideLength);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.fillRect(x, y, sideLength, sideLength);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.fillRect(x, oldMouseY, sideLength, sideLength);
            }
            if ((x - oldMouseX) > 0 && (y - oldMouseY) > 0) {
                gc.fillRect(oldMouseX, oldMouseY, sideLength, sideLength);
            }
        } else if (circle == true && shapeFill == false) {
            if (abs(y - oldMouseY) > abs(x - oldMouseX)) {
                sideLength = abs(x - oldMouseX);
            }
            if (abs(y - oldMouseY) < abs(x - oldMouseX)) {
                sideLength = abs(y - oldMouseY);
            }
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.strokeOval(oldMouseX, y, sideLength, sideLength);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.strokeOval(x, y, sideLength, sideLength);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.strokeOval(x, oldMouseY, sideLength, sideLength);
            }
            if ((x - oldMouseX) > 0 && (y - oldMouseY) > 0) {
                gc.strokeOval(oldMouseX, oldMouseY, sideLength, sideLength);
            }
        } else if (circle == true && shapeFill == true) {
            if (abs(y - oldMouseY) > abs(x - oldMouseX)) {
                sideLength = abs(x - oldMouseX);
            }
            if (abs(y - oldMouseY) < abs(x - oldMouseX)) {
                sideLength = abs(y - oldMouseY);
            }
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.fillOval(oldMouseX, y, sideLength, sideLength);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.fillOval(x, y, sideLength, sideLength);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.fillOval(x, oldMouseY, sideLength, sideLength);
            }
            if ((x - oldMouseX) > 0 && (y - oldMouseY) > 0) {
                gc.fillOval(oldMouseX, oldMouseY, sideLength, sideLength);
            }
        } else if (ellipse == true && shapeFill == false) {
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.strokeOval(oldMouseX, y, x - oldMouseX, oldMouseY - y);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.strokeOval(x, y, oldMouseX - x, oldMouseY - y);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.strokeOval(x, oldMouseY, oldMouseX - x, y - oldMouseY);
            } else {
                gc.strokeOval(oldMouseX, oldMouseY, x - oldMouseX, y - oldMouseY);
            }
        } else if (ellipse == true && shapeFill == true) {
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.fillOval(oldMouseX, y, x - oldMouseX, oldMouseY - y);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.fillOval(x, y, oldMouseX - x, oldMouseY - y);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.fillOval(x, oldMouseY, oldMouseX - x, y - oldMouseY);
            } else {
                gc.fillOval(oldMouseX, oldMouseY, x - oldMouseX, y - oldMouseY);
            }
        } else if (roundRectangle == true && shapeFill == false) {
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.strokeRoundRect(oldMouseX, y, x - oldMouseX, oldMouseY - y, (x - oldMouseX) * .1, (oldMouseY - y) * .1);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.strokeRoundRect(x, y, oldMouseX - x, oldMouseY - y, (oldMouseX - x) * .1, (oldMouseY - y) * .1);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.strokeRoundRect(x, oldMouseY, oldMouseX - x, y - oldMouseY, (oldMouseX - x) * .1, (y - oldMouseY) * .1);
            } else {
                gc.strokeRoundRect(oldMouseX, oldMouseY, x - oldMouseX, y - oldMouseY, (x - oldMouseX) * .1, (y - oldMouseY) * .1);
            }
        } else if (roundRectangle == true && shapeFill == true) {
            if ((x - oldMouseX) > 0 && (y - oldMouseY) < 0) {
                gc.fillRoundRect(oldMouseX, y, x - oldMouseX, oldMouseY - y, (x - oldMouseX) * .1, (oldMouseY - y) * .1);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) < 0) {
                gc.fillRoundRect(x, y, oldMouseX - x, oldMouseY - y, (oldMouseX - x) * .1, (oldMouseY - y) * .1);
            }
            if ((x - oldMouseX) < 0 && (y - oldMouseY) > 0) {
                gc.fillRoundRect(x, oldMouseY, oldMouseX - x, y - oldMouseY, (oldMouseX - x) * .1, (y - oldMouseY) * .1);
            } else {
                gc.fillRoundRect(oldMouseX, oldMouseY, x - oldMouseX, y - oldMouseY, (x - oldMouseX) * .1, (y - oldMouseY) * .1);
            }
        } else if (text == true && shapeFill == false) {
            gc.strokeText(displayString, x, y);
        } else if (text == true && shapeFill == true) {
            gc.fillText(displayString, x, y);
        } else if (polygon == true) {
            int sides = Integer.parseInt(polygonSides.getText());
            double[] xPoints;
            double[] yPoints;
            xPoints = findPolygonVertices(sides, x - oldMouseX, x, "x");
            yPoints = findPolygonVertices(sides, y - oldMouseY, y, "y");
            if (shapeFill == false) {
                gc.strokePolygon(xPoints, yPoints, sides);
            }
            if (shapeFill == true) {
                gc.fillPolygon(xPoints, yPoints, sides);
            }
        }

    }

    /**
     * Sets the UI for the timer.
     */
    private void timerSetLabels() {
        timerMinutes.setText(Integer.toString(minutes));
        timerTens.setText(Integer.toString(tens));
        timerOnes.setText(Integer.toString(ones));
    }

    /**
     * Used to set a scene given the name of an FXML file.
     *
     * @param fxml
     * @return
     * @throws IOException
     */
    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFXApplication8.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Opens a new window from the FXML file with the title of the given string.
     *
     * @param fileName
     * @throws IOException
     */
    private void openNewWindow(String fileName) throws IOException {
        Scene scene = new Scene(loadFXML(fileName));
        Stage stage = new Stage();
        stage.setTitle(fileName);
        stage.setScene(scene);
        stage.show();
    }

    private void addCanvas() {
        canvas.add(canvas0);
        canvas.add(canvas1);
        canvas.add(canvas2);
        canvas.add(canvas3);
        canvas.add(canvas4);
        canvas.add(canvas5);
        canvas.add(canvas6);
    }

    private void addTempCanvas() {
        tempCanvas.add(tempCanvas0);
        tempCanvas.add(tempCanvas1);
        tempCanvas.add(tempCanvas2);
        tempCanvas.add(tempCanvas3);
        tempCanvas.add(tempCanvas4);
        tempCanvas.add(tempCanvas5);
        tempCanvas.add(tempCanvas6);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureFileChooser(fileChooser);                          //Initialize file chooser settings
        addCanvas();                                                //Put all the FXML imported canvases into the list
        addTempCanvas();                                            //Put all the FXML imported tempCanvases into the list
        gc = canvas.get(tabIndex).getGraphicsContext2D();           //set Graphics contexts to respective canvases
        gc.setImageSmoothing(false);
        tempGraphicsContext = tempCanvas.get(tabIndex).getGraphicsContext2D();
        gc.setStroke(Color.BLACK);                                  //set stroke color and width
        gc.setLineWidth(2);
        lineColorPicker.getStyleClass().add("button");              //set color picker styles and intial colors
        lineColorPicker.setValue(Color.BLACK);
        fillColorPicker.getStyleClass().add("button");
        fillColorPicker.setValue(Color.BLACK);
        shapeFill = true;                                           //set tool use boolean values
        textFill = true;
        hand = true;
        pencil = false;
        line = false;
        square = false;
        rectangle = false;
        ellipse = false;
        circle = false;
        text = false;
        eraser = false;
        roundRectangle = false;
        polygon = false;
        select = false;
        selected = false;                                           //Booleans not for tools but related
        savedOnce = false;
        firstUndo = true;
        firstRedo = true;
        scaleX = 1.0;                                               //Scale inital values
        scaleY = 1.0;
        WritableImage blankCanvas = new WritableImage((int) canvas.get(tabIndex).getWidth(), (int) canvas.get(tabIndex).getHeight());                 //add a writable image of the intial canvas to the undo stack and the tabs list
        for (int i = 0; i < 7; i++) {                               //allow free movement of selection canvas
            tempCanvas.get(i).setManaged(false);
            Stack<WritableImage> stack = new Stack<>();
            redo.add(stack);
            stack.add(blankCanvas);
            undo.add(stack);
        }
        //Timeline for counting down the timer
        saveTimer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (minutes == 0 && tens == 0 && ones == 0) {
                    try {
                        saveFile(new ActionEvent());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    minutes = 5;
                    tens = 0;
                    ones = 0;
                } else if (tens == 0 && ones == 0) {
                    minutes = minutes - 1;
                    tens = 5;
                    ones = 9;
                } else if (ones == 0) {
                    tens = tens - 1;
                    ones = 9;
                } else {
                    ones = ones - 1;
                }
                timerSetLabels();
            }
        }));
        saveTimer.setCycleCount(Timeline.INDEFINITE);
        saveTimer.play();
        //timeline for logging
        logTimer = new Timeline(new KeyFrame(Duration.seconds(60), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (toolBarToolSelect.getText() == "Tools") {
                    tool = toolBarShapeSelect.getText();
                } else {
                    tool = toolBarToolSelect.getText();
                }

                if (fileIn != null) {
                    filename = fileIn.getName();
                } else {
                    filename = "unsaved";
                }

                toLog = "Tool is: " + tool + " File name is: " + filename;

                logger.log(Level.INFO, toLog);
            }
        }));
        logTimer.setCycleCount(Timeline.INDEFINITE);
        logTimer.play();

        toolBarToolSelect.setTooltip(toolsList);                    //set all four tooltips
        toolBarShapeSelect.setTooltip(shapesList);
        toolBarlineWidthOptions.setTooltip(lineWidth);
        toolBarFillOptions.setTooltip(fillOptions);
    }
}