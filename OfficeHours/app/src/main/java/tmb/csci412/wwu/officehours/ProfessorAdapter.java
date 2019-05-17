package tmb.csci412.wwu.officehours;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tullyj2 on 4/28/19.
 */

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView hourTextView;
        public TextView officeTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.prof_name);
            hourTextView = (TextView) itemView.findViewById(R.id.hours);
            officeTextView = (TextView) itemView.findViewById(R.id.office);
        }
    }

    private List<ProfItem> profList;

    public ProfessorAdapter(List<ProfItem> profs) {
        profList = profs;
    }

    @Override
    public ProfessorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View professorView = inflater.inflate(R.layout.item_professor, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(professorView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProfessorAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ProfItem prof = profList.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        TextView hoursText = viewHolder.hourTextView;
        TextView officeText = viewHolder.officeTextView;

        textView.setText(prof.getName());
        hoursText.setText("Hours: " + prof.getHours());
        officeText.setText("Office: " + prof.getRoom());
    }

    @Override
    public int getItemCount() {
        return profList.size();
    }

}