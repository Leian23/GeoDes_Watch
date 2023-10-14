// Adapter3.java

package com.example.geodes_____watch.recentAlerts_Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.geodes_____watch.R;

import java.util.List;

public class Adapter3 extends RecyclerView.Adapter<Adapter3.ViewHolder> {
    private List<DataModel3> dataList;
    private Context context;
    private OnItemClickListener listener;

    public Adapter3(List<DataModel3> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_alerts_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel3 data = dataList.get(position);
        holder.bind(data);

        // You may need to handle onClickListeners for other interactive elements
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click here
                Toast.makeText(context, "Clicked: " + data.getSchedTitle(), Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onItemClick(data);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageButton entryImageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.TitleAlert);
            entryImageButton = itemView.findViewById(R.id.Alerts); // Use the ImageButton ID from your layout
            // Initialize other views as needed
        }

        public void bind(DataModel3 data) {
            titleTextView.setText(data.getSchedTitle());
            entryImageButton.setImageResource(data.getEntryImage());

            // Set other data as needed
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DataModel3 data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
