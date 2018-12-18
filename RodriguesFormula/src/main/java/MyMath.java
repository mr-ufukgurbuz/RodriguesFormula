import java.security.Policy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyMath
{
    private static float findEquationSlope(float x1, float y1, float x2, float y2)           // 'm' -> equationSlope
    {   // equation -> y = mx + n
        return ( (y2 - y1) / (x2 - x1) );   // m
    }

    public static float findVertEquationSlope(float equationSlope)                          // 'm2' -> verticalEquationSlope
    {   // equationSlope * verticalEquationSlope = -1
        return ( -1 / equationSlope );     // m2
    }

    public static float findEquationConstant(float x, float y, float slope)                 // 'n' -> constant
    {   // equation -> y = mx + n
        return ( y - (slope * x) );         // n
    }

    public static float findIntersectionXCoor(float slope1, float slope2, float constant1, float constant2)
    {
        return ( (constant2 - constant1) / (slope1 - slope2));
    }

    public static float findYCoorFromXCoor(float x, float slope, float constant)            // y = mx + n
    {
        return ( (slope * x) + constant );
    }

    public static float findGroundDistance(float altitude, float degree)
    {
        return (float) (altitude * ( Math.cos(Math.toRadians(90-degree)) / Math.cos(Math.toRadians(degree))) );
    }

    public static NeighborPointList findNeighborPointList2(float x, float y, float altitude, float hDistance, float vDistance, float slope, String direction)
    {
        float xLeftCoor, yLeftCoor, xRightCoor, yRightCoor;
        float hBaseDistance = ( hDistance * (altitude + vDistance) ) / altitude;

        float kVertical = (float) Math.sqrt( Math.pow(vDistance, 2) / (Math.pow(slope, 2) + 1) );               // k2 + (mk)2 = (vDistance)2   -> (m2 +1)k2 = (vDistance)2
        float kHorizontal = (float) Math.sqrt( Math.pow(hBaseDistance, 2) / (Math.pow(slope, 2) + 1) );         // k2 + (mk)2 = (hBaseDistance)2   -> (m2 +1)k2 = (hBaseDistance)2

        float xVerticalDistance   = kVertical;
        float yVerticalDistance   = Math.abs(slope) * kVertical;
        float xHorizontalDistance = Math.abs(slope) * kHorizontal;
        float yHorizontalDistance = kHorizontal;

        if(direction.equals("NORTH_EAST"))
        {
            xLeftCoor   = x + xVerticalDistance + xHorizontalDistance;
            xRightCoor  = x + xVerticalDistance - xHorizontalDistance;
            yLeftCoor   = y + yVerticalDistance - yHorizontalDistance;
            yRightCoor  = y + yVerticalDistance + yHorizontalDistance;
        }
        else if(direction.equals("NORTH_WEST"))
        {
            xLeftCoor   = x - xVerticalDistance + xHorizontalDistance;
            xRightCoor  = x - xVerticalDistance - xHorizontalDistance;
            yLeftCoor   = y + yVerticalDistance + yHorizontalDistance;
            yRightCoor  = y + yVerticalDistance - yHorizontalDistance;
        }
        else if(direction.equals("SOUTH_EAST"))
        {
            xLeftCoor   = x + xVerticalDistance - xHorizontalDistance;
            xRightCoor  = x + xVerticalDistance + xHorizontalDistance;
            yLeftCoor   = y - yVerticalDistance - yHorizontalDistance;
            yRightCoor  = y - yVerticalDistance + yHorizontalDistance;
        }
        else if(direction.equals("SOUTH_WEST"))
        {
            xLeftCoor   = x - xVerticalDistance - xHorizontalDistance;
            xRightCoor  = x - xVerticalDistance + xHorizontalDistance;
            yLeftCoor   = y - yVerticalDistance + yHorizontalDistance;
            yRightCoor  = y - yVerticalDistance - yHorizontalDistance;
        }
        else if(direction.equals("NORTH"))
        {
            xLeftCoor   = x + hBaseDistance;
            xRightCoor  = x - hBaseDistance;
            yLeftCoor   = y + vDistance;
            yRightCoor  = y + vDistance;
        }
        else if(direction.equals("SOUTH"))
        {
            xLeftCoor   = x - hBaseDistance;
            xRightCoor  = x + hBaseDistance;
            yLeftCoor   = y - vDistance;
            yRightCoor  = y - vDistance;
        }
        else if(direction.equals("EAST"))
        {
            xLeftCoor   = x + vDistance;
            xRightCoor  = x + vDistance;
            yLeftCoor   = y - hBaseDistance;
            yRightCoor  = y + hBaseDistance;
        }
        else    // WEST
        {
            xLeftCoor   = x - vDistance;
            xRightCoor  = x - vDistance;
            yLeftCoor   = y + hBaseDistance;
            yRightCoor  = y - hBaseDistance;
        }

        NeighborPointList pointList = NeighborPointList.getVector(xLeftCoor, yLeftCoor, xRightCoor, yRightCoor);
        return pointList;
    }

    public static ArrayList<Point> findPolygonPointList(float x1Coor, float y1Coor, float x2Coor, float y2Coor, float ALTITUDE, float HORIZANTAL_DEGREE, float VERTICAL_DEGREE, float ANTENNA_DEGREE )
    {
        float slope = MyMath.findEquationSlope(x1Coor, y1Coor, x2Coor, y2Coor);
        float H_DISTANCE, V1_DISTANCE, V2_DISTANCE;
        String direction;

        H_DISTANCE = MyMath.findGroundDistance(ALTITUDE, HORIZANTAL_DEGREE);
        V1_DISTANCE = MyMath.findGroundDistance(ALTITUDE, VERTICAL_DEGREE - (ANTENNA_DEGREE/2) );  // VERTICAL_DEGREE - (ANTENNA_DEGREE/2)     -> FIRST_POINT
        V2_DISTANCE = MyMath.findGroundDistance(ALTITUDE, VERTICAL_DEGREE + (ANTENNA_DEGREE/2) );  // VERTICAL_DEGREE + (ANTENNA_DEGREE/2)     -> SECOND_POINT

        if( (slope == Float.POSITIVE_INFINITY) || (slope == Float.NEGATIVE_INFINITY) )
        {
            if(y1Coor <= y2Coor)
            {
                direction = "NORTH";
            }
            else
            {
                direction = "SOUTH";
            }
        }
        else if(slope == 0)
        {
            if(x1Coor <= x2Coor)
            {
                direction = "EAST";
            }
            else
            {
                direction = "WEST";
            }
        }
        else if(slope > 0)
        {
            if(x1Coor <= x2Coor)
            {
                direction = "NORTH_EAST";
            }
            else
            {
                direction = "SOUTH_WEST";
            }
        }
        else        // slope < 0
        {
            if(y1Coor <= y2Coor)
            {
                direction = "NORTH_WEST";
            }
            else
            {
                direction = "SOUTH_EAST";
            }
        }

        ArrayList<Point> firstPointArrayList = MyMath.findNeighborPointList(x1Coor, y1Coor, ALTITUDE, H_DISTANCE, V1_DISTANCE, slope, direction);
        ArrayList<Point> secondPointArrayList = MyMath.findNeighborPointList(x1Coor, y1Coor, ALTITUDE, H_DISTANCE, V2_DISTANCE, slope, direction);

        ArrayList<Point> polygonPointList = new ArrayList<Point>();
        polygonPointList.addAll(firstPointArrayList);
        polygonPointList.addAll(secondPointArrayList);

        return polygonPointList;
    }

    private static ArrayList<Point> findNeighborPointList(float x, float y, float altitude, float hDistance, float vDistance, float slope, String direction)
    {
        float xLeftCoor, yLeftCoor, xRightCoor, yRightCoor;
        float hBaseDistance = ( hDistance * (altitude + vDistance) ) / altitude;

        float kVertical = (float) Math.sqrt( Math.pow(vDistance, 2) / (Math.pow(slope, 2) + 1) );               // k2 + (mk)2 = (vDistance)2   -> (m2 +1)k2 = (vDistance)2
        float kHorizontal = (float) Math.sqrt( Math.pow(hBaseDistance, 2) / (Math.pow(slope, 2) + 1) );         // k2 + (mk)2 = (hBaseDistance)2   -> (m2 +1)k2 = (hBaseDistance)2

        float xVerticalDistance   = kVertical;
        float yVerticalDistance   = Math.abs(slope) * kVertical;
        float xHorizontalDistance = Math.abs(slope) * kHorizontal;
        float yHorizontalDistance = kHorizontal;

        if(direction.equals("NORTH_EAST"))
        {
            xLeftCoor   = x + xVerticalDistance + xHorizontalDistance;
            xRightCoor  = x + xVerticalDistance - xHorizontalDistance;
            yLeftCoor   = y + yVerticalDistance - yHorizontalDistance;
            yRightCoor  = y + yVerticalDistance + yHorizontalDistance;
        }
        else if(direction.equals("NORTH_WEST"))
        {
            xLeftCoor   = x - xVerticalDistance + xHorizontalDistance;
            xRightCoor  = x - xVerticalDistance - xHorizontalDistance;
            yLeftCoor   = y + yVerticalDistance + yHorizontalDistance;
            yRightCoor  = y + yVerticalDistance - yHorizontalDistance;
        }
        else if(direction.equals("SOUTH_EAST"))
        {
            xLeftCoor   = x + xVerticalDistance - xHorizontalDistance;
            xRightCoor  = x + xVerticalDistance + xHorizontalDistance;
            yLeftCoor   = y - yVerticalDistance - yHorizontalDistance;
            yRightCoor  = y - yVerticalDistance + yHorizontalDistance;
        }
        else if(direction.equals("SOUTH_WEST"))
        {
            xLeftCoor   = x - xVerticalDistance - xHorizontalDistance;
            xRightCoor  = x - xVerticalDistance + xHorizontalDistance;
            yLeftCoor   = y - yVerticalDistance + yHorizontalDistance;
            yRightCoor  = y - yVerticalDistance - yHorizontalDistance;
        }
        else if(direction.equals("NORTH"))
        {
            xLeftCoor   = x + hBaseDistance;
            xRightCoor  = x - hBaseDistance;
            yLeftCoor   = y + vDistance;
            yRightCoor  = y + vDistance;
        }
        else if(direction.equals("SOUTH"))
        {
            xLeftCoor   = x - hBaseDistance;
            xRightCoor  = x + hBaseDistance;
            yLeftCoor   = y - vDistance;
            yRightCoor  = y - vDistance;
        }
        else if(direction.equals("EAST"))
        {
            xLeftCoor   = x + vDistance;
            xRightCoor  = x + vDistance;
            yLeftCoor   = y - hBaseDistance;
            yRightCoor  = y + hBaseDistance;
        }
        else    // WEST
        {
            xLeftCoor   = x - vDistance;
            xRightCoor  = x - vDistance;
            yLeftCoor   = y + hBaseDistance;
            yRightCoor  = y - hBaseDistance;
        }
        ArrayList<Point> pointList = new ArrayList<Point>();
        pointList.add(new Point(xLeftCoor, yLeftCoor));
        pointList.add(new Point(xRightCoor, yRightCoor));

        return  pointList;
    }

    /**
     * returns a new vector which is {@code v} rotated around {@code axis} by {@code alpha} degrees clockwise<p>
     * @param v The vector to rotate
     * @param axis The axis of rotation
     * @param alpha The angle of rotation
     * @return The new vector
     */
    public static MyVector rotateVector(MyVector v, MyVector axis, float alpha)
    {
        //This formula is based on Rodrigues' rotation formula.
        alpha *= 1;     // -1 -> it's mean clockwise; 1 -> it's mean counterclockwise
        MyVector k = axis.normalize();
        alpha = (float)((alpha  * Math.PI) / 180 ) ;
       // MyVector k = axis;
        //System.out.println(k.toString());

        MyVector p2 = (k.crossProduct(v)).multiply((float)Math.sin(alpha));
        MyVector p3 = (k.multiply(k.dotProduct(v))).multiply((float) (1 - Math.cos(alpha)));
        MyVector p1 = v.multiply((float)Math.cos(alpha));
        return p1.add(p2).add(p3);
    }
}
