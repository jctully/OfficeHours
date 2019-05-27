package tmb.csci412.wwu.officehours;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by farriem on 5/11/19.
 */

public class ManageHoursActivity extends AppCompatActivity {

    private TextView studWaitlist;
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
//        DocumentReference docRef = db.collection("professors").document("Phil Nelson");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot doc = task.getResult();
//
//                if (doc == null){
//                    Log.d("LOG:", "No such document");
//                }
//                else{
//                    ArrayList<String> studList = (ArrayList<String>) doc.get("student_list");
//                    for (int i=0; i<studList.size(); i++) {
//                        studWaitlist.setText(studWaitlist.getText() + "\n\n" + (i + 1) + ". " + studList.get(i));
//                    }
//
//                }
//            }
//        });
        String currentProf;
        db.collection("professors")
                .whereEqualTo("email", profEmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                        @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed", e);
                            return;
                        }
                        List<String> profs = new ArrayList<>();
                        //TODO add variable profs for logins
                        ArrayList<String> studList = (ArrayList<String>) profs.get(0).get("student_list");
                        for (int i=0; i<studList.size(); i++) {
                            studWaitlist.setText(studWaitlist.getText() + "\n\n" + (i + 1) + ". " + studList.get(i));
                        }
                    }
                });

        db.collection("professors")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    dc.getDocument().getData();
                                    break;
                            }
                        }

                    }
                });
    }
}
