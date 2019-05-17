package tmb.csci412.wwu.officehours;


import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    ArrayList<ProfItem> professorContents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create AppBar Menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.inflateMenu(R.menu.option_menu);
        myToolbar.setTitle("Office Hours");
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.settings) {
                    Toast.makeText(MainActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                }
                if(item.getItemId() == R.id.login) {
                    Toast.makeText(MainActivity.this, "Login selected", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
                return false;
            }
        });

        // RecyclerView Professor List

        // Initialize contacts
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lookup the recyclerview in activity layout
        final RecyclerView rvProfs = (RecyclerView) findViewById(R.id.rvProfs);



        //if (savedInstanceState == null) {
            db.collection("professors")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    ProfessorContent.addItem(new ProfItem(document.getId(),
        document.getString("building"), document.getString("dep"), document.getString("room"), document.getString("email"),
        document.getString("hours"), document.getString("picURL")));
                                }

                                // Create adapter passing in the sample user data
                                ProfessorAdapter adapter = new ProfessorAdapter(ProfessorContent.ITEMS);
                                // Attach the adapter to the recyclerview to populate items
                                rvProfs.setAdapter(adapter);

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
//            for(int i=0; i<5; i++) {
//                ProfItem newItem = new ProfItem();
//                ProfessorContent.addItem(newItem);
//            }
//            ProfessorContent.addItem(new ProfItem(document.getId(),
//                    document.getString("building"), document.getString("dep"), document.getString("room"), document.getString("email"),
//                    document.getString("hours"), document.getString("picURL")));
       // }

        // Set layout manager to position the items
        rvProfs.setLayoutManager(new LinearLayoutManager(this));

    }
}