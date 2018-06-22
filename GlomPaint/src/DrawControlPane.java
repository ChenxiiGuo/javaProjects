/*This pane is the control pane which is in the right part of the window
   All buttons in the control pane are put here.*/


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class DrawControlPane extends GridPane {

    //private int pixelsPerMM = 1;
    DrawControlPane(DrawControl drawControl) {
        this.getStylesheets().add("Style.css");
        this.getStyleClass().add("ControlPane");

        this.setMinSize(250, 450);

        buildConstraint(this, 5, 9, 250, 450);

        Button eraserButton = new Button("Eraser");
        setEraserButtion(eraserButton, drawControl);
        Text eraser = new Text("Eraser");
        eraser.getStyleClass().add("PaneText");
        this.add(eraserButton, 1, 2, 2, 1);
        this.add(eraser, 0, 2, 1, 1);

        ChoiceBox eraserCb = new ChoiceBox(FXCollections.observableArrayList(
                "Small", "Medium", "Big"
        ));
        setEraserCb(eraserCb, drawControl);
        this.add(eraserCb, 3, 2, 2, 1);


        Text pen = new Text("Pen");
        pen.getStyleClass().add("PaneText");
        this.add(pen, 0, 3, 1, 1);

        final ColorPicker colorPicker = new ColorPicker();
        setColorPicker(colorPicker, drawControl);
        this.add(colorPicker, 1, 3, 2,1);

        ChoiceBox penSizeCb = new ChoiceBox(FXCollections.observableArrayList(
                "2", "4", "6", "8", "10", "20"
        ));
        setPenSizeCb(penSizeCb, drawControl);
        this.add(penSizeCb, 3, 3, 2, 1);

        Text filler  = new Text("Filler");
        pen.getStyleClass().add("PaneText");
        this.add(filler, 0, 4, 1, 1);

        final ColorPicker colorPickerForFilter = new ColorPicker();
        setColorPickerForFilter(colorPickerForFilter, drawControl);
        this.add(colorPickerForFilter, 1, 4,3,1);


        Text open = new Text("Open");
        pen.getStyleClass().add("PaneText");
        this.add(open, 0, 0, 1, 1);

        Button openMultiPictures = new Button("Open files");
        setOpenMultiPictures(openMultiPictures, drawControl);
        this.add(openMultiPictures,1, 0, 2, 1);


        Text clear = new Text("Clear");
        pen.getStyleClass().add("PaneText");
        this.add(clear, 0, 5, 1, 1);

        Button clearAll = new Button("Clear All");
        setClearAll(clearAll, drawControl);
        this.add(clearAll, 1, 5, 2, 1);


        Text saveText = new Text("Save");
        pen.getStyleClass().add("PaneText");
        this.add(saveText, 0, 1, 1, 1);

        Button save = new Button("SAVE");
        setSave(save, drawControl);
        this.add(save,3, 1,2,1);

        Button quickSave = new Button("Quick Save");
        setQuickSave(quickSave, drawControl);
        this.add(quickSave, 1, 1, 2,1);


        Text timeMachine = new Text("Un/Re");
        timeMachine.getStyleClass().add("PaneText");
        this.add(timeMachine, 0, 6, 1, 1);

        Button undo = new Button("Undo");
        setUndo(undo, drawControl);
        this.add(undo, 1, 6, 2, 1);

        Button redo = new Button("Redo");
        setRedo(redo, drawControl);
        this.add(redo, 3, 6, 2, 1);


        Text ppm= new Text("PPM");
        timeMachine.getStyleClass().add("PaneText");
        this.add(ppm, 0, 7, 1, 1);

        TextField pixelsPerMM = new TextField();
        setTextField(pixelsPerMM, drawControl);
        this.add(pixelsPerMM, 1 , 7, 3, 1);


        Text calculator= new Text("Area");
        timeMachine.getStyleClass().add("PaneText");
        this.add(calculator, 0, 8, 1, 1);

        Button areaCalculator = new Button("Area Calculator");
        setAreaCalculator(areaCalculator, pixelsPerMM, drawControl);
        this.add(areaCalculator, 1, 8, 3, 1);


    }



    //After user click this button, there cursor will have the ability to trigger the area
    //calculator when they click it.
    void setAreaCalculator(Button areaCalculator, TextField pixelsPerMM, DrawControl drawControl) {
        areaCalculator.getStyleClass().add("Button");
        areaCalculator.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                drawControl.setIsCalculateArea(true);
                drawControl.setFillModel(false);
                if (pixelsPerMM.getText().length() > 0) {
                    drawControl.setPpm(Integer.parseInt(pixelsPerMM.getText()));
                }

            }
        });
    }



    //ppm which means the number of pixels in a square of millimetres.
    //avoid invalid input is 0
    //the defalt value of pixelsPerMM is 1
    void setTextField(TextField pixelsPerMM, DrawControl drawControl) {
        pixelsPerMM.getStyleClass().add("Text");
        pixelsPerMM.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    pixelsPerMM.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    //to avoid blurry image!!!! spend 4 honours.....
    //The idea of this function is use a previously preserved image to
    //replace the current canvas
    void setUndo (Button undo, DrawControl drawControl) {
        undo.getStyleClass().add("Button");
        undo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                WritableImage image = drawControl.undo();

                GraphicsContext gc = drawControl.getMainCanvas().getGraphicsContext2D();
                gc.clearRect(0,0, 800, 450);
                gc.drawImage(image, 0, 0, 800, 450);
            }
        });

    }

    void setRedo (Button redo, DrawControl drawControl) {
        redo.getStyleClass().add("Button");
        redo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {

                WritableImage image = drawControl.redo();
                if (image != null) {
                    GraphicsContext gc = drawControl.getMainCanvas().getGraphicsContext2D();
                    gc.clearRect(0,0, 800, 450);
                    gc.drawImage(image, 0, 0, 800, 450);
                }

            }
        });
    }


    // Clear all things in the canvas
    void setClearAll (Button clearAll, DrawControl drawControl) {
        clearAll.getStyleClass().add("Button");
        clearAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                GraphicsContext gc = drawControl.getMainCanvas().getGraphicsContext2D();
                gc.clearRect(0,0, 850, 400);
                //for redo
                drawControl.addToCanvasHistory(drawControl.getMainCanvas());
            }
        });
    }


    void setOpenMultiPictures(Button openMultiPictures, DrawControl drawControl) {
        final FileChooser fileChooser = new FileChooser();
        openMultiPictures.getStyleClass().add("Button");

        Stage stage = new Stage();
        stage.setTitle("Choose images");

        openMultiPictures.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        List<File> list =
                                fileChooser.showOpenMultipleDialog(stage);
                        List<String> fileURL = new ArrayList<>();
                        if (list != null) {
                            for (File item : list) {
                                if (item.getAbsolutePath().endsWith(".png")) {
                                    fileURL.add(item.getAbsolutePath());
                                }

                            }
                        }
                        drawControl.addImageList(fileURL);
                    }
                });
    }


    //QuickSave is a simple function,
    //it can only take one still image of the drawing board
    void setQuickSave(Button save, DrawControl drawControl) {
        save.getStyleClass().add("Button");
        save.setOnAction(e -> {
            WritableImage image = drawControl.getImage().
                    snapshot(new SnapshotParameters(), null);
            File file = new File("quicksave.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException error) {
                System.out.println("File to save!");
            }

        });
    }


    //With the help of this function, you can save the image in any folder
    void setSave(Button save, DrawControl drawControl) {
        save.getStyleClass().add("Button");
        Stage stage = new Stage();

        save.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Image");
                File file = fileChooser.showSaveDialog(stage);

                if (file != null) {
                    try {
                        WritableImage image = drawControl.getImage().
                                snapshot(new SnapshotParameters(), null);
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null),
                                "png", file);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });
    }


    void setPenSizeCb(ChoiceBox penSizeCb, DrawControl drawControl) {
        penSizeCb.getStyleClass().add("Cb");
        penSizeCb.setValue("10");
        penSizeCb.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    drawControl.setIsCalculateArea(false);
                    drawControl.setFillModel(false);
                    drawControl.setEraser(false);
                    drawControl.setPenSize(
                            (new_val.intValue()*2 + 2) > 10 ? 20 : new_val.intValue() * 2 + 2);
                }
        );
    }

    void setEraserButtion(Button eraserButton, DrawControl drawControl) {
        eraserButton.getStyleClass().add("Button");
        eraserButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(final ActionEvent e) {
                drawControl.setIsCalculateArea(false);
                drawControl.setFillModel(false);
                drawControl.setEraser(true);
            }
        });

    }



    void setEraserCb(ChoiceBox eraserCb, DrawControl drawControl) {
        eraserCb.getStyleClass().add("Cb");
        eraserCb.setValue("Medium");

        eraserCb.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    drawControl.setIsCalculateArea(false);
                    drawControl.setFillModel(false);
                    drawControl.setEraser(true);
                    drawControl.setEraserSize(new_val.intValue()*5 + 5);
                }
        );
    }


    void setColorPicker(ColorPicker  colorPicker, DrawControl drawControl) {
        colorPicker.getStyleClass().add("ColorPicker");
        colorPicker.getStyleClass().add("Cb");


        colorPicker.setValue(Color.WHITE);
        colorPicker.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                drawControl.setIsCalculateArea(false);
                drawControl.setFillModel(false);
                drawControl.setEraser(false);
                drawControl.setColor(colorPicker.getValue());
            }
        });
    }

    void setColorPickerForFilter(ColorPicker colorPicker, DrawControl drawControl) {
        colorPicker.getStyleClass().add("ColorPicker");
        colorPicker.getStyleClass().add("Cb");

        colorPicker.setValue(Color.WHITE);
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawControl.setIsCalculateArea(false);
                drawControl.setFillModel(true);
                drawControl.setEraser(false);
                drawControl.setFillColor(colorPicker.getValue());
            }
        });
    }

    private void buildConstraint(GridPane grid, int colNum, int rowNum, int width, int height) {
        for (int i = 0; i < colNum; i++) {
            ColumnConstraints column = new ColumnConstraints(width/colNum);
            grid.getColumnConstraints().add(column);
        }
        for (int i = 0; i < rowNum; i++) {
            RowConstraints row = new RowConstraints(height/rowNum);
            grid.getRowConstraints().add(row);
        }
    }



}
