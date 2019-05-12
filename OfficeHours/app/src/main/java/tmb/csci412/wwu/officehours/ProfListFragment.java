package tmb.csci412.wwu.officehours;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jungg2 on 5/11/19.
 */

public class ProfListFragment extends Fragment {

    /* Constructor to call in MainActivity*/
    public ProfListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // TODO create the fragment views
        // access string-array of restaurants and instantiate RestItem objects
        View recyclerView = inflater.inflate(R.layout.activity_card, container, false);
        RecyclerView rv = (RecyclerView) recyclerView.findViewById(R.id.rvProfs);

        ProfessorContent pc = new ProfessorContent();
        if (savedInstanceState == null) {
            for(int i=0; i<20; i++) {
                ProfessorContent.ProfItem newItem =  pc.createProfItem("Professor " +
                        ++ProfessorContent.lastProfId, "Office", "startTime", "endTime");
                pc.addItem(newItem);
            }
        }
        for (int i=0; i<pc.ITEMS.size(); i++) {
            Log.d("ITEMS", pc.ITEMS.get(i).getName());
        }
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        ProfessorAdapter adapter = new ProfessorAdapter(pc.ITEMS);
        rv.setAdapter(adapter);
        rv.setItemAnimator(new DefaultItemAnimator());

        return recyclerView;
    }

}
