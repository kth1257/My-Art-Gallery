package com.example.art;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Frag3 extends Fragment {

    private View view;
    private ImageView iv_profile;
    private TextView tv_nickname, tv_art_count;
    private Button btn_logout;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference picturesRef;

    View.OnClickListener cl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        picturesRef = FirebaseDatabase.getInstance().getReference("Picture");

        btn_logout = view.findViewById(R.id.btn_logout);
        iv_profile = view.findViewById(R.id.iv_profile);
        iv_profile.setImageResource(R.drawable.f1);
        tv_nickname = view.findViewById(R.id.tv_nickname);
        tv_art_count = view.findViewById(R.id.tv_art_count);
        cl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_logout:
                        // 로그아웃 하기
                        mFirebaseAuth.signOut();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        // 사용자 정보에서 닉네임 가져와서 설정
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userNickname = currentUser.getDisplayName();
            tv_nickname.setText(userNickname);
        }

        // 현재 사용자의 그린 작품 수를 실시간으로 감지하여 업데이트
        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            picturesRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int artCount = (int) snapshot.getChildrenCount();
                    tv_art_count.setText(String.valueOf(artCount));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 에러 처리
                    Log.e("Frag3", "Error fetching art count: " + error.getMessage());
                }
            });
        }

        // 탈퇴 처리
        // mFirebaseAuth.getCurrentUser().delete();
        btn_logout.setOnClickListener(cl);
        return view;
    }

    // 현재 로그인한 사용자의 UID 가져오기
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            // 사용자가 로그인하지 않은 경우 처리
            return null;
        }
    }
}