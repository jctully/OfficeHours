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

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    ArrayList<ProfessorContent.ProfItem> professorContents;
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
        // Lookup the recyclerview in activity layout
        RecyclerView rvProfs = (RecyclerView) findViewById(R.id.rvProfs);

        // Initialize contacts TEMP
        ProfessorContent pc = new ProfessorContent();
        if (savedInstanceState == null) {
            for(int i=0; i<20; i++) {
                ProfessorContent.ProfItem newItem =  pc.createProfItem("Professor " +
                        ++ProfessorContent.lastProfId, "Office", "startTime", "endTime");
                pc.addItem(newItem);
            }
        }
        // Create adapter passing in the sample user data
        ProfessorAdapter adapter = new ProfessorAdapter(ProfessorContent.ITEMS);
        // Attach the adapter to the recyclerview to populate items
        rvProfs.setAdapter(adapter);
        // Set layout manager to position the items
        rvProfs.setLayoutManager(new LinearLayoutManager(this));

    }
}