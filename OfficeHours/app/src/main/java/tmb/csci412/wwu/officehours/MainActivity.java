package tmb.csci412.wwu.officehours;


import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


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
        final ProfessorAdapter adapter = new ProfessorAdapter(ProfessorContent.ITEMS);;

        //pull prof data from firestore, make objects and add to list
        db.collection("professors")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            ProfessorContent.addItem(new ProfItem(document.getId(),
                                document.getString("building"), document.getString("dep"),
                                document.getString("room"), document.getString("email"),
                                document.getString("hours"), document.getString("picURL"),
                                    document.getBoolean("online")));
                        }

                        // Create adapter passing in the sample user data
                        adapter.notifyDataSetChanged();
                        // Attach the adapter to the recyclerview to populate items
                        rvProfs.setAdapter(adapter);

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
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
                            Log.d(TAG, "New city: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                            modifyProfList(dc.getDocument());
                            adapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                            break;
                    }
                }

            }
        });

        // Set layout manager to position the items
        rvProfs.setLayoutManager(new LinearLayoutManager(this));

    }

    public void modifyProfList(QueryDocumentSnapshot document) {
        ProfItem p;
        Map<String, Object> data = document.getData();
        for (int i=0; i<ProfessorContent.ITEMS.size(); i++) {
            p=ProfessorContent.ITEMS.get(i);
            if (p.getName().equals(document.getId())) {
                Log.d("AAAAAAAAAAAAAAAA", "Updating Prof " + document.getId());
                ProfessorContent.ITEMS.set(i, new ProfItem(document.getId(),
                        document.getString("building"), document.getString("dep"),
                        document.getString("room"), document.getString("email"),
                        document.getString("hours"), document.getString("picURL"),
                        document.getBoolean("online")));
            }
        }

    }
}