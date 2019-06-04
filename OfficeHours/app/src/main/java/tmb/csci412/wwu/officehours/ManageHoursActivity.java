package tmb.csci412.wwu.officehours;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by farriem on 5/11/19.
 */

public class ManageHoursActivity extends AppCompatActivity {
    private TextView studWaitlist;
    static String profName;
    public final String TAG = "MANAGEHOURSACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managehours);

        final String profEmail;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                profEmail = null;
            }
            else {
                profEmail = extras.getString("email");
            }
        }
        else {
            profEmail = (String) savedInstanceState.getSerializable("email");
        }

        studWaitlist = findViewById(R.id.mgrWaitlist);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Try to get Phil's Waitlist NOTE: this will be changed later,
        // we are just testing if we can query Firestore
        db.collection("professors")
                .whereEqualTo("email", profEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            profName = document.getId();
                            ArrayList<String> studList = (ArrayList<String>) document.get("student_list");
                            for (int i=0; i<studList.size(); i++) {
                                Log.d(TAG, studList.get(i));
                                studWaitlist.setText(studWaitlist.getText() + "\n\n" + (i + 1) + ". " + studList.get(i));
                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        final Switch onlineSwitch = (Switch) findViewById(R.id.onlineSwitch);
        onlineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                DocumentReference profRef = db.collection("professors").document(profName);
                if(isChecked) {
                    profRef
                        .update("online", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
                } else {
                    profRef
                            .update("online", false)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }

            }
        });
        if (profName != null) {
            DocumentReference profRef = db.collection("professors").document(profName);
            profRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() ) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() ) {
                            Log.d(TAG, "Successfully retrieved snapshot");
                            if (document.get("online").equals(true) ) {
                                onlineSwitch.setChecked(true);
                            }
                        }
                    }
                }
            });
        }
        Button notifyStudentsButton = (Button) findViewById(R.id.mgrNotifyButton);
        notifyStudentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference profRef = db.collection("professors").document(profName);
                profRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String topStudent = ((ArrayList<String>) task.getResult().get("student_list")).get(0);
                            profRef.update("student_list", FieldValue.arrayRemove(topStudent));


                        }
                    }
                });

            }
        });

        db.collection("professors")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    break;
                                case MODIFIED:
                                    studWaitlist.setText("Student Waitlist");
                                    modifyProfList(dc.getDocument());
                                    ArrayList<String> studList = (ArrayList<String>) dc.getDocument().get("student_list");
                                    for (int i=0; i<studList.size(); i++) {
                                        Log.d(TAG, studList.get(i));

                                        studWaitlist.setText(studWaitlist.getText() + "\n\n" + (i + 1) + ". " + studList.get(i));
                                    }
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                    }
                });
    }

    public void modifyProfList(QueryDocumentSnapshot document) {
        ProfItem p;
        Map<String, Object> data = document.getData();
        for (int i=0; i<ProfessorContent.ITEMS.size(); i++) {
            p=ProfessorContent.ITEMS.get(i);
            if (p.getName().equals(document.getId())) {
                Log.d("AAAAAAAAAAAAAAAA", "Updating Prof " + document.getId());
                ProfessorContent.ITEMS.set(i, document.toObject(ProfItem.class));
            }
        }

    }
}
