public class NeighborPointList
{
    private float xLeftCoor, yLeftCoor, xRightCoor, yRightCoor;

    NeighborPointList(float xLeftCoor, float yLeftCoor, float xRightCoor, float yRightCoor)
    {
        this.xLeftCoor = xLeftCoor;
        this.yLeftCoor = yLeftCoor;
        this.xRightCoor = xRightCoor;
        this.yRightCoor = yRightCoor;
    }

    public float xLeftCoor()
    {
        return xLeftCoor;
    }

    public float yLeftCoor()
    {
        return yLeftCoor;
    }

    public float xRightCoor()
    {
        return xRightCoor;
    }

    public float yRightCoor()
    {
        return yRightCoor;
    }

    public static NeighborPointList getVector(float xLeftCoor, float yLeftCoor, float xRightCoor, float yRightCoor)
    {
        return new NeighborPointList(xLeftCoor, yLeftCoor, xRightCoor, yRightCoor);
    }

    @Override
    public String toString()
    {
        return "[" + this.xLeftCoor() + ", " + this.yLeftCoor() + ", " + this.xRightCoor() + ", " + this.yRightCoor() + "]";
    }
}
