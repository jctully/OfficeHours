package tmb.csci412.wwu.officehours;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tullyj2 on 4/28/19.
 */

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.ViewHolder> implements Filterable {

    private View.OnClickListener onItemClickListener;


    private static final int IO_BUFFER_SIZE = 4 * 1024;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView indicatorTextView;
        public ImageView picImageView;
        public TextView nameTextView;
        public TextView hourTextView;
        public TextView officeTextView;
        public AppCompatImageView chevronImageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);

            indicatorTextView = (TextView) itemView.findViewById(R.id.indicator_light);
            chevronImageView = (AppCompatImageView) itemView.findViewById(R.id.chevron_right);
            picImageView = (ImageView) itemView.findViewById(R.id.prof_pic);
            nameTextView = (TextView) itemView.findViewById(R.id.prof_name);
            hourTextView = (TextView) itemView.findViewById(R.id.hours);
            officeTextView = (TextView) itemView.findViewById(R.id.office);

        }
    }

    private List<ProfItem> profList;
    private List<ProfItem> profListFull;

    public ProfessorAdapter(List<ProfItem> profs) {
        profList = profs;
        profListFull = new ArrayList<>(profList);
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
        TextView indicatorView = viewHolder.indicatorTextView;
        ImageView picView = viewHolder.picImageView;
        TextView textView = viewHolder.nameTextView;
        TextView hoursText = viewHolder.hourTextView;
        TextView officeText = viewHolder.officeTextView;

        GradientDrawable gd = (GradientDrawable)indicatorView.getBackground();
        if (prof.isOnline()) {
            gd.setColor(ContextCompat.getColor(indicatorView.getContext(), R.color.colorGreen));
        }
        else {
            gd.setColor(ContextCompat.getColor(indicatorView.getContext(), R.color.colorRed));
        }

        Picasso.get().load(prof.getPicURL()).into(picView);
        textView.setText(prof.getName());
        hoursText.setText("Hours: " + prof.getHours());
        officeText.setText("Office: " + prof.getRoom());
    }

    @Override
    public int getItemCount() {
        return profList.size();
    }


    public void setOnItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }


    // FILTER ZONE

    @Override
    public Filter getFilter() {
        return profFilter;
    }

    private Filter profFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            List<ProfItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(profListFull);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ProfItem prof : profListFull) {
                    if (prof.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(prof);
                    }
                    // can add prof.getDept here to check if it contains filterPatt
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            profList.clear();
            profList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };





}