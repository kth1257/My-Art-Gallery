package com.example.art;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.ImageButton;

public class Frag2 extends Fragment {

    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2, container, false);

        Button btn_ndraw = view.findViewById(R.id.btn_ndraw);
        Button btn_mygal = view.findViewById(R.id.btn_mygal);
        btn_ndraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingActivity로 이동하는 Intent 생성
                Intent intent = new Intent(getActivity(), DrawingActivity.class);
                startActivity(intent);
            }
        });

        btn_mygal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // JoinDrawingActivity로 이동하는 Intent 생성
                Intent intent = new Intent(getActivity(), MyGalleryActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
