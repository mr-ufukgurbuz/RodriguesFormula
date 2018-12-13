/*public class MyApplication {

    public static void main(String args[])
    {
        MyVector myAxis = new MyVector(0,0,4);
        MyVector myVector = new MyVector(4,4,4);

        float myAngleDegree = 0.0f;

        for(int degree=0; degree <= 360; degree++)
        {
            MyVector newVector = MyMath.rotateVector(myVector, myAxis, degree);
            System.out.println(newVector.toString());
        }

        //MyVector newVector = MyMath.rotateVector(myVector, myAxis, 30);
        //System.out.println(newVector.toString());
    }
}
*/

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Point2D;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.collections.*;


public class MyApplication extends Application
{
    Stage secondaryStage = new Stage();
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
    final ObservableList<XYChart.Data<Float, Float>> dataset = FXCollections.observableArrayList();
    final ObservableList<XYChart.Data<Float, Float>> datasetLeft = FXCollections.observableArrayList();
    final ObservableList<XYChart.Data<Float, Float>> datasetRight = FXCollections.observableArrayList();

    NeighborPointList pointList;
    float ALTITUDE=4, HORIZANTAL_DEGREE=30, VERTICAL_DEGREE=20, DISTANCE=0;
    int wayPointCounter = 0;

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("RF Scannner Heading Simulation");
        secondaryStage.setTitle("Altitudes of Waypoints");
        xAxis.setLabel("X - Axis");
        yAxis.setLabel("Y - Axis");
        lineChart.setTitle("Mission Map");

        XYChart.Series series = new XYChart.Series();
        XYChart.Series seriesLeft = new XYChart.Series();
        XYChart.Series seriesRight = new XYChart.Series();
        series.setName("UAV Waypoints");
        seriesLeft.setName("Left Waypoints");
        seriesRight.setName("Right Waypoints");

        // ------------------- ALTITUDE PANEL ------------------
        VBox wrapperVBox = new VBox(5);
        wrapperVBox.setPadding(new Insets(5, 5, 5, 5));

        HBox buttonsHBox = new HBox(5);
        buttonsHBox.setPadding(new Insets(5, 5, 5, 100));

        Button drawButton = new Button("Draw");
        drawButton.setVisible(false);
        drawButton.setPrefWidth(80);
        drawButton.setPrefHeight(25);

        HBox horizontalDegreeHBox = createHBoxNode("Horizontal Degree: ", HORIZANTAL_DEGREE);
        wrapperVBox.getChildren().add(horizontalDegreeHBox);
        HBox verticalDegreeHBox = createHBoxNode("Vertical Degree: ", VERTICAL_DEGREE);
        wrapperVBox.getChildren().add(verticalDegreeHBox);
        // ------------------- MOUSE EVENT (DRAW BUTTON) ------------------
        drawButton.setOnAction(new EventHandler<ActionEvent>() {
            float x1Coor, y1Coor, x2Coor, y2Coor, xLeftCoor, yLeftCoor, xRightCoor, yRightCoor, slope;
            String direction;

            @Override
            public void handle(ActionEvent event) {
                HBox horizontalDegreeHBox = (HBox) wrapperVBox.getChildren().get(0);
                HBox verticalDegreeHBox = (HBox) wrapperVBox.getChildren().get(1);
                TextField horizontalField = (TextField) horizontalDegreeHBox.getChildren().get(1);
                TextField verticalField = (TextField) verticalDegreeHBox.getChildren().get(1);
                HORIZANTAL_DEGREE = Float.parseFloat(horizontalField.getText());
                VERTICAL_DEGREE = Float.parseFloat(verticalField.getText());

                for(int index=0; index < dataset.size()-1; index++)
                {
                    x1Coor = dataset.get(index).getXValue();
                    y1Coor = dataset.get(index).getYValue();
                    x2Coor = dataset.get(index+1).getXValue();
                    y2Coor = dataset.get(index+1).getYValue();

                    slope = MyMath.findEquationSlope(x1Coor, y1Coor, x2Coor, y2Coor);

                    if(slope >= 0)
                    {
                        if(x1Coor <= x2Coor)
                        {
                            direction = "UP_POSITIVE";
                        }
                        else
                        {
                            direction = "DOWN_POSITIVE";
                        }
                    }
                    else
                    {
                        if(y1Coor <= y2Coor)
                        {
                            direction = "UP_NEGATIVE";
                        }
                        else
                        {
                            direction = "DOWN_NEGATIVE";
                        }
                    }

                    for(int pointCounter=2; pointCounter <= 3; pointCounter++)
                    {
                        HBox altitudeHBox = (HBox) wrapperVBox.getChildren().get(index + pointCounter);
                        TextField altitudeField = (TextField) altitudeHBox.getChildren().get(1);
                        ALTITUDE = Float.parseFloat(altitudeField.getText());
                        DISTANCE = MyMath.findGroundDistance(ALTITUDE, HORIZANTAL_DEGREE);
                        System.out.println("Distance: " + DISTANCE);

                        if(pointCounter == 2)
                        {
                            pointList = MyMath.findNeighborPointList(x1Coor, y1Coor, DISTANCE, slope, direction);
                        }
                        else
                        {
                            pointList = MyMath.findNeighborPointList(x2Coor, y2Coor, DISTANCE, slope, direction);
                        }
                        System.out.println(index + " -> " + direction + " -> "+ slope);

                        xLeftCoor   = pointList.xLeftCoor();
                        yLeftCoor   = pointList.yLeftCoor();
                        xRightCoor  = pointList.xRightCoor();
                        yRightCoor  = pointList.yRightCoor();

                        XYChart.Data<Float, Float> dataLeft = new XYChart.Data(xLeftCoor, yLeftCoor);
                        XYChart.Data<Float, Float> dataRight = new XYChart.Data(xRightCoor, yRightCoor);
                        datasetLeft.add(dataLeft);
                        datasetRight.add(dataRight);
                    }
                }
                seriesLeft.setData(datasetLeft);
                seriesRight.setData(datasetRight);
                lineChart.getData().addAll(seriesLeft, seriesRight);
            }
        });
        buttonsHBox.getChildren().add(drawButton);
        wrapperVBox.getChildren().add(buttonsHBox);

