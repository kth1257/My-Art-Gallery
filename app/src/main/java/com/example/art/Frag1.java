package com.example.art;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Frag1 extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Picture> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMain); // 아이디 연결
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        arrayList = new ArrayList<>(); // Picture 객체를 담을 어레이 리스트 (어댑터쪽으로)

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동

        databaseReference = database.getReference("Picture"); //DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); // 기존 배열리스트가 존재하지 않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터 List를 추출해냄
                    Picture picture = snapshot.getValue(Picture.class); // 만들어뒀던 Picture 객체에 데이터를 담는다.
                    arrayList.add(picture); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // DB를 가져오던중 에러 발생 시
                Log.e("Frag1", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        adapter = new PictureAdapter(arrayList, getContext());
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

        return view;
    }
}
