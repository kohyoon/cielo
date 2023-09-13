package com.example.cielo.activity;
//찐최종

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cielo.R;
import com.example.cielo.database.Calendar;
import com.example.cielo.database.Member;
import com.example.cielo.database.Myscrap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MemberInitActivity extends AppCompatActivity {
    String TAG = "MEMBER_INFO ) ";
    int blood = 0;
    boolean uploaded = false; //프로필등록되었는지
    boolean clicked = false;//생리양 눌러졌는지 check
    boolean pass = false;//닉네임 통과여부
    EditText age, name, nick;
    ImageView profile, profileb;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    ImageView passimg;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            pass = false;
            passimg.setBackground(null);
        }
    };

    //edittext 키보드내리기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void profileCrop() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {

                startToast("프로필 사진을 업로드 중입니다.");
                Uri resultUri = result.getUri();
                StorageReference riversRef = storageRef.child(currentUser.getUid());
                UploadTask uploadTask= riversRef.putFile(resultUri);;
// Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(currentUser.getUid());
                        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    // Glide 이용하여 이미지뷰에 로딩

                                    uploaded = true;
                                    profile.setMaxWidth(100);
                                    profile.setScaleType(ImageView.ScaleType.FIT_XY);

                                    Glide.with(MemberInitActivity.this)
                                            .load(task.getResult())
                                            .apply(new RequestOptions().circleCrop())
                                            .into(profile);
                                    profileb.setImageResource(R.drawable.uploadcircle);

                                } else {
                                    Log.d(TAG, "프로필 다운로드 실패");
                                }
                            }
                        });
                    }
                });





            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberinfo);
        findViewById(R.id.small).setOnClickListener(onClickListener);
        findViewById(R.id.medium).setOnClickListener(onClickListener);
        findViewById(R.id.large).setOnClickListener(onClickListener);
        findViewById(R.id.nickCheck).setOnClickListener(onClickListener);
        profile = (ImageView)findViewById(R.id.mem_profile);
        profileb = (ImageView)findViewById(R.id.mem_profile_b);
        profile.setMaxWidth(60);
        profile.setMaxHeight(60);
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                profileCrop();
            }
        });

        passimg = findViewById(R.id.nickPass);
        name = (EditText)findViewById(R.id.mem_name);
        age = (EditText)findViewById(R.id.mem_age);
        nick = (EditText)findViewById(R.id.mem_nick);
        clicked= false;
        pass = false;
        nick.addTextChangedListener(textWatcher);
        findViewById(R.id.mem_done).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            switch (v.getId()) {
                case R.id.small:
                    blood= 1;
                    clicked= true;
                    break;
                case R.id.medium:
                    blood=2;
                    clicked= true;
                    break;
                case R.id.large:
                    blood=3;
                    clicked= true;
                    break;

                case R.id.nickCheck:
                    pass =false;
                    String nick1 = nick.getText().toString();
                    if(nick1.length()>0){
                        db.collection("userInfo")
                                .whereEqualTo("nickname", nick1)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            passimg.setBackgroundResource(R.drawable.checked);
                                            pass = true;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //중복임
                                                pass= false;
                                                passimg.setBackground(null);
                                                startToast("이미 사용중인 닉네임입니다.");
                                                nick.setText(null);

                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                    }
                    else{
                        passimg.setBackground(null);
                        startToast("닉네임이 입력되지 않았습니다.");
                    }
                    break;


                case R.id.mem_done:
                    String name_ = name.getText().toString();
                    String nick_ = nick.getText().toString();
                    String age_ = age.getText().toString();
                    if(!uploaded){
                        startToast("프로필 사진을 업로드 해주세요.");
                    }
                    else if(uploaded&&clicked  && name_.length()>0 && nick_.length()>0 && age_.length()>0){
                        if(pass == false) {
                            startToast("닉네임 중복체크 해주세요.");
                        }
                        else{
                            Member mem = new Member();
                            mem.setName(name_);
                            mem.setAge(Integer.parseInt(age_));
                            mem.setNickname(nick_);
                            mem.setKey(currentUser.getUid());
                            mem.setBlood(blood);
                            db.collection("userInfo").document(currentUser.getUid()).set(mem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Success on member registration");
                                    finish();
                                    openActivity(MainActivity.class);
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Failed to register product", e);
                                        }
                                    });

                            db.collection("Myscrap").document(currentUser.getUid()).set(new Myscrap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Success on Myscrap registration");
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Failed to register scrap", e);
                                        }
                                    });

                            db.collection("Calendar").document(currentUser.getUid()).set(new Calendar(currentUser.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Success on Calendar registration");
                                    finish();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Failed to register calendar", e);
                                        }
                                    });

                        }
                    }
                    else{
                        startToast("회원정보가 모두 입력되지 않았습니다.");
                    }
                    break;
            }
        }
    };

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void openActivity(Class c) {
        Intent intent = new Intent(this, c);
        finish();
        startActivity(intent);
    }
}
