package tmb.csci412.wwu.officehours;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


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

public class ProfessorPage extends AppCompatActivity {
    Dialog studentDialog;
    DocumentReference profRef;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_page);
        ButterKnife.bind(this);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        Intent myIntent = getIntent();
        final int position = myIntent.getIntExtra("position", 0);
        studentDialog = new Dialog(this);

        final ProfItem prof = ProfessorContent.ITEMS.get(position);

        TextView indicatorView = findViewById(R.id.indicator_light);
        ImageView picView = findViewById(R.id.prof_pic);
        TextView textView = findViewById(R.id.prof_name);
        TextView hoursText = findViewById(R.id.hours);
        final Button joinQueue = findViewById(R.id.joinQueue);

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
        listLength.setText("Queue Size: " + prof.getStudent_list().size());





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
                                    if (ProfessorContent.ITEMS.get(position).isOnline()) {
                                        gd.setColor(getResources().getColor(R.color.colorGreen));
                                    } else {
                                        gd.setColor(getResources().getColor(R.color.colorRed));
                                    }
                                    listLength.setText("List: " + ProfessorContent.ITEMS.get(position).getStudent_list().size());
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                    }
                });
        profRef = db.collection("professors").document(prof.getName());
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

    /**
     * click join queue to toggle the bottom sheet
     */
    @OnClick(R.id.joinQueue)
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void onSubmitButtonClick(View v) {
        View sheet = findViewById(R.id.bottom_sheet);
        EditText studentNameInput = (EditText) sheet.findViewById(R.id.student_name_input);
        EditText studentTopicInput = (EditText) sheet.findViewById(R.id.student_topic_input);
        final Button submitButton = (Button) sheet.findViewById(R.id.submit_button);

        studentNameInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                   return true;
                }
                return false;
            }
        });

        final String studentName = studentNameInput.getText().toString();
        final String studentTopic = studentTopicInput.getText().toString();

        // Email Empty
        if(TextUtils.isEmpty(studentName)) {
            studentNameInput.setError("This field cannot be empty!");
            return;
        }
        Log.d("AAAAAAAAAAAAAAAAAAAAAA", "In OnCLick");
        profRef
                .update("student_list", FieldValue.arrayUnion(studentName))
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
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

}
