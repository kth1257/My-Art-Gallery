package com.example.art;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends FrameLayout {

    private Paint paint;
    private Canvas canvas;
    private Bitmap bitmap;
    private List<DrawingPath> paths;
    private DrawingPath currentPath;
    private int currentColor;
    private int strokeWidth;
    private boolean isEraser = false;

    // 예시를 보여줄 View
    private View lineWidthExample;

    private PictureAdapter pictureAdapter;

    // PictureAdapter와 연결할 리스트
    private List<Picture> savedDrawings;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        savedDrawings = new ArrayList<>();
    }

    // DrawingView 클래스에 현재 사용 중인 색상을 업데이트하는 메서드 추가
    public void setCurrentColor(int color) {
        currentColor = color;
        if (paint != null) {
            paint.setColor(currentColor);
        }
    }


    private void init() {
        currentColor = Color.BLACK;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        int dpValue = 5; // dp 단위의 값
        float density = getResources().getDisplayMetrics().density;
        strokeWidth = (int) (dpValue * density); // px 단위의 값으로 변환
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        paths = new ArrayList<>();

        // 예시를 보여줄 레이아웃 초기화
        lineWidthExample = new LinearLayout(getContext());
        lineWidthExample.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                strokeWidth // 이 부분을 strokeWidth로 변경
        ));
        // 기본 예시를 화면에 표시
        addView(lineWidthExample);
    }

    public void setColor(int color) {
        currentColor = color;
        isEraser = false;
        paint.setColor(currentColor);
    }

    public void setEraser() {
        isEraser = true;
        currentColor = Color.WHITE; // 지우개 모드에서는 흰색으로 설정
        paint.setColor(currentColor);
    }

    // 지우개 모드 토글
    public void toggleEraserMode() {
        isEraser = !isEraser;

        if (isEraser) {
            setEraser();
        } else {
            setColor(currentColor);
        }
    }

    public void setStrokeWidth(int width) {
        strokeWidth = width;
        paint.setStrokeWidth(strokeWidth);
    }

    public void clear() {
        paths.clear();
        invalidate();
    }

    // 선 굵기 다이얼로그 표시
    public void showLineWidthDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_line_width);

        SeekBar seekBarLineWidth = dialog.findViewById(R.id.seekBarLineWidth);
        final View viewLineWidthExample = dialog.findViewById(R.id.ViewLineWidthExample);
        Button btnApplyLineWidth = dialog.findViewById(R.id.btnApplyLineWidth);

        seekBarLineWidth.setProgress(strokeWidth);

        seekBarLineWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // dp 단위의 값을 px 단위로 변환
                int dpValue = progress * 5;
                float density = getResources().getDisplayMetrics().density;
                int pxValue = (int) (dpValue * density);

                ViewGroup.LayoutParams params = viewLineWidthExample.getLayoutParams();
                params.height = pxValue; // 픽셀 단위의 값으로 높이 설정
                viewLineWidthExample.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnApplyLineWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strokeWidth = seekBarLineWidth.getProgress();
                paint.setStrokeWidth(strokeWidth);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // 선 굵기 적용
    public void applyLineWidth(int width) {
        // dp 단위의 값을 px 단위로 변환
        int dpValue = width;
        float density = getResources().getDisplayMetrics().density;
        strokeWidth = (int) (dpValue * density); // px 단위로 변환
        paint.setStrokeWidth(strokeWidth);

        // 선 굵기 예시를 표시하는 부분 업데이트
        ViewGroup.LayoutParams params = lineWidthExample.getLayoutParams();
        params.height = strokeWidth;
        lineWidthExample.setLayoutParams(params);
    }



    // 그림 저장 및 Picture 객체 만들기
    public void saveDrawingAsImage(Context context, String title, String emailId, String nickname, int like) {
        Bitmap drawingBitmap = createBitmapFromPaths();
        String filename = "drawing_image.png";
        saveBitmapToStorage(context, drawingBitmap, filename);

        // Picture 객체 생성 및 목록에 추가
        Picture picture = new Picture();
        picture.setPicture("images/" + filename);
        picture.setTitle(title);
        picture.setEmailId(emailId);
        picture.setNickname(nickname);
        picture.setLike(like);

        // RecyclerView 갱신
        if (pictureAdapter != null) {
            pictureAdapter.addPicture(picture);
            pictureAdapter.notifyDataSetChanged();
        }

        Toast.makeText(context, "Drawing saved as image", Toast.LENGTH_SHORT).show();
    }


    private void uploadImageToStorage(Context context, Bitmap bitmap, String filename, String title, String emailId, String nickname, int like) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + filename);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            Uri downloadUrl = taskSnapshot.getUploadSessionUri();

            // Picture 객체 생성 및 목록에 추가
            Picture picture = new Picture();
            picture.setPicture(downloadUrl.toString());
            picture.setTitle(title);
            picture.setEmailId(emailId);
            picture.setNickname(nickname);
            picture.setLike(like);

            savedDrawings.add(picture);

            Toast.makeText(context, "Image uploaded: " + downloadUrl, Toast.LENGTH_SHORT).show();
        });
    }

    // PictureAdapter와 연결
    public void setPictureAdapter(PictureAdapter pictureAdapter) {
        this.pictureAdapter = pictureAdapter;
        // 초기화할 때 RecyclerView 갱신
        if (pictureAdapter != null) {
            pictureAdapter.setArrayList(savedDrawings);
            pictureAdapter.notifyDataSetChanged();
        }
    }


    public Bitmap createBitmapFromPaths() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        for (DrawingPath path : paths) {
            paint.setColor(path.color);
            paint.setStrokeWidth(path.strokeWidth);
            canvas.drawPath(path.path, paint);
        }

        return bitmap;
    }

    private void saveBitmapToStorage(Context context, Bitmap bitmap, String filename) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
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
        for (DrawingPath path : paths) {
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
                currentPath = new DrawingPath();
                currentPath.color = isEraser ? Color.WHITE : currentColor;
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


    private static class DrawingPath {
        android.graphics.Path path = new android.graphics.Path();
        int color;
        int strokeWidth;
    }
}