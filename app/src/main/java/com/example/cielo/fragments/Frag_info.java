package com.example.cielo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

public class Frag_info extends Fragment {
    String TAG = "user)";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    TextView name;
    TextView age;
    TextView nickname;
    TextView manner_temp;
    ProgressBar progress;
    Switch alarm_check;
    ImageView profile;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.info_frag,container,false);
        v.findViewById(R.id.myscrap).setOnClickListener(onClickListener);
        v.findViewById(R.id.myscrapp).setOnClickListener(onClickListener);
        name = (TextView)v.findViewById(R.id.username);
        age = (TextView)v.findViewById(R.id.birthdate);
        nickname = (TextView)v.findViewById(R.id.nickname);
        manner_temp = (TextView)v.findViewById(R.id.manner_temp);
        progress = (ProgressBar)v.findViewById(R.id.progress_manner) ;
        alarm_check = (Switch)v.findViewById(R.id.push_switch);
        alarm_check.setOnCheckedChangeListener(new SwitchListener());
        profile = (ImageView)v.findViewById(R.id.profileImage);
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder dlg;
                dlg = new AlertDialog.Builder(getContext());
                dlg.setMessage("프로필사진을 변경하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("네",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        profileCrop();
                                    }
                                }
                        ).setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }
                );

                AlertDialog alert = dlg.create();
                alert.show();
            }
        });
        return v;
    }

    private void profileCrop() {

        startToast("정사각형에 맞게 잘라주세요.");
        CropImage.activity()
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                StorageReference riversRef = storageRef.child(user.getUid());
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
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(user.getUid());
                        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    // Glide 이용하여 이미지뷰에 로딩
                                    profile.setScaleType(ImageView.ScaleType.FIT_XY);
                                    profile.setMaxWidth(150);
                                    Glide.with(getActivity())
                                            .load(task.getResult())
                                            .apply(new RequestOptions().circleCrop())
                                            .into(profile);
                                    startToast("프로필 사진을 업로드 중입니다.");

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




    public void onStart() {


        super.onStart();


        StorageReference ref = FirebaseStorage.getInstance().getReference().child(user.getUid());
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    profile.setScaleType(ImageView.ScaleType.FIT_XY);
                    if(getActivity()!=null) {
                        Glide.with(getActivity())
                                .load(task.getResult())
                                .apply(new RequestOptions().circleCrop())
                                .into(profile);
                    }

                } else {
                    profile.setMaxWidth(100);
                    profile.setImageResource(R.drawable.profiledefault);
                }
            }
        });




        DocumentReference docRef = db.collection("userInfo").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name.setText(String.valueOf(document.get("name")));
                        age.setText(String.valueOf(document.get("age"))+"세");
                        nickname.setText(String.valueOf(document.get("nickname")));
                        float manner = Float.parseFloat(document.get("manner").toString());
                        manner_temp.setText(String.format("%.2f", manner)+"°C");
                        progress.setProgress(Integer.parseInt(String.valueOf((int)manner)));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }
        });



    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.myscrap:
                    // ((MainActivity)getActivity()).replaceFragment(Frag_padSearch.newInstance(),2);
                    ((MainActivity)getActivity()).replaceAddFragment(new Frag_scrap(),"scrap");
                    break;
                case R.id.myscrapp:
                    ((MainActivity)getActivity()).replaceAddFragment(new Frag_myboard(),"scrap");
                    break;

            }
        }
    };

    class SwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
                startToast("알람이 설정되었습니다");
            else
                startToast("알람이 해제되었습니다");
        }
    }



    private void startToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

}

