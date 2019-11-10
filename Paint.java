// Emily Stanish

package paint;

import com.sun.javaws.Main;
import static java.awt.SystemColor.text;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import static java.lang.System.gc;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;

/**
 *
 * @author emily
 */
public class Paint extends Application {
    VBox sp;
    
    VBox vb;
    final MenuBar menuBar = new MenuBar();
    
    Stage primaryStage;
    Canvas canvas = new Canvas(800, 800);
  
    GraphicsContext gc = canvas.getGraphicsContext2D();
    final ColorPicker colorPicker = new ColorPicker();
    private Line currentLine;
        
    Color c;
    int strokeRadius = 25;
    int xCoordinate;
    int yCoordinate;

    
    AnchorPane anchorRoot;
    double initX;
    double initY;
    
    File currentFile;
    int i = 1;
    
    /**
     *
     * @throws IOException
     */
    public void chooseAFile() throws IOException  {
        //this will open up the file chooser and return the image path
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(primaryStage);
            
        currentFile = file;   
        renImage(file);  
            
    }
            
    /**
     *
     * @param file
     * @throws IOException
     */
    public void renImage(File file) throws IOException {
        //take chooseAFile() as input
        //render and return image
        
        Image image = new Image("file:" + file, false);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0,0, canvas.getWidth(),canvas.getHeight());
        
        
    
       
    }

    /**
     *
     * @param file
     */
    public void saveImage(File file) {
      
        if (file == null) return;
        
        SnapshotParameters params = new SnapshotParameters();
        WritableImage snapshot = canvas.snapshot(params, null);
        
        
            try {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot,
                        null), "png", currentFile);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
        
        currentFile = file;
        
    }
    
    /**
     *
     */
    public void saveImageAs() {
        //takes image
        //saves to a file (call the following method to select a file)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            saveImage(file);
        }
        
    }
   
  
    
    @Override
    public void start(Stage primaryStage) {
     
        Stack<Shape> undoHistory = new Stack();
        Stack<Shape> redoHistory = new Stack();
                        
        /* ----btns---- */
        ToggleButton drawbtn = new ToggleButton("Pencil");
        ToggleButton linebtn = new ToggleButton("Line");
        ToggleButton rectbtn = new ToggleButton("Rectangle");
        ToggleButton circlebtn = new ToggleButton("Circle");
        ToggleButton elpsbtn = new ToggleButton("Ellipse");
        ToggleButton starbtn = new ToggleButton("Star"); 
        ToggleButton textbtn = new ToggleButton("Text"); 
        
        ToggleButton[] toolList = {drawbtn, linebtn, rectbtn, circlebtn, elpsbtn, starbtn, textbtn};
        
        ToggleGroup tools = new ToggleGroup();
        
        for (ToggleButton tool : toolList) {
            tool.setMinWidth(90);
            tool.setToggleGroup(tools);
            tool.setCursor(Cursor.HAND);
        }
        
        ColorPicker cpLine = new ColorPicker(Color.BLACK);
        ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);
        
        
        
        TextArea text = new TextArea(); 
        
        text.setPrefRowCount(1);
        
        Slider widthScroll = new Slider(1, 50, 3);
        widthScroll.setShowTickLabels(true);
        widthScroll.setShowTickMarks(true);
        
        
       
        Label line_color = new Label("Line Color");
        Label fill_color = new Label("Fill Color");
        Label line_width = new Label("Line Width");
        
        
        

        
        VBox btns = new VBox(10);
        btns.getChildren().addAll(drawbtn, linebtn, rectbtn, circlebtn, elpsbtn,starbtn,
                textbtn, text, line_color, cpLine, fill_color, cpFill, line_width, widthScroll);
        btns.setPadding(new Insets(5));
        btns.setStyle("-fx-background-color: #afbbcc");
        btns.setPrefWidth(100);
        
        
        
        /* ----Canvas---- */
        
        
        gc.setLineWidth(1);
        
        Line line = new Line();
        Rectangle rect = new Rectangle();
        Circle circ = new Circle();
        Ellipse elps = new Ellipse();
        Shape star = new Polygon();

                        
        canvas.setOnMousePressed(e->{
            if(drawbtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            }
            else if(linebtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                line.setStartX(e.getX());
                line.setStartY(e.getY());
            }
            else if(rectbtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                rect.setX(e.getX());                
                rect.setY(e.getY());
            }
            else if(circlebtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());
            }
            else if(elpsbtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                elps.setCenterX(e.getX());
                elps.setCenterY(e.getY());
            }
            else if(starbtn.isSelected()) {
                star.setLayoutX(e.getX());
                star.setLayoutY(e.getY());
                
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                
            }
           
            else if(textbtn.isSelected()) {
                gc.setLineWidth(1);
                gc.setFont(Font.font(widthScroll.getValue()));
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                gc.fillText(text.getText(), e.getX(), e.getY());
                gc.strokeText(text.getText(), e.getX(), e.getY());
            }
        });
        
        canvas.setOnMouseDragged(e->{
            if(drawbtn.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }   
        });
        
        canvas.setOnMouseReleased(e->{
            if(drawbtn.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.closePath();
            }
            
            else if(linebtn.isSelected()) {
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                
                undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
            }
            else if(rectbtn.isSelected()) {
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                
                if(rect.getX() > e.getX()) {
                    rect.setX(e.getX());
                }
                
                if(rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                
                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
                
            }
            else if(circlebtn.isSelected()) {
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);
                
                if(circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if(circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }
                
                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                
                undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
            }
            else if(elpsbtn.isSelected()) {
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));
                
                if(elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if(elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }
                
                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                
                undoHistory.push(new Ellipse(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY()));
            }
            else if(starbtn.isSelected()) {
                double startX = star.getLayoutX();
                double startY = star.getLayoutY();
                
                double xpoints[] = {startX, startX+75, startX+100, startX+125, startX+200, startX+150, startX+160, startX+100, startX+40, startX+50};
                double ypoints[] = {startY, startY-10, startY-75, startY-10, startY, startY+40, startY+105, startY+65, startY+105, startY+40};
                      
                gc.strokePolygon(xpoints, ypoints, xpoints.length);
                undoHistory.push(new Polygon(star.getLayoutX(), star.getLayoutY()));
                
            }
           
            redoHistory.clear();
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());
            
        });
        
        
         /**
          * color picker
      **/
        cpLine.setOnAction(e->{
                gc.setStroke(cpLine.getValue());
        });
        cpFill.setOnAction(e->{
                gc.setFill(cpFill.getValue());
        });
        
        
         /**
          * slider for line width
      **/
        widthScroll.valueProperty().addListener(e->{
            double width = widthScroll.getValue();
            if(textbtn.isSelected()){
                gc.setLineWidth(1);
                gc.setFont(Font.font(widthScroll.getValue()));
                line_width.setText(String.format("%.1f", width));
                return;
            }
            line_width.setText(String.format("%.1f", width));
            gc.setLineWidth(width);
        });
        
        
        /* ---- Menu ---- */
        Menu menu1 = new Menu("File");
        
        MenuItem m1Item1 = new MenuItem("Open");
        m1Item1.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                chooseAFile();
            } catch (IOException ex) {
                Logger.getLogger(Paint.class.getName()).log(Level.SEVERE, null, ex);
                
            }
        }
        });
        
                
        MenuItem m1Item2 = new MenuItem("Save");
        m1Item2.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            saveImage(currentFile);
        }
        });
              
        MenuItem m1Item3 = new MenuItem("Save As");
        m1Item3.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {
            saveImageAs();
        }
        });
        
        MenuItem m1Item4 = new MenuItem("Exit");
        
        m1Item4.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            System.exit(0);
        }
        });
        
        
        menu1.getItems().addAll(m1Item1, m1Item2, m1Item3, m1Item4);
        
        Menu menu2 = new Menu("Edit");
        
        MenuItem m2Item1 = new MenuItem("Undo");
        m2Item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //undo
                if(!undoHistory.empty()){
                gc.clearRect(0, 0, 1080, 790);
                Shape removedShape = undoHistory.lastElement();
                if(removedShape.getClass() == Line.class) {
                    Line tempLine = (Line) removedShape;
                    tempLine.setFill(gc.getFill());
                    tempLine.setStroke(gc.getStroke());
                    tempLine.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                    
                }
                else if(removedShape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) removedShape;
                    tempRect.setFill(gc.getFill());
                    tempRect.setStroke(gc.getStroke());
                    tempRect.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                }
                else if(removedShape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) removedShape;
                    tempCirc.setStrokeWidth(gc.getLineWidth());
                    tempCirc.setFill(gc.getFill());
                    tempCirc.setStroke(gc.getStroke());
                    redoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                }
                else if(removedShape.getClass() == Ellipse.class) {
                    Ellipse tempElps = (Ellipse) removedShape;
                    tempElps.setFill(gc.getFill());
                    tempElps.setStroke(gc.getStroke());
                    tempElps.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                Shape lastRedo = redoHistory.lastElement();
                lastRedo.setFill(removedShape.getFill());
                lastRedo.setStroke(removedShape.getStroke());
                lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
                undoHistory.pop();
                
                for(int i=0; i < undoHistory.size(); i++) {
                    Shape shape = undoHistory.elementAt(i);
                    if(shape.getClass() == Line.class) {
                        Line temp = (Line) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                    }
                    else if(shape.getClass() == Rectangle.class) {
                        Rectangle temp = (Rectangle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                        gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    }
                    else if(shape.getClass() == Circle.class) {
                        Circle temp = (Circle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    }
                    else if(shape.getClass() == Ellipse.class) {
                        Ellipse temp = (Ellipse) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    }
                    
                }
            } else {
                System.out.println("There is no action to undo.");
            }
            }
        });
          //end of undo
          
        MenuItem m2Item2 = new MenuItem("Redo");
        m2Item2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //redo
                if(!redoHistory.empty()) {
                    Shape shape = redoHistory.lastElement();
                    gc.setLineWidth(shape.getStrokeWidth());
                    gc.setStroke(shape.getStroke());
                    gc.setFill(shape.getFill());
                    
                    redoHistory.pop();
                    if(shape.getClass() == Line.class) {
                        Line tempLine = (Line) shape;
                        gc.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
                        undoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                }
                    else if(shape.getClass() == Rectangle.class) {
                        Rectangle tempRect = (Rectangle) shape;
                        gc.fillRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                        gc.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                    
                        undoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                }
                    else if(shape.getClass() == Circle.class) {
                        Circle tempCirc = (Circle) shape;
                        gc.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                        gc.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                    
                        undoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                }
                    else if(shape.getClass() == Ellipse.class) {
                        Ellipse tempElps = (Ellipse) shape;
                        gc.fillOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
                        gc.strokeOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
                    
                        undoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                    Shape lastUndo = undoHistory.lastElement();
                    lastUndo.setFill(gc.getFill());
                    lastUndo.setStroke(gc.getStroke());
                    lastUndo.setStrokeWidth(gc.getLineWidth());
            }       
                    else {
                        System.out.println("There is no action to redo.");
            }
        
                
            }
        });
        
        menu2.getItems().addAll(m2Item1, m2Item2);
        
        Menu menu3 = new Menu("Help");
        MenuItem m3Item1 = new MenuItem("Tool Tips");
        m3Item1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                BufferedReader reader = null;
            try {
                String strCurrentLine;

                reader = new BufferedReader(new FileReader("C:\\Users\\emily\\Documents\\ToolTips.txt"));

                while ((strCurrentLine = reader.readLine()) != null) {

                    System.out.println(strCurrentLine);
   }

  }         catch (IOException ex) {

                ex.printStackTrace();

  }         finally {

                try {
                    if (reader != null)
                    reader.close();
   }            catch (IOException ex) {
                    ex.printStackTrace();
   }
  }
 
        
            }
        });
        
        MenuItem m3Item2 = new MenuItem("About");
        menu3.getItems().addAll(m3Item1, m3Item2);
        
        m3Item2.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            BufferedReader reader = null;
            try {
                String strCurrentLine;

                reader = new BufferedReader(new FileReader("C:\\Users\\emily\\Documents\\paintReleaseStanish.txt"));

                while ((strCurrentLine = reader.readLine()) != null) {

                    System.out.println(strCurrentLine);
   }

  }         catch (IOException ex) {

                ex.printStackTrace();

  }         finally {

                try {
                    if (reader != null)
                    reader.close();
   }            catch (IOException ex) {
                    ex.printStackTrace();
   }
  }
 
        }
        });
        
       
       
        
        
        ScrollPane s = new ScrollPane();
        
        
        s.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        s.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        
        
        

        
        
        menuBar.getMenus().addAll(menu1, menu2, menu3); 
        
        //the following creates the color picker
        HBox box = new HBox();
        box.setPadding(new Insets(5, 5, 5, 5));            
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.CORAL);
        box.getChildren().add(colorPicker);
        box.setSpacing(40);
        
        
        
        
        
        
        btns.setAlignment(Pos.TOP_CENTER);
        
        StackPane stacking = new StackPane();
        
        stacking.getChildren().add(canvas);
        
        BorderPane pane = new BorderPane();
        pane.setLeft(btns);
        pane.setCenter(canvas);
        pane.setTop(menuBar);
      
        
        
        s.setContent(pane);
        
        vb = new VBox(pane, s);
        
        vb.setSpacing(10);
        
        
        
        
        primaryStage.setTitle("Paint"); 
        Scene scene = new Scene(vb, 600, 600);
        
        s.prefWidthProperty().bind(scene.widthProperty());
	s.prefHeightProperty().bind(scene.heightProperty());
        s.blendModeProperty();
        s.backgroundProperty();
        
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        
        
        
        primaryStage.show();
        
        
        
                }
        
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
