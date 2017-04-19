package com.duy.pascal.backend.lib.graph.graphic_model;

import android.graphics.Canvas;

/**
 * Created by Duy on 02-Mar-17.
 */

public class CircleObject extends GraphObject {
    private int x, y, r;

    public CircleObject(int x, int y, int r) {
        this.x = x;
        this.y = y;
        this.r = r;

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, r, linePaint);
    }
}
