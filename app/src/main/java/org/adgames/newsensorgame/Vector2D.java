package org.adgames.newsensorgame;

import android.graphics.PointF;

public class Vector2D extends PointF {
    public Vector2D() {
    }

    public Vector2D(float x, float y) {
        super(x, y);
    }

    public static float getAngle(Vector2D v1, Vector2D v2) {
        Vector2D vector1 = new Vector2D(v1.x, v1.y);
        vector1.normalize();
        Vector2D vector2 = new Vector2D(v2.x, v2.y);
        vector2.normalize();
        return (float) ((Math.atan2((double) vector2.y, (double) vector2.x) - Math.atan2((double) vector1.y, (double) vector1.x)) * 57.29577951308232d);
    }

    public void normalize() {
        float length = (float) Math.sqrt((double) ((this.x * this.x) + (this.y * this.y)));
        if (length == 0.0f) {
            length = 1.0f;
        }
        this.x /= length;
        this.y /= length;
    }

    public final float length2() {
        return (float) Math.sqrt((double) ((this.x * this.x) + (this.y * this.y)));
    }

    public Vector2D multiply(float k) {
        return new Vector2D(this.x * k, this.y * k);
    }

    public static float dotProduct(Vector2D vector1, Vector2D vector2) {
        return (float) (((double) (vector1.length() * vector2.length())) * Math.cos(((double) getAngle(vector1, vector2)) * 0.017453292519943295d));
    }

    public Vector2D subtract(Vector2D v) {
        return new Vector2D(this.x - v.x, this.y - v.y);
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(this.x + v.x, this.y + v.y);
    }
}