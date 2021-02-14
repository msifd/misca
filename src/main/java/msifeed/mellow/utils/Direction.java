package msifeed.mellow.utils;

public enum Direction {
    HORIZONTAL, VERTICAL, BOTH;

    public boolean isHorizontal() {
        return this == HORIZONTAL || this == BOTH;
    }

    public boolean isVertical() {
        return this == VERTICAL || this == BOTH;
    }
}
