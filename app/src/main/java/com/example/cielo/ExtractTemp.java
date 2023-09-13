package com.example.cielo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.List;

public class ExtractTemp {

    String TAG = "ExtractTemp";
    FirebaseFirestore db;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public ExtractTemp() {
        db = FirebaseFirestore.getInstance();
    }
    public void getHottopic(List<DocumentSnapshot> ListofFireStore) {
        String ctime= new Timestamp(System.currentTimeMillis()).toString();
       String reset= timereset(ctime);
        db.collection("Board")
                .orderBy("commentNum", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "queryDocumentSnapshot is Empty");
                        } else {
                            int i = 0;
                            while(true) {
                                try {
                                    Log.d(TAG, "queryDocumentSnapshot is Add");
                                    ListofFireStore.add(queryDocumentSnapshots.getDocuments().get(i++));

                                } catch (Exception e){
                                    Log.d(TAG ,e.getMessage() + "query Document -> End of data");
                                    break;
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"!!!ERROR!!! " + e.getMessage());
                    }
                });
    }
    public void getBoardResult(List<DocumentSnapshot> ListofFireStore, String theme) {
        db.collection("Board")
                .whereEqualTo("theme",theme)
                .orderBy("key", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "queryDocumentSnapshot is Empty");
                        } else {
                            int i = 0;
                            while(true) {
                                try {
                                    ListofFireStore.add((QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(i++));

                                } catch (Exception e){
                                    Log.d(TAG ,e.getMessage() + "query Document -> End of data");
                                    break;
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"!!!ERROR!!! " + e.getMessage());
                    }
                });
    }
    public void getmyBoard(List<DocumentSnapshot> ListofFireStore, String id) {
        db.collection("Board")
                .whereEqualTo("id",id)
                .orderBy("key", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "queryDocumentSnapshot is Empty");
                        } else {
                            int i = 0;
                            while(true) {
                                try {
                                    ListofFireStore.add((QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(i++));

                                } catch (Exception e){
                                    Log.d(TAG ,e.getMessage() + "query Document -> End of data");
                                    break;
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"!!!ERROR!!! " + e.getMessage());
                    }
                });
    }

    public void getPadResult(List<DocumentSnapshot> ListofFireStore) {
        db.collection("PAD_ingredient")
                .orderBy("recommend", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "queryDocumentSnapshot is Empty");
                        } else {
                            int i = 0;
                            while(true) {
                                try {
                                    ListofFireStore.add(queryDocumentSnapshots.getDocuments().get(i++));

                                } catch (Exception e){
                                    Log.d(TAG ,e.getMessage() + "query Document -> End of data");
                                    break;
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"!!!ERROR!!! " + e.getMessage());
                    }
                });
    }

    public void getResultByOption(List<DocumentSnapshot> ListofFireStore, int minPrice, int maxPrice, int option) {


        switch(option){
            case 1: //추천수정렬
                db.collection("PAD_ingredient")
                        .orderBy("recommend", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    Log.d(TAG, "queryDocumentSnapshot is Empty");
                                } else {
                                    int i = 0;
                                    while(true) {
                                        try {
                                            ListofFireStore.add(queryDocumentSnapshots.getDocuments().get(i++));

                                        } catch (Exception e){
                                            Log.d(TAG ,e.getMessage() + "query Document -> End of data");
                                            break;
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"!!!ERROR!!! " + e.getMessage());
                            }
                        });
                break;
            case 2: //낮은가격순
                db.collection("PAD_ingredient")
                        .orderBy("pad_price")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    Log.d(TAG, "queryDocumentSnapshot is Empty");
                                } else {
                                    int i = 0;
                                    while(true) {
                                        try {
                                            ListofFireStore.add(queryDocumentSnapshots.getDocuments().get(i++));

                                        } catch (Exception e){
                                            Log.d(TAG ,e.getMessage() + "query Document -> End of data");
                                            break;
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"!!!ERROR!!! " + e.getMessage());
                            }
                        });
                break;
            case 3: //높은가격순
                db.collection("PAD_ingredient")
                        .orderBy("pad_price", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    Log.d(TAG, "queryDocumentSnapshot is Empty");
                                } else {
                                    int i = 0;
                                    while(true) {
                                        try {
                                            ListofFireStore.add(queryDocumentSnapshots.getDocuments().get(i++));

                                        } catch (Exception e){
                                            Log.d(TAG ,e.getMessage() + "query Document -> End of data");
                                            break;
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"!!!ERROR!!! " + e.getMessage());
                            }
                        });
        }


    } //end function
public String timereset(String time){

        String dfDate=time.substring(0,10);
        String str2=" 00:00:00.001";
            StringBuffer strBuffer1 = new StringBuffer(dfDate);
            StringBuffer strBuffer2 = new StringBuffer(str2);
            String result= strBuffer1.append(str2).toString();

        return result;

}

}