        // ------------------- MOUSE EVENT (CHART) ------------------
        lineChart.setOnMousePressed((MouseEvent event) ->
        {
            drawButton.setVisible(true);

            Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
            double xPixelCoor = xAxis.sceneToLocal(mouseSceneCoords).getX();
            double yPixelCoor = yAxis.sceneToLocal(mouseSceneCoords).getY();
            Number xCoor = xAxis.getValueForDisplay(xPixelCoor);
            Number yCoor = yAxis.getValueForDisplay(yPixelCoor);

            XYChart.Data<Float, Float> data = new XYChart.Data(xCoor.floatValue(), yCoor.floatValue());
            data.setNode(new HoveredThresholdNode(wayPointCounter));
            dataset.add(data);
            series.setData(dataset);

            primaryStage.setTitle("" + xCoor.floatValue() + ",  " + yCoor.floatValue());

            HBox myHBoxNode = createHBoxNode("Altitude " + (wayPointCounter) + ": ", ALTITUDE);
            wrapperVBox.getChildren().add(wayPointCounter+2, myHBoxNode);
            wayPointCounter++;
        });

        lineChart.getData().add(series);
        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);

        Group group = new Group();
        Rectangle rect = new Rectangle(20,20,200,200);
        rect.setFill(new Color(0.6, 0.2,0.3, 0.1 ));
        rect.setStroke(Color.TRANSPARENT);
        group.getChildren().addAll(lineChart, rect);
        lineChart.setMinWidth(790);
        lineChart.setMinHeight(790);


        Scene scene = new Scene(group, 800, 800);
        Scene scene2 = new Scene(wrapperVBox, 250, 300);

        primaryStage.setScene(scene);
        secondaryStage.setScene(scene2);
        primaryStage.show();
        secondaryStage.show();
    }

    public HBox createHBoxNode (String labelName, float defaultValue)
    {
        HBox myHBoxNode = new HBox();
        myHBoxNode.setPrefWidth(300);
        myHBoxNode.setPrefHeight(45);
        myHBoxNode.setPadding(new Insets(5, 5, 5, 5));
        myHBoxNode.setSpacing(10);

        Label myLabel = new Label(labelName);
        myLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        myLabel.setPadding(new Insets(5, 5, 5, 5));
        myLabel.setMinWidth(150);

        TextField myField = new TextField();
        myField.setPrefHeight(30);
        myField.setPrefWidth(80);

        NumberStringFilteredConverter converter = new NumberStringFilteredConverter();
        final TextFormatter<Number> formatter = new TextFormatter<>(
                converter,
                defaultValue,
                converter.getFilter()
        );
        myField.setTextFormatter(formatter);
        myHBoxNode.getChildren().addAll(myLabel, myField);

        return myHBoxNode;
    }

    /** a node which displays a value on hover, but is otherwise empty */
    class HoveredThresholdNode extends StackPane
    {
        HoveredThresholdNode(int value) {
            setPrefSize(15, 15);

            final Label label = createDataThresholdLabel(value);

            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    getChildren().setAll(label);
                    setCursor(Cursor.NONE);
                    toFront();
                }
            });
            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    getChildren().clear();
                    setCursor(Cursor.CROSSHAIR);
                }
            });
        }

        private Label createDataThresholdLabel(int value)
        {
            final Label label = new Label(value + "");
            label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
            label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
            label.setTextFill(Color.FORESTGREEN);
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

            return label;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


