package com.example.cielo.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import java.util.ArrayList;

public class Frag_alert extends Fragment {
    private static final String TAG = "showAlert";
    String time, tmpname;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private String title, text, id;
    Boolean exists;
    ArrayList<String> comment, commentID, commenttime;

    public static com.example.cielo.fragments.Frag_alert newInstance() {
        return new com.example.cielo.fragments.Frag_alert();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.decide_delete, container, false);
        tmpname = user.getUid().toString();
        Bundle fromitem = getArguments();
        time = fromitem.getString("time");

        v.findViewById(R.id.positiveButton).setOnClickListener(onClickListener);
        v.findViewById(R.id.negativeButton).setOnClickListener(onClickListener);
        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        private static final String TAG = "Comment Upload) ";


        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            switch (v.getId()) {
                case R.id.positiveButton:
                    deleteItem();


                case R.id.negativeButton:

                    fragmentManager.beginTransaction().remove(Frag_alert.this).commit();
                    fragmentManager.popBackStack();


            }
        }
    };

    private void deleteItem() {
        db.collection("Board").document(time)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        mannerdown(tmpname);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void mannerdown(String tmpname) {
        final float[] number = new float[1];
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("userInfo").document(tmpname);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String number1 = (String) document.getData().get("manner").toString();
                        String name = (String) document.getData().get("name");
                        Log.d("Name is this", name);
                        Log.d("Number is this", String.valueOf(number1));
                        number[0] = Float.valueOf(number1); //String -> Float
                        number[0] -= 0.01f;
                        db.collection("userInfo").document(tmpname)
                                .update("manner", number[0]);
                        Log.d("String value is this", String.valueOf(number[0]));


                    }
                }
            }
        });

    }
}
