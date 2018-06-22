
// This is for the gallary
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;


import java.io.File;
import java.util.List;

public class ImageGrid extends GridPane{

    private ImageView mainImageView;
    final int gallaryWidth = 960;
    final int gallaryHeigh = 200;

    final int imageWidth = 400;
    final int imageHeigh = 200;

    GalaryControl galaryControl;
    ImageGrid(GalaryControl galaryControl) {
        this.setWidth(gallaryWidth);
        this.setHeight(gallaryHeigh);
        this.galaryControl = galaryControl;
        //System.out.println("jhh");
        //his.se;
        //this.getStylesheets().add("Style.css");
        //this.getStyleClass().add("ImageBackPane");

        //Image image = new Image("test.png");
        //mainImageView = new ImageView(image);

    }
    public void addImages(List<String> imageUrlList) {
        for(int i = 0; imageUrlList != null && i < imageUrlList.size(); i ++) {
            System.out.println(imageUrlList.get(i));
            File imageFile = new File(imageUrlList.get(i));
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(imageWidth);
            imageView.setFitHeight(imageHeigh);

            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent e) {
                    mainImageView= imageView;
                    //GalaryControl.mainImage = imageView;
                    galaryControl.setMainImageGrid(mainImageView.getImage());

                    //System.out.println(mainImageView.toString());

                }
            });

            this.add(imageView, i, 0);
            //System.out.println(imageView.toString());

            //System.out.println(i);
        }
    }


}
