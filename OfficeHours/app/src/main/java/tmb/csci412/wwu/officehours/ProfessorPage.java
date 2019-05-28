package tmb.csci412.wwu.officehours;

import android.app.Dialog;
import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import android.location.LocationListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.firebase.firestore.DocumentChange.Type.REMOVED;

public class ProfessorPage extends AppCompatActivity {
    Dialog studentDialog;
    DocumentReference profRef;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;

    private static final String TAG = "ProfessorPage Class:";
    private double userLongitude;
    private double userLatitude;
    private LocationManager locManager;
    private LocationListener locListener;


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

        // Obtain UI elements
        final TextView listLength = findViewById(R.id.waitListFill);
        TextView indicatorView = findViewById(R.id.indicator_light);
        TextView textView = findViewById(R.id.prof_name);
        TextView hoursText = findViewById(R.id.hours);
        TextView deptText = findViewById(R.id.deptFill);
        TextView buildOfficeText = findViewById(R.id.buildOfficeFill);
        TextView contactText = findViewById(R.id.contactFill);
        ImageView picView = findViewById(R.id.prof_pic);
        final Button joinQueue = findViewById(R.id.joinQueue);


        // Determine if user is in range of buildings (Comm, Miller, Humanities)
        Location commLoc = new Location("Communications Building");
        commLoc.setLatitude(-122.485185);
        commLoc.setLongitude(48.732619);

        Location millLoc = new Location("Miller Hall");

        final double commLatitude = 48.732619;
        final double commLongitude = -122.485185;
        final double millerLatitude = 48.736419;
        final double millerLongitude = -122.484728;

        // check if permissions enabled
        if (ActivityCompat.checkSelfPermission(ProfessorPage.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Please enable location services", Toast.LENGTH_LONG).show();
        }

        // Locations
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle b){}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0,locListener);
        Location myLocation = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        userLatitude = myLocation.getLatitude();
        userLongitude = myLocation.getLongitude();

        // Check if they are in range
        float[] results = new float[1];

        // Comm Check
        if(prof.getBuilding().equalsIgnoreCase("Communications Facility")){
            Location.distanceBetween(userLatitude, userLongitude, commLatitude, commLongitude, results);
            joinQueue.setEnabled(checkIfInRange(results));

        }

        // Miller Check
        else if(prof.getBuilding().equalsIgnoreCase("Miller Hall")){
            Location.distanceBetween(userLatitude, userLongitude, millerLatitude, millerLongitude, results);
            joinQueue.setEnabled(checkIfInRange(results));

        }

        // Indicator Light set-up
        findViewById(R.id.indicator_light);
        final GradientDrawable gd = (GradientDrawable)indicatorView.getBackground();
        if (prof.isOnline()) {
            gd.setColor(ContextCompat.getColor(indicatorView.getContext(), R.color.colorGreen));
        }
        else {
            gd.setColor(ContextCompat.getColor(indicatorView.getContext(), R.color.colorRed));
        }

        // Populate UI elements
        Picasso.get().load(prof.getPicURL()).into(picView);
        textView.setText(prof.getName());
        hoursText.setText(prof.getHours());
        deptText.setText(prof.getDept());
        buildOfficeText.setText(prof.getBuilding()+ " , " + prof.getRoom());
        contactText.setText(prof.getEmail());
        listLength.setText("Queue Size: " + prof.getStudent_list().size());






        // TESTING ZONE
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
    
    public boolean checkIfInRange(float[] results){
        float distanceInMeters = results[0];
        return distanceInMeters < 100;
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
