package tmb.csci412.wwu.officehours;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;

import static com.google.firebase.firestore.DocumentChange.Type.REMOVED;

public class ProfessorPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_page);
        Intent myIntent = getIntent();
        final int i = myIntent.getIntExtra("position", 0);

        final ProfItem prof = ProfessorContent.ITEMS.get(i);

        TextView indicatorView = findViewById(R.id.indicator_light);
        ImageView picView = findViewById(R.id.prof_pic);
        TextView textView = findViewById(R.id.prof_name);
        TextView hoursText = findViewById(R.id.hours);

        Button joinQueue = findViewById(R.id.joinQueue);

        findViewById(R.id.indicator_light);
        final GradientDrawable gd = (GradientDrawable)indicatorView.getBackground();
        if (prof.isOnline()) {
            gd.setColor(ContextCompat.getColor(indicatorView.getContext(), R.color.colorGreen));
        }
        else {
            gd.setColor(ContextCompat.getColor(indicatorView.getContext(), R.color.colorRed));
        }

        Picasso.get().load(prof.getPicURL()).into(picView);
        textView.setText(prof.getName());


        hoursText.setText(prof.getHours());





        // Testing ZONE
        TextView deptText = findViewById(R.id.deptFill);
        TextView buildOfficeText = findViewById(R.id.buildOfficeFill);
        TextView contactText = findViewById(R.id.contactFill);
        final TextView listLength = findViewById(R.id.waitListFill);

        deptText.setText(prof.getDept());
        buildOfficeText.setText(prof.getBuilding()+ " , " + prof.getRoom());
        contactText.setText(prof.getEmail());
        listLength.setText("Queue Size: " + prof.getStudent_list().length);





        //firebase: if change to db
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                    modifyProfList(dc.getDocument());
                                    if (ProfessorContent.ITEMS.get(i).isOnline()) {
                                        gd.setColor(getResources().getColor(R.color.colorGreen));
                                    } else {
                                        gd.setColor(getResources().getColor(R.color.colorRed));
                                    }
                                    listLength.setText("List: " + ProfessorContent.ITEMS.get(i).getStudent_list().length);
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                    }
                });
        final DocumentReference profRef = db.collection("professors").document(prof.getName());
        joinQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] newArr = prof.addToList("Joseph");
                profRef
                        .update("student_list", FieldValue.arrayUnion("Ducky"))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"You were added to queue",Toast.LENGTH_SHORT);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Error joining queue",Toast.LENGTH_SHORT);
                            }
                        });
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
                ProfessorContent.ITEMS.set(i, new ProfItem(document.getId(),
                        document.getString("building"), document.getString("dept"),
                        document.getString("room"), document.getString("email"),
                        document.getString("hours"), document.getString("picURL"),
                        document.getBoolean("online")));
            }
        }

    }



}
