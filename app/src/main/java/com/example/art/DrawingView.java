package com.example.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private Paint paint;
    private Canvas canvas;
    private Bitmap bitmap;
    private List<Path> paths;
    private Path currentPath;
    private int currentColor;
    private int strokeWidth;
    private boolean isEraser = false;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        currentColor = Color.BLACK;
        paint = new Paint();
        paint.setColor(Color.BLACK); // 초기 선 색상 설정
        strokeWidth = 5; // 초기 선 굵기 설정
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        paths = new ArrayList<>();
    }

    public void setColor(int color) {
        currentColor = color;
        isEraser = false;
        paint.setColor(currentColor);
    }

    public void setEraser() {
        isEraser = true;
        paint.setColor(Color.WHITE); // 지우개는 흰색 선
    }

    public void setStrokeWidth(int width) {
        strokeWidth = width;
        paint.setStrokeWidth(strokeWidth);
    }

    public void clear() {
        paths.clear();
        invalidate();
    }

    public void saveDrawing() {
        String filename = "drawing.png";
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path path : paths) {
            paint.setColor(path.color);
            paint.setStrokeWidth(path.strokeWidth);
            canvas.drawPath(path.path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.color = isEraser ? Color.WHITE : currentColor; // 지우개 모드일 경우 흰색으로 그립니다.
                currentPath.strokeWidth = strokeWidth;
                currentPath.path.moveTo(x, y);
                paths.add(currentPath);
                return true;
            case MotionEvent.ACTION_MOVE:
                currentPath.path.lineTo(x, y);
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    private class Path {
        android.graphics.Path path = new android.graphics.Path();
        int color;
        int strokeWidth;
    }
}
