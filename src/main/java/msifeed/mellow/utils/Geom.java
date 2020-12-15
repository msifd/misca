package msifeed.mellow.utils;

public class Geom implements Cloneable {
    public int x, y, z;
    public int w, h;

    public Geom() {
    }

    public Geom(int x, int y, int w, int h) {
        set(x, y, w, h);
    }

    public void reset() {
        set(0, 0, 0, 0, 0);
    }

    public void set(int x, int y, int w, int h) {
        set(x, y, this.z, w, h);
    }

    public void set(int x, int y, int z, int w, int h) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.h = h;
    }

    public void add(int x, int y, int w, int h) {
        this.x += x;
        this.y += y;
        this.w += w;
        this.h += h;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void translate(int x, int y) {
        translate(x, y, 0);
    }

    public void translate(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void translate(Point p) {
        translate(p.x, p.y);
    }

    public void translate(Point p, int z) {
        translate(p.x, p.y, z);
    }

    public void translate(Geom g) {
        translate(g.x, g.y, g.z);
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

//    public void resize(Point p) {
//        this.w = p.x;
//        this.h = p.y;
//    }

    public void addSize(int w, int h) {
        this.w += w;
        this.h += h;
    }

//    public void offsetPos(Margins m) {
//        this.x += m.left;
//        this.y += m.top;
//    }
//
//    public void offsetSize(Margins m) {
//        this.w -= m.left + m.right;
//        this.h -= m.top + m.bottom;
//    }

    public boolean contains(Point p) {
        return p.x >= this.x && p.x <= this.x + this.w &&
                p.y >= this.y && p.y <= this.y + this.h;
    }

    @Override
    public Geom clone() {
        try {
            return (Geom) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Geom) {
            final Geom o = (Geom) obj;
            return x == o.x && y == o.y && z == o.z && w == o.w && h == o.h;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("Geom(%d_%d_%d %d_%d)", x, y, z, w, h);
    }
}
