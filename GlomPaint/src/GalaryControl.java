import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;



/*this class story the mainImage*/
public class GalaryControl{
    private double drawMaxW = 800;
    private double drawMaxH = 450;

    private GridPane mainImageGrid = new GridPane();



    /*Recive image and put it in the drawing board(Also called mainImage)*/
    void setMainImageGrid(Image mainImage) {
        ImageView mainImageView = new ImageView(mainImage);

        mainImageView.setFitWidth(drawMaxW);
        mainImageView.setFitHeight(drawMaxH);
        mainImageGrid.add(mainImageView, 0, 0);
    }

    GridPane getMainImageGrid() {
        return mainImageGrid;
    }
}
