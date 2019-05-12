package tmb.csci412.wwu.officehours;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity {

    ArrayList<ProfessorContent.ProfItem> professorContents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        // Lookup the recyclerview in activity layout
        RecyclerView rvProfs = (RecyclerView) findViewById(R.id.rvProfs);

        // Initialize contacts
        ProfessorContent pc = new ProfessorContent();
        if (savedInstanceState == null) {
            for(int i=0; i<20; i++) {
                ProfessorContent.ProfItem newItem =  pc.createProfItem("Professor " +
                        ++ProfessorContent.lastProfId, "Office", "startTime", "endTime");
                pc.addItem(newItem);
            }
        }
        // Create adapter passing in the sample user data
        ProfessorAdapter adapter = new ProfessorAdapter(professorContents);
        // Attach the adapter to the recyclerview to populate items
        rvProfs.setAdapter(adapter);
        // Set layout manager to position the items
        rvProfs.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
    }
}
