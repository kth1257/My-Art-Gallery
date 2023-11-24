package com.example.art;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

public class ColorPicker extends AppCompatActivity {

    ColorPickerView colorPickerView;
    TextView tv;
    LinearLayout l1;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.color_picker);
        colorPickerView = findViewById(R.id.colorPickerView);
        BrightnessSlideBar brightnessSlideBar = findViewById(R.id.brightnessSlide);
        AlphaSlideBar alphaSlideBar = findViewById(R.id.alphaSlideBar);
        l1 = findViewById(R.id.colorlayout);
        tv = findViewById(R.id.colorcode);

        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                l1.setBackgroundColor(envelope.getColor());
                tv.setText(envelope.getHexCode());
                // 선택된 컬러를 메인 화면으로 전달하고 그림 그리는 기능에 적용할 수 있습니다.
                int selectedColor = envelope.getColor();
                // 선택된 컬러를 메인 화면에 전달하는 로직을 추가하세요.
            }
        });
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);
        colorPickerView.attachAlphaSlider(alphaSlideBar);

    }


}
