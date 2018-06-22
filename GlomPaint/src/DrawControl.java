/*This class is a medium of information. All important class
exchange there information from this class.
*
* */

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;


import java.util.*;


public class DrawControl {
    private Color penColor;
    private Color fillColor;


    private boolean isEraser;
    private boolean isFill;
    private boolean isCalculateArea;

    private int eraserSize;
    private int penSize;


    // this is for the main window which shows the big picture
    private GridPane paintGrid;


    //this grid contains the gallery pictures
    private ImageGrid imageGrid;

    //this is where user can draw
    private Canvas mainCanvas;

    //This is my data structure for undo and redo
    private StatusHistory<WritableImage> canvasHistory;

    //The area user chosen by user
    private int area;

    // number of pixels per mm^2
    private int ppm;



    DrawControl(GalaryControl galaryControl) {

        penColor = Color.WHITE;
        fillColor = Color.WHITE;
        isEraser = false;
        eraserSize = 10;
        penSize = 10;
        isFill = false;

        isCalculateArea = false;
        area = 1;
        ppm = 1;

        imageGrid = new ImageGrid(galaryControl);


        canvasHistory = new StatusHistory<>();
    }

    public void setColor(Color color) {
        penColor = color;
    }

    public Color penColor() {
        return penColor;
    }

    public void setPenSize(int size) {
        penSize = size;
    }

    public int getPenSize() {
        return penSize;
    }


    public void setEraser(Boolean isTrue) {
        isEraser = isTrue;
    }

    Boolean isEraser() {
        return isEraser;
    }

    public void setEraserSize(int size) {
        eraserSize = size;
    }

    public int getEraserSize() {
        return eraserSize;
    }

    public boolean isFillModel() {
        return isFill;
    }

    public void setFillModel(Boolean isFill) {
        this.isFill = isFill;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }


    public Color getFillColor() {
        return fillColor;
    }

    public void setIsCalculateArea(Boolean isTrue) {
        this.isCalculateArea = isTrue;
    }

    public boolean isCalculateArea () {
        return isCalculateArea;
    }


    /*for save*/
    public void setImage(GridPane paintGrid) {
        this.paintGrid = paintGrid;
    }

    /*for save*/
    public GridPane getImage() {
        return paintGrid;
    }

    /*this is gallary*/
    public void addImageList(List<String> imageUrlList) {
        imageGrid.addImages(imageUrlList);
    }

    public ImageGrid getGallaryImages() {

        return imageGrid;
    }

    public void setMainCanvas(Canvas canvas) {
        this.mainCanvas = canvas;
    }

    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    //this is used to avoid blurry
    //A snapshot of the drawing board is put into the StatusHistory which works
    // as a library.
    public void addToCanvasHistory(Canvas canvas) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setTransform(javafx.scene.transform.Transform.scale(8,8));

        WritableImage image = canvas.snapshot(parameters,null);
        canvasHistory.addToHistory(image);
    }

    public WritableImage undo() {
        return canvasHistory.undo();
    }

    public WritableImage redo() {
        return canvasHistory.redo();
    }


    public void setPpm(int ppm) {
        this.ppm = ppm;
    }

    public int getPpm() {
        return ppm;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getArea() {
        return area;
    }





}
