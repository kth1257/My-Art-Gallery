package com.example.art;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class Frag3 extends Fragment {

    private View view;
    Button btn_logout;
    private FirebaseAuth mFirebaseAuth;

    View.OnClickListener cl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();

        btn_logout = view.findViewById(R.id.btn_logout);


        cl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch ( view.getId() ) {
                    case R.id.btn_logout:
                        //로그아웃 하기
                        mFirebaseAuth.signOut();

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };
        //탈퇴 처리
        //mFirebaseAuth.getCurrentUser().delete();
        btn_logout.setOnClickListener(cl);
        return view;
    }
}
