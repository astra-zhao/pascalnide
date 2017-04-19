package com.duy.pascal.backend.lib.graph.graphic_model;

import android.graphics.Canvas;

import com.duy.pascal.backend.lib.graph.style.FillType;

/**
 * Created by Duy on 02-Mar-17.
 */

public class BarObject extends GraphObject {
    private int x1, y1, x2, y2;

    public BarObject(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(Canvas canvas) {
        if (fillStyle != FillType.EmptyFill) {
            canvas.drawRect(x1, y1, x2, y2, fillPaint);
        }
        canvas.drawRect(x1, y1, x2, y2, linePaint);
    }
}
