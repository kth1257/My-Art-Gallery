package com.example.art;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {
    // PictureAdapter 클래스 내에서 상수로 Firebase 경로 정의
    private final String FIREBASE_PATH = "Picture";
    private ArrayList<Picture> arrayList;
    private Context context;

    public PictureAdapter(ArrayList<Picture> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override //실제 리스트뷰가 어댑터에 연결된 후 이쪽에서 뷰 홀더를 최초로 만들어냄
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        PictureViewHolder holder = new PictureViewHolder(view);
        return holder;
    }

    @Override // 각 아이템들에 대한 매칭을 시켜주는 역할
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        final int itemPosition = position; // final로 상수로 만들어줌

        Glide.with(holder.itemView)
                .load(arrayList.get(itemPosition).getPicture())
                .into(holder.iv_picture);
        holder.tv_title.setText(arrayList.get(itemPosition).getTitle());
        holder.tv_nickname.setText(arrayList.get(itemPosition).getNickname());
        holder.tv_like.setText(String.valueOf(arrayList.get(itemPosition).getLike()));

        // 좋아요 버튼 상태 업데이트
        boolean liked = arrayList.get(itemPosition).isLiked();
        int likeDrawable = liked ? R.drawable.like_filled : R.drawable.like_outline;

        // 벡터 드로어블을 ImageView에 설정
        holder.iv_like.setImageResource(likeDrawable);

        // 색상 지정
        int color = liked ? context.getResources().getColor(R.color.like_filled_color) : context.getResources().getColor(R.color.like_outline_color);
        holder.iv_like.setColorFilter(color);

        // 좋아요 버튼 클릭 이벤트 처리
        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭 이벤트가 발생하는지 로그 추가
                Log.d("ClickEvent", "Like button clicked at position: " + itemPosition);

                // Firebase Realtime Database에 좋아요 정보 업데이트
                DatabaseReference picturesRef = FirebaseDatabase.getInstance().getReference("Picture");
                String pictureKey = arrayList.get(itemPosition).getKey();

                if (pictureKey != null) {
                    DatabaseReference likeRef = picturesRef.child(pictureKey).child("like");
                    DatabaseReference likedRef = picturesRef.child(pictureKey).child("liked");

                    picturesRef.child(pictureKey).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            // 데이터가 없을 경우 초기값 설정
                            if (currentData.child("like").getValue() == null) {
                                currentData.child("like").setValue(0);
                                currentData.child("liked").setValue(false);
                                return Transaction.success(currentData);
                            }

                            Integer currentLike = currentData.child("like").getValue(Integer.class);
                            Boolean currentLiked = currentData.child("liked").getValue(Boolean.class);

                            // 좋아요 버튼이 클릭되었을 때의 처리
                            if (currentLiked != null && currentLiked) {
                                currentLike = currentLike != null ? currentLike + 1 : 1; // 좋아요 증가
                            } else {
                                currentLike = currentLike != null && currentLike > 0 ? currentLike - 1 : 0; // 좋아요 감소
                            }

                            // 업데이트된 값을 다시 설정
                            currentData.child("like").setValue(currentLike);
                            currentData.child("liked").setValue(!currentLiked);

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            if (error != null) {
                                Log.e("FirebaseTransaction", "Transaction failed", error.toException());
                            } else {
                                Log.d("FirebaseTransaction", "Transaction succeeded");
                            }
                        }
                    });
                }

                // UI 업데이트
                holder.tv_like.setText(String.valueOf(arrayList.get(itemPosition).getLike()));
                // 색상 업데이트
                updateLikeButtonColor(holder);
            }
        });
    }

    // 좋아요 버튼의 색상 업데이트 메서드
    private void updateLikeButtonColor(PictureViewHolder holder) {
        boolean liked = arrayList.get(holder.getAdapterPosition()).isLiked();
        int color = liked ? context.getResources().getColor(R.color.like_filled_color) : context.getResources().getColor(R.color.like_outline_color);
        Log.d("LikeButtonColor", "Color: " + color);
        holder.iv_like.setColorFilter(color);
    }



    @Override
    public int getItemCount() {
        // 삼향 연산자 arraylist가 null이 아니면? 참이면 arrayList.size()실행 참이 아니면 0실행
        return (arrayList != null ? arrayList.size() : 0);
    }

    public void setArrayList(List<Picture> pictureList) {
        this.arrayList = new ArrayList<>(pictureList);
        notifyDataSetChanged();
        Log.d("Adapter Update", "Data Updated");
    }

    public void addPicture(Picture picture) {
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(picture);
        notifyDataSetChanged();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_picture;
        TextView tv_title;
        TextView tv_nickname;
        TextView tv_like;
        ImageView iv_like;
        TextView tv_likeCount;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_picture = itemView.findViewById(R.id.iv_picture);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_nickname = itemView.findViewById(R.id.tv_nickname);
            this.tv_like = itemView.findViewById(R.id.tv_like);
            this.iv_like = itemView.findViewById(R.id.iv_like);
            this.tv_likeCount = itemView.findViewById(R.id.tv_like);
        }
    }
}
