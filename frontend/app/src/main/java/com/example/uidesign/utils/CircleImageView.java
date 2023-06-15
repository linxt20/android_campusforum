package com.example.uidesign.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.uidesign.R;

public class CircleImageView extends AppCompatImageView {
    private Paint borderPaint;
    private Path clipPath;

    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(4f);

        clipPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        clipPath.reset();
        float halfWidth = getWidth() / 2f;
        float halfHeight = getHeight() / 2f;
        float radius = Math.min(halfWidth, halfHeight);

        clipPath.addCircle(halfWidth, halfHeight, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);

        super.onDraw(canvas);

        canvas.drawCircle(halfWidth, halfHeight, radius, borderPaint);
    }
}
