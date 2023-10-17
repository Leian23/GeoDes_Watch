package com.example.geodes_____watch.Sched_section.add_alertt_recycle_view;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.AlertSection.alerts_adapter.DataModel; // Assuming DataModel is used here
import com.example.geodes_____watch.R;

import java.util.List;

public class AdapterSched extends WearableRecyclerView.Adapter<AdapterSched.ViewHolder> {
    private List<DataModelSched> dataList;
    private Context context;
    private OnItemClickListener listener;
    private SparseBooleanArray selectedItems;

    public AdapterSched(List<DataModelSched> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        this.selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_alerts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModelSched data = dataList.get(position);
        holder.bind(data, isSelected(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getAdapterPosition(); // Get the position using the ViewHolder
                toggleSelection(clickedPosition);
                if (listener != null) {
                    listener.onItemClick(dataList.get(clickedPosition)); // Use the clicked position to get the correct data
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends WearableRecyclerView.ViewHolder {
        private TextView textView;  // replace with actual views from your layout

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.AlertItem);  // replace with actual IDs
        }

        public void bind(DataModelSched data, boolean isSelected) {
            // bind your data to views
            textView.setText(data.getTitleAlerts()); // Ensure that DataModelSched has getTitleAlerts() method

            // Change background color based on selection state
            itemView.setSelected(isSelected);
        }
    }

    private boolean isSelected(int position) {
        return selectedItems.get(position, false);
    }

    private void toggleSelection(int position) {
        selectedItems.put(position, !selectedItems.get(position, false));
        notifyItemChanged(position);
    }

    public interface OnItemClickListener {
        void onItemClick(DataModelSched data); // Update the interface to use DataModelSched
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
