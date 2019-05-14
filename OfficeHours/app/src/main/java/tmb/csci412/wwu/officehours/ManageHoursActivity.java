package tmb.csci412.wwu.officehours;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by farriem on 5/11/19.
 */

public class ManageHoursActivity extends AppCompatActivity {

    private TextView studWaitlist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managehours);


        studWaitlist = findViewById(R.id.mgrWaitlist);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Try to get Phil's Waitlist NOTE: this will be changed later,
        // we are just testing if we can query Firestore
        DocumentReference docRef = db.collection("professors").document("Phil Nelson");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();

                if (doc == null){
                    Log.d("LOG:", "No such document");
                }
                else{

                    String stud1 = doc.getString("stud1");
                    String stud2 = doc.getString("stud2");
                    String stud3 = doc.getString("stud3");

                    studWaitlist.setText(studWaitlist.getText() + "\n\n1. " + stud1 + "\n2. " + stud2 + "\n3. " + stud3);
                }
            }
        });
    }
}
