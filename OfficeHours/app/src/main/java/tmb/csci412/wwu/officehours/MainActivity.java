package tmb.csci412.wwu.officehours;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity  {

    private String TAG = "MainActivity";
    ArrayList<ProfItem> professorContents;

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // This viewHolder will have all required values.
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            ProfItem thisItem = ProfessorContent.ITEMS.get(position);
            //Toast.makeText(MainActivity.this, "You Clicked: " + position, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this, ProfessorPage.class);
            i.putExtra("position",position);

            startActivity(i);
        }
    };

    private ProfessorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain location permissions
        requestPermission();



        // Create AppBar Menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.inflateMenu(R.menu.option_menu);
        myToolbar.setTitle("Office Hours");
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                // SETTINGS/USAGE
                if(item.getItemId() == R.id.settings) {
                    Toast.makeText(MainActivity.this, "Usage selected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, UsageActivity.class);
                    startActivity(intent);
                }


                // LOGIN
                if(item.getItemId() == R.id.login) {
                    Toast.makeText(MainActivity.this, "Login selected", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }

                // SEARCH
                if(item.getItemId() == R.id.action_search){
                    SearchView searchView = (SearchView) item.getActionView();

                    searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            adapter.getFilter().filter(query);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            adapter.getFilter().filter(newText);
                            return true;
                        }
                    });


                }
                return false;
            }
        });

        // RecyclerView Professor List

        // Initialize contacts
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lookup the recyclerview in activity layout
        final RecyclerView rvProfs = (RecyclerView) findViewById(R.id.rvProfs);

        //pull prof data from firestore, make objects and add to list
        db.collection("professors")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            ProfessorContent.addItem(document.toObject(ProfItem.class));
                        }

                        adapter = new ProfessorAdapter(ProfessorContent.ITEMS);
                        // Create adapter passing in the sample user data
                        adapter.notifyDataSetChanged();
                        // Attach the adapter to the recyclerview to populate items
                        rvProfs.setAdapter(adapter);
                        adapter.setOnItemClickListener(onItemClickListener);


                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

        //professor object modified
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
                            modifyProfList(dc.getDocument());
                            adapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
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
                ProfessorContent.ITEMS.set(i, document.toObject(ProfItem.class));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1);
    }


}