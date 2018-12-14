public class MyMath
{
    public static float findEquationSlope(float x1, float y1, float x2, float y2)           // 'm' -> equationSlope
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

    public static float findGroundHDistance(float altitude, float degree)
    {
        return (float) (altitude * ( Math.cos(Math.toRadians(90-degree)) / Math.cos(Math.toRadians(degree))) );
    }

    public static float findGroundVDistance(float altitude, float degree)
    {
        return (float) (altitude * ( Math.cos(Math.toRadians(degree)) / Math.cos(Math.toRadians(90-degree))) );
    }

    public static NeighborPointList findNeighborPointList(float x, float y, float hDistance, float vDistance, float slope, String direction, String pointStatus)
    {
        float kHorizontal = (float) Math.sqrt( Math.pow(hDistance, 2) / (Math.pow(slope, 2) + 1) );         // k2 + (mk)2 = (hDistance)2   -> (m2 +1)k2 = (hDistance)2
        float kVertical = (float) Math.sqrt( Math.pow(vDistance, 2) / (Math.pow(slope, 2) + 1) );           // k2 + (mk)2 = (vDistance)2   -> (m2 +1)k2 = (vDistance)2
        float xLeftCoor, yLeftCoor, xRightCoor, yRightCoor;
        float xHorizontalDistance, yHorizontalDistance, xVerticalDistance, yVerticalDistance;

        xHorizontalDistance = Math.abs(slope) * kHorizontal;
        yHorizontalDistance = kHorizontal;
        xVerticalDistance   = kVertical;
        yVerticalDistance   = Math.abs(slope) * kVertical;

        if(direction.equals("UP_POSITIVE")) {
            xLeftCoor = x + xHorizontalDistance;
            xRightCoor = x - xHorizontalDistance;
            yLeftCoor = y - yHorizontalDistance;
            yRightCoor = y + yHorizontalDistance;

            if(pointStatus.equals("SECOND_POINT"))
            {
                xLeftCoor = xLeftCoor + xVerticalDistance;
                xRightCoor = xRightCoor + xVerticalDistance;
                yLeftCoor = yLeftCoor + yVerticalDistance;
                yRightCoor = yRightCoor + yVerticalDistance;
            }
        }
        else if(direction.equals("DOWN_POSITIVE")) {
            xLeftCoor = x - xHorizontalDistance;
            xRightCoor = x + xHorizontalDistance;
            yLeftCoor = y + yHorizontalDistance;
            yRightCoor = y - yHorizontalDistance;

            if(pointStatus.equals("SECOND_POINT"))
            {
                xLeftCoor = xLeftCoor - xVerticalDistance;
                xRightCoor = xRightCoor - xVerticalDistance;
                yLeftCoor = yLeftCoor - yVerticalDistance;
                yRightCoor = yRightCoor - yVerticalDistance;
            }
        }
        else if(direction.equals("UP_NEGATIVE"))
        {
            xLeftCoor   = x + xHorizontalDistance;
            xRightCoor  = x - xHorizontalDistance;
            yLeftCoor   = y + yHorizontalDistance;
            yRightCoor  = y - yHorizontalDistance;

            if(pointStatus.equals("SECOND_POINT"))
            {
                xLeftCoor = xLeftCoor - xVerticalDistance;
                xRightCoor = xRightCoor - xVerticalDistance;
                yLeftCoor = yLeftCoor + yVerticalDistance;
                yRightCoor = yRightCoor + yVerticalDistance;
            }
        }
        else if(direction.equals("DOWN_NEGATIVE"))
        {
            xLeftCoor   = x - xHorizontalDistance;
            xRightCoor  = x + xHorizontalDistance;
            yLeftCoor   = y - yHorizontalDistance;
            yRightCoor  = y + yHorizontalDistance;

            if(pointStatus.equals("SECOND_POINT"))
            {
                xLeftCoor = xLeftCoor + xVerticalDistance;
                xRightCoor = xRightCoor + xVerticalDistance;
                yLeftCoor = yLeftCoor - yVerticalDistance;
                yRightCoor = yRightCoor - yVerticalDistance;
            }
        }
        else        // "VERTICAL_INFINITY"
        {
            xLeftCoor   = x - hDistance;
            xRightCoor  = x + hDistance;
            yLeftCoor   = y;
            yRightCoor  = y;

            if(pointStatus.equals("SECOND_POINT"))
            {
                //
            }
        }

        NeighborPointList pointList = new NeighborPointList(xLeftCoor, yLeftCoor, xRightCoor, yRightCoor);
        return pointList;
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
