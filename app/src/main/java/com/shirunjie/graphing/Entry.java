package com.shirunjie.graphing;

/**
 * Created by shirunjie on 2016-07-08.
 */

public class Entry {
    private double x;
    private double y;
    private double value = Double.NEGATIVE_INFINITY;
    private CharSequence stringLabel = null;

    public Entry(float x, float y, double value) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public Entry(float x, float y, CharSequence stringLabel) {
        this.stringLabel = stringLabel;
        this.x = x;
        this.y = y;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public CharSequence getStringLabel() {
        return stringLabel;
    }

    public void setStringLabel(String stringLabel) {
        this.stringLabel = stringLabel;
    }
}
