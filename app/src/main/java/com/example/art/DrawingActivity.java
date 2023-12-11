package com.example.art;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DrawingActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private ImageButton btn_color, btn_eraser, btn_save, btn_line_width, btn_fill, btn_back, btn_forward;
    private View paletteLayout;
    private int selectedColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        // XML에서 정의한 것
        drawingView = findViewById(R.id.drawingView);
        btn_color = findViewById(R.id.btn_color);
        btn_save = findViewById(R.id.btn_save);
        btn_line_width = findViewById(R.id.btn_line_width);
        btn_eraser = findViewById(R.id.btn_eraser);
        btn_fill = findViewById(R.id.btn_fill);
        btn_back = findViewById(R.id.btn_back);
        btn_forward = findViewById(R.id.btn_forward);

        // 팔레트 레이아웃 초기화
        paletteLayout = findViewById(R.id.paletteLayout);


        // 액티비티 또는 프래그먼트에서 DrawingView의 객체를 가져옴
        DrawingView drawingView = findViewById(R.id.drawingView);

        // 예시: Red 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton redButton = findViewById(R.id.red);
        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.RED);
            }
        });

        // Orange 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton orangeButton = findViewById(R.id.orange);
        orangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.parseColor("#FFA500")); // Orange 색상 코드
            }
        });

        // Yellow 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton yellowButton = findViewById(R.id.yellow);
        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.YELLOW);
            }
        });

        // Green 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton greenButton = findViewById(R.id.green);
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.GREEN);
            }
        });

        // Dark Green 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton darkGreenButton = findViewById(R.id.dark_green);
        darkGreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.parseColor("#006400")); // Dark Green 색상 코드
            }
        });

        // Blue 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton blueButton = findViewById(R.id.blue);
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.BLUE);
            }
        });

        // Dark Blue 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton darkBlueButton = findViewById(R.id.dark_blue);
        darkBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.parseColor("#00008B")); // Dark Blue 색상 코드
            }
        });

        // Purple 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton purpleButton = findViewById(R.id.purple);
        purpleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.parseColor("#800080")); // Purple 색상 코드
            }
        });

        // White 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton whiteButton = findViewById(R.id.white);
        whiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.WHITE);
            }
        });

        // Black 버튼에 대한 클릭 이벤트 리스너 설정
        ImageButton blackButton = findViewById(R.id.black);
        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DrawingView의 setCurrentColor 메서드 호출
                drawingView.setCurrentColor(Color.BLACK);
            }
        });

        // 버튼 클릭 리스너 설정
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });

        btn_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog();
            }
        });

        // 선 굵기 다이얼로그 표시
        ImageButton btnLineWidth = findViewById(R.id.btn_line_width);
        btnLineWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.showLineWidthDialog(DrawingActivity.this);
            }
        });

        // 지우개 모드 토글
        ImageButton btnEraser = findViewById(R.id.btn_eraser);
        btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.toggleEraserMode();
            }
        });

        // 팔레트 버튼에 클릭 리스너 추가
        ImageButton btnPalette = findViewById(R.id.btn_palette);
        btnPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팔레트 레이아웃의 현재 상태에 따라 보이기 또는 숨기기
                if (paletteLayout.getVisibility() == View.VISIBLE) {
                    hidePalette();
                } else {
                    showPalette();
                }
            }
        });
    }
    private void showPalette() {
        paletteLayout.setVisibility(View.VISIBLE);
    }

    private void hidePalette() {
        paletteLayout.setVisibility(View.GONE);
    }

    // 색상 선택 다이얼로그 표시
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

    // 그림 저장 다이얼로그 표시
    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("그림 저장");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = input.getText().toString();
                saveDrawing(title);
                Toast.makeText(DrawingActivity.this, "그림이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // 현재 사용자 이메일 가져오기
    private String getCurrentUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getEmail();
        } else {
            // 사용자가 로그인하지 않은 경우 처리
            return null;
        }
    }

    // 현재 사용자 닉네임 가져오기
    private String getCurrentUserNickname() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // FirebaseUser에서 닉네임 정보 가져오기
            String nickname = user.getDisplayName();

            // 만약 닉네임이 없다면 Firebase 프로필 업데이트를 수행하고 다시 가져오기
            if (nickname == null || nickname.isEmpty()) {
                // 닉네임이 없을 경우에 대한 처리를 추가로 작성할 수 있습니다.
                // 예를 들어, 기본 닉네임을 설정하거나 사용자에게 입력받는 등의 로직을 추가할 수 있습니다.
                nickname = "DefaultNickname";
            }

            return nickname;
        } else {
            // 사용자가 로그인하지 않은 경우 처리
            return null;
        }
    }


    // 그림 저장
    private void saveDrawing(String title) {
        String userId = getCurrentUserId();

        if (userId != null) {
            // Picture 객체 생성
            Picture picture = new Picture();
            picture.setUserId(userId); // 사용자의 UID 설정
            picture.setPicture("images/" + title + ".png");
            picture.setTitle(title);
            picture.setEmailId(getCurrentUserEmail());
            picture.setNickname(getCurrentUserNickname());
            picture.setLike(0); // 예시로 0으로 초기화
            picture.setDate(new Date()); // 현재 날짜 설정

            // Firebase Realtime Database에 저장
            DatabaseReference picturesRef = FirebaseDatabase.getInstance().getReference("Picture");
            String pictureKey = picturesRef.push().getKey(); // 고유한 키 생성
            picturesRef.child(pictureKey).setValue(picture);

            // 파일 이름은 제목으로 설정
            String filename = title + ".png";

            // 그림 데이터를 Firebase Storage에 업로드
            uploadDrawingToStorage(filename, pictureKey);

            Toast.makeText(this, "그림이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // Firebase Storage에 그림 업로드
    private void uploadDrawingToStorage(String filename, String pictureKey) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("images/" + filename);

        Bitmap drawingBitmap = drawingView.createBitmapFromPaths();
        // Convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        drawingBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the image
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            // Handle successful uploads
            Log.d("UploadTask", "Image upload successful");

            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Firebase Realtime Database에 이미지 다운로드 URL 저장
                DatabaseReference picturesRef = FirebaseDatabase.getInstance().getReference("Picture");
                picturesRef.child(pictureKey).child("picture").setValue(uri.toString());
                picturesRef.child(pictureKey).child("nickname").setValue(getCurrentUserNickname());

                Log.d("DownloadUrl", "Download URL: " + uri.toString());
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                Log.e("DownloadUrl", "Failed to get download URL");
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 추가 작업이 필요한 경우 여기에서 수행
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
