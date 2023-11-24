package com.example.art;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class DrawingActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private Button colorButton;
    private Button lineWidthButton;
    private Button eraserButton;

    private int selectedColor = Color.BLACK; // 초기 선택 색상을 검정으로 설정합니다.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        lineWidthButton = findViewById(R.id.lineWidthButton);
        eraserButton = findViewById(R.id.eraserButton);
        drawingView = findViewById(R.id.drawingView);
        colorButton = findViewById(R.id.colorButton);

        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog();
            }
        });
    }

    private void showColorPickerDialog() {
        new ColorPickerDialog.Builder(DrawingActivity.this)
                .setTitle("색상 선택")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("확인", new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                        selectedColor = envelope.getColor();
                        drawingView.setColor(selectedColor);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show();

    }
}
