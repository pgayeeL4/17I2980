package day10;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MoreKernels extends Application {
    
    static Image inputImageJfx;
    static MyImage outputImageJfx;
    
    static List<MyImage> undoStack = new ArrayList<MyImage>();
    static List<MyImage> redoStack = new ArrayList<MyImage>();
    
    private ImageView outputImageView;
    

    public static void main(String[] args) throws Exception{
        
         System.out.println("Working Directory = " +
              System.getProperty("user.dir"));
        
        inputImageJfx = new Image(new FileInputStream("DogSprinkler.jpeg"));
        
        outputImageJfx = new MyImage((int)inputImageJfx.getWidth(), (int)inputImageJfx.getHeight());;
        
        outputImageJfx.copyFrom(inputImageJfx);
        
        
        
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        outputImageView = new ImageView(outputImageJfx);
      
        VBox menuPane = new VBox();
        
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e->{
                
            if(undoStack.size() <= 0) return;

            MyImage previousImage = undoStack.get(undoStack.size() - 1);
            undoStack.remove(undoStack.size() - 1);

            redoStack.add(outputImageJfx);

            outputImageJfx = previousImage;

            outputImageView.setImage(outputImageJfx);
            
        });
        menuPane.getChildren().add(undoButton);
        
        Button redoButton = new Button("Redo");
        redoButton.setOnAction(e->{
            if(redoStack.size() <= 0) return;

            MyImage previousImage = redoStack.get(redoStack.size() - 1);
            redoStack.remove(redoStack.size() - 1);

            undoStack.add(outputImageJfx);

            outputImageJfx = previousImage;


            outputImageView.setImage(outputImageJfx);
        });
        menuPane.getChildren().add(redoButton);
        
        menuPane.getChildren().add(new Separator());
        
        
        for(Method method : outputImageJfx.getClass().getDeclaredMethods())
        {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            
            
            Button button = new Button(method.getName());
            button.setOnAction(e->{
                        try {  
                            
                            MyImage newImage = new MyImage((int)inputImageJfx.getWidth(), (int)inputImageJfx.getHeight());
                            
                            newImage.copyFrom(outputImageJfx);
                            
                            undoStack.add(outputImageJfx);
                            
                            redoStack.clear();
                            
                            outputImageJfx = newImage;
                            
                            
                            method.invoke(outputImageJfx, new Object[]{1});
                            
                            outputImageView.setImage(outputImageJfx);
                            
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            Logger.getLogger(MoreKernels.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                
            );
            
            
            menuPane.getChildren().add(button);
        }
        
        menuPane.getChildren().add(new Separator());
        
        Button reduceButton = new Button("Reduce Colors");
        reduceButton.setOnAction(e->undoable(()->outputImageJfx.reduceColors(32)));        
        menuPane.getChildren().add(reduceButton);
        
        reduceButton = new Button("Reduce Colors Random");
        reduceButton.setOnAction(e->undoable(()->outputImageJfx.reduceColorsRandom(32)));        
        menuPane.getChildren().add(reduceButton);
        
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setStyle("-fx-background-color: DAE6F3;");
        root.setCenter(outputImageView);
        
        
        root.setLeft(menuPane);
        
        Scene scene = new Scene(root, inputImageJfx.getWidth() + 400, inputImageJfx.getHeight() + 20 );
        
        primaryStage.setTitle("Duplicates");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void undoable (Runnable toRun)
    {
        MyImage newImage = new MyImage((int)inputImageJfx.getWidth(), (int)inputImageJfx.getHeight());

        newImage.copyFrom(outputImageJfx);

        undoStack.add(outputImageJfx);

        redoStack.clear();

        outputImageJfx = newImage;
        toRun.run();
        
        outputImageView.setImage(outputImageJfx);
    }
}
