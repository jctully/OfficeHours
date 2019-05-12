package tmb.csci412.wwu.officehours;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    ArrayList<ProfessorContent> professorContents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Bottom Navigation Bar
        BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Set functionality for each button
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.prof:
//                        Toast.makeText(MainActivity.this, "Pressed Prof", Toast.LENGTH_SHORT).show();
//                        Intent profList = new Intent(MainActivity.this, CardActivity.class);
//                        startActivity(profList);
                        ProfListFragment pFragment = new ProfListFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.prof_list_container, pFragment)
                                .commit();
                        break;
                    case R.id.login:
                        Toast.makeText(MainActivity.this, "Pressed Login", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        break;
                    case R.id.map_view:
                        Toast.makeText(MainActivity.this, "Pressed Map View", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        // FIREBASE Start-Up
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference locRef = db.getReference("locations")
                .child("CF_Floor4")
                .child("dept");

        locRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "FIREBASE Set-Up Failed", databaseError.toException());
            }
        });
    }
}