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
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
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
    Group myChartGroup = new Group();
    Group myPolygonGroup = new Group();

    final NumberAxis xAxis = new NumberAxis(0, 200, 5);
    final NumberAxis yAxis = new NumberAxis(0, 200, 5);
    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
    final ObservableList<XYChart.Data<Float, Float>> dataset = FXCollections.observableArrayList();
    final ObservableList<XYChart.Data<Float, Float>> datasetLeft = FXCollections.observableArrayList();
    final ObservableList<XYChart.Data<Float, Float>> datasetRight = FXCollections.observableArrayList();

    NeighborPointList pointList;
    float ALTITUDE = 10, HORIZANTAL_DEGREE = 30, VERTICAL_DEGREE = 70, ANTENNA_DEGREE = 20, ANTENNA_MAX_DEGREE = 85, H_DISTANCE = 0, V1_DISTANCE = 0, V2_DISTANCE = 0;
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

        Button clearButton = new Button("Clear");
        clearButton.setVisible(false);
        clearButton.setPrefWidth(80);
        clearButton.setPrefHeight(25);

        HBox horizontalDegreeHBox = createHBoxNode("Horizontal Degree: ", HORIZANTAL_DEGREE);
        wrapperVBox.getChildren().add(horizontalDegreeHBox);
        HBox verticalDegreeHBox = createHBoxNode("Vertical Degree: ", VERTICAL_DEGREE);
        wrapperVBox.getChildren().add(verticalDegreeHBox);
        HBox antennaDegreeHBox = createHBoxNode("Antenna Degree: ", ANTENNA_DEGREE);
        wrapperVBox.getChildren().add(antennaDegreeHBox);
        HBox antennaMaxDegreeHBox = createHBoxNode("Antenna Max Degree: ", ANTENNA_MAX_DEGREE);
        wrapperVBox.getChildren().add(antennaMaxDegreeHBox);
        // ------------------- MOUSE EVENT (DRAW BUTTON) ------------------
        drawButton.setOnAction(new EventHandler<ActionEvent>() {
            float x1Coor, y1Coor, x2Coor, y2Coor, xLeftCoor, yLeftCoor, xRightCoor, yRightCoor, slope;
            String direction;

            @Override
            public void handle(ActionEvent event) {
                myPolygonGroup.getChildren().clear();

                HBox horizontalDegreeHBox = (HBox) wrapperVBox.getChildren().get(0);
                HBox verticalDegreeHBox = (HBox) wrapperVBox.getChildren().get(1);
                HBox antennaDegreeHBox = (HBox) wrapperVBox.getChildren().get(2);
                HBox antennaMaxDegreeHBox = (HBox) wrapperVBox.getChildren().get(3);
                TextField horizontalField = (TextField) horizontalDegreeHBox.getChildren().get(1);
                TextField verticalField = (TextField) verticalDegreeHBox.getChildren().get(1);
                TextField antennaField = (TextField) antennaDegreeHBox.getChildren().get(1);
                TextField antennaMaxField = (TextField) antennaMaxDegreeHBox.getChildren().get(1);
                HORIZANTAL_DEGREE = Float.parseFloat(horizontalField.getText());
                VERTICAL_DEGREE = Float.parseFloat(verticalField.getText());
                ANTENNA_DEGREE = Float.parseFloat(antennaField.getText());
                ANTENNA_MAX_DEGREE = Float.parseFloat(antennaMaxField.getText());

                Double[] polygonPointArray = new Double[8];

                for(int pointIndex=0; pointIndex < dataset.size()-1; pointIndex++)
                {
                    Polygon myFillPolygon = new Polygon();
                    myFillPolygon.setFill(new Color(0.2, 0.1, 0.3, 0.2));

                    x1Coor = dataset.get(pointIndex).getXValue();
                    y1Coor = dataset.get(pointIndex).getYValue();
                    x2Coor = dataset.get(pointIndex+1).getXValue();
                    y2Coor = dataset.get(pointIndex+1).getYValue();

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
                    else if( (slope == Float.POSITIVE_INFINITY) || (slope == Float.NEGATIVE_INFINITY) )
                    {
                        direction = "VERTICAL_INFINITY";
                    }
                    else        // slope < 0
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
                    int polygonCounter = 0;

                    for(int pointCounter=0; pointCounter <= 1; pointCounter++)
                    {
                        HBox altitudeHBox = (HBox) wrapperVBox.getChildren().get(pointIndex + (pointCounter+4));    // horizontalDegree, verticalDegree, antennaDegree ve antennaMaxDegree
                        TextField altitudeField = (TextField) altitudeHBox.getChildren().get(1);                    // icin 4 index atla.
                        ALTITUDE = Float.parseFloat(altitudeField.getText());
                        H_DISTANCE = MyMath.findGroundDistance(ALTITUDE, HORIZANTAL_DEGREE);
                        V1_DISTANCE = MyMath.findGroundDistance(ALTITUDE, VERTICAL_DEGREE - (ALTITUDE/2) );  // VERTICAL_DEGREE - (ANTENNA_DEGREE/2)     -> FIRST_POINT
                        V2_DISTANCE = MyMath.findGroundDistance(ALTITUDE, VERTICAL_DEGREE + (ALTITUDE/2) );  // VERTICAL_DEGREE + (ANTENNA_DEGREE/2)     -> SECOND_POINT

                        System.out.println(H_DISTANCE + " - >> " + V1_DISTANCE + " - >> " + V2_DISTANCE);

                        if(pointCounter == 0)
                        {
                            pointList = MyMath.findNeighborPointList(x1Coor, y1Coor, H_DISTANCE, V1_DISTANCE, slope, direction, "FIRST_POINT");
                        }
                        else
                        {
                            pointList = MyMath.findNeighborPointList(x2Coor, y2Coor, H_DISTANCE, V2_DISTANCE, slope, direction, "SECOND_POINT");
                        }
                        System.out.println(pointIndex + " -> " + direction + " -> "+ slope);

                        xLeftCoor   = pointList.xLeftCoor();
                        yLeftCoor   = pointList.yLeftCoor();
                        xRightCoor  = pointList.xRightCoor();
                        yRightCoor  = pointList.yRightCoor();

                        XYChart.Data<Float, Float> dataLeft = new XYChart.Data(xLeftCoor, yLeftCoor);
                        XYChart.Data<Float, Float> dataRight = new XYChart.Data(xRightCoor, yRightCoor);
                        datasetLeft.add(dataLeft);
                        datasetRight.add(dataRight);

                        double leftXPixel = xAxis.getDisplayPosition(xLeftCoor);
                        double leftYPixel = yAxis.getDisplayPosition(yLeftCoor);
                        double rightXPixel = xAxis.getDisplayPosition(xRightCoor);
                        double rightYPixel = yAxis.getDisplayPosition(yRightCoor);

                        if(pointCounter == 0)
                        {
                            polygonPointArray[polygonCounter]   = leftXPixel  + 65;
                            polygonPointArray[polygonCounter+1] = leftYPixel  + 40;
                            polygonPointArray[polygonCounter+2] = rightXPixel + 65;
                            polygonPointArray[polygonCounter+3] = rightYPixel + 40;
                        }
                        else
                        {
                            polygonPointArray[polygonCounter] = rightXPixel + 65;
                            polygonPointArray[polygonCounter+1] = rightYPixel + 40;
                            polygonPointArray[polygonCounter+2]   = leftXPixel  + 65;
                            polygonPointArray[polygonCounter+3] = leftYPixel  + 40;
                        }
                        polygonCounter = polygonCounter + 4;
                    }
                    myFillPolygon.getPoints().addAll(polygonPointArray);
                    myPolygonGroup.getChildren().add(myFillPolygon);
                }
                seriesLeft.setData(datasetLeft);
                seriesRight.setData(datasetRight);
                //lineChart.getData().addAll(seriesLeft, seriesRight);
            }
        });

        // ------------------- MOUSE EVENT (CLEAR BUTTON) ------------------
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                myPolygonGroup.getChildren().clear();
                myChartGroup.getChildren().clear();
                myChartGroup.getChildren().addAll(lineChart, myPolygonGroup);
            }
        });

        buttonsHBox.getChildren().addAll(drawButton, clearButton);
        wrapperVBox.getChildren().add(buttonsHBox);

        // ------------------- MOUSE EVENT (CHART) ------------------
        lineChart.setOnMousePressed((MouseEvent event) ->
        {
            drawButton.setVisible(true);
            clearButton.setVisible(true);

            Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
            double xPixelCoor = xAxis.sceneToLocal(mouseSceneCoords).getX();
            double yPixelCoor = yAxis.sceneToLocal(mouseSceneCoords).getY();
            System.out.println("XPixel: " + xPixelCoor + " - YPixel: " + yPixelCoor);
            Number xCoor = xAxis.getValueForDisplay(xPixelCoor);
            Number yCoor = yAxis.getValueForDisplay(yPixelCoor);

            XYChart.Data<Float, Float> data = new XYChart.Data(xCoor.floatValue(), yCoor.floatValue());
            data.setNode(new HoveredThresholdNode(wayPointCounter));
            dataset.add(data);
            series.setData(dataset);

            primaryStage.setTitle("" + xCoor.floatValue() + ",  " + yCoor.floatValue());

            HBox myHBoxNode = createHBoxNode("Altitude " + (wayPointCounter) + ": ", ALTITUDE);
            wrapperVBox.getChildren().add(wayPointCounter+4, myHBoxNode);   // First four indexes are -> HORIZONTAL_DEGREE, VERTICAL_DEGREE, ANTENNA_DEGREE, ANTENNA_MAX_DEGREE
            wayPointCounter++;
        });

        lineChart.getData().add(series);
        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        lineChart.setMinWidth(900);
        lineChart.setMinHeight(900);

        myChartGroup.getChildren().addAll(lineChart, myPolygonGroup);

        Scene scene = new Scene(myChartGroup, 900, 900);
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


