package au.edu.unimelb.eldercare.usersearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import au.edu.unimelb.eldercare.R;

public class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptor.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.user_search_cell, viewGroup, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TextView textView = viewHolder.nameTextView;
        textView.setText("John Smith");
        Button button = viewHolder.addButton;
        button.setText("Add Friend");
        button.setEnabled(true);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public Button addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.user_name);
            addButton = itemView.findViewById(R.id.add_button);
        }
    }
}
