package day10;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class PhotoEditor extends Application {
    
    static Image inputImageJfx;
    static EditableImage outputImageJfx;
    
    static List<EditableImage> history = new ArrayList<EditableImage>();

    public static void main(String[] args) throws Exception{
        
        inputImageJfx = new Image(new FileInputStream(System.getProperty("user.home") + "/Desktop/photo.jpg"));;
        
        outputImageJfx = new EditableImage((int)inputImageJfx.getWidth(), (int)inputImageJfx.getHeight());;
        
        outputImageJfx.copyFrom(inputImageJfx);
        
        
        //outputImageJfx.sharpen();
        //outputImageJfx.save();
        
        
        
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        //ImageView inputImageView = new ImageView(inputImageJfx);
        ImageView outputImageView = new ImageView(outputImageJfx);
      
        
        
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 0));
        root.setStyle("-fx-background-color: DAE6F3;");
       
        //root.setCenter(inputImageView);
        root.setCenter(outputImageView);
        
       VBox menuPane = new VBox();
       menuPane.setPadding(new Insets(10, 10, 10, 10));
        
       
       Button undoButton = new Button("Undo");
       Button redoButton = new Button("Redo");
       
       menuPane.getChildren().add(undoButton);
       menuPane.getChildren().add(redoButton);
       
       menuPane.getChildren().add(new Separator());
       
       
       
       Method[] methods = EditableImage.class.getDeclaredMethods();
       
       for(Method method : methods)
       {
           //Conditions for rejecting this method
           
        if (!Modifier.isPublic(method.getModifiers())) {
            continue;
        }

           
          Button methodExecute = new Button(method.getName());
          methodExecute.setOnAction(new EventHandler<ActionEvent>(){

              @Override
              public void handle(ActionEvent event) {
                  try {
                      
                      EditableImage newImage = new EditableImage((int)inputImageJfx.getWidth(), (int)inputImageJfx.getHeight());
                      newImage.copyFrom(outputImageJfx);
                      
                      history.add(outputImageJfx);
                      
                      outputImageJfx = newImage;
                      
                      outputImageView.setImage(outputImageJfx);
                      
                      
                      method.invoke(outputImageJfx, new Object[]{1});
                  } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                      Logger.getLogger(PhotoEditor.class.getName()).log(Level.SEVERE, null, ex);
                  }
              }
              
          });
          
          menuPane.getChildren().add(methodExecute);
       }
       
       
       
        root.setLeft(menuPane);
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Photo Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}