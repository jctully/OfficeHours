package tmb.csci412.wwu.officehours;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity {

    ArrayList<Professor> professors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Lookup the recyclerview in activity layout
        RecyclerView rvProfs = (RecyclerView) findViewById(R.id.rvProfs);

        // Initialize contacts
        professors = Professor.createProfList(20);
        // Create adapter passing in the sample user data
        ProfessorAdapter adapter = new ProfessorAdapter(professors);
        // Attach the adapter to the recyclerview to populate items
        rvProfs.setAdapter(adapter);
        // Set layout manager to position the items
        rvProfs.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
    }
}
