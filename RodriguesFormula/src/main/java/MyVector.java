public class MyVector
{
    // An epsilon for comparing floats.
    private static float EPSILON = 0.000001f;

    private float x, y, z;

    MyVector(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float x()
    {
        return x;
    }

    public float y()
    {
        return y;
    }

    public float z()
    {
        return z;
    }

    public static MyVector getVector(float x, float y, float z)
    {
        return new MyVector(x, y, z);
    }

    public static MyVector zeroVector()
    {
        return MyVector.getVector(0, 0, 0);
    }

    public MyVector add(MyVector v)
    {
        return new MyVector(x + v.x(), y + v.y(), z + v.z());
    }

    public MyVector subtract(MyVector v)
    {
        return new MyVector(x - v.x(), y - v.y(), z - v.z());
    }

    public MyVector negate()
    {
        return new MyVector(-x, -y, -z);
    }

    public MyVector multiply(float scale)
    {
        return new MyVector(x * scale, y * scale, z * scale);
    }

    public MyVector normalize()
    {
        return this.multiply(1.0f / this.magnitude());
    }

    public float magnitude()
    {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public float dotProduct(MyVector v)
    {
        return (this.x * v.x()) + (this.y * v.y()) + (this.z * v.z());
    }

    public MyVector crossProduct(MyVector v)
    {
        float nx = (this.y() * v.z()) - (this.z() * v.y());
        float ny = (this.z() * v.x()) - (this.x() * v.z());
        float nz = (this.x() * v.y()) - (this.y() * v.x());

        return new MyVector(nx, ny, nz);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof MyVector)
        {
            MyVector v = (MyVector) o;

            return withinEpsilon(this.x(), v.x()) && withinEpsilon(this.y(), v.y()) && withinEpsilon(this.z(), v.z());
        }
        else
        {
            return false;
        }
    }

    private boolean withinEpsilon(float f1, float f2)
    {
        return Math.abs(f1 - f2) < EPSILON;
    }

    @Override
    public String toString()
    {
        return "[" + this.x() + ", " + this.y() + ", " + this.z() + "]";
    }
}

    /*MyVector myAxis = new MyVector(0,0,5);
    MyVector myVector = new MyVector(6,0,5);

    for(int degree= 30; degree <= 60; degree++)
    {
        MyVector newVector = MyMath.rotateVector(myVector, myAxis, degree);
        System.out.println(newVector.toString());

        series.getData().add(new XYChart.Data(newVector.x(), newVector.y()));
    }*/