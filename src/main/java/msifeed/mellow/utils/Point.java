package msifeed.mellow.utils;

public class Point implements Cloneable {
    public int x;
    public int y;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public void translate(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void translate(Point p) {
        translate(p.x, p.y);
    }

    public Point clone() {
        try {
            return (Point) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
