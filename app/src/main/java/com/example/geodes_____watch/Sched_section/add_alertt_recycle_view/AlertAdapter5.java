package com.example.geodes_____watch.Sched_section.add_alertt_recycle_view;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlertAdapter5 extends WearableRecyclerView.Adapter<AlertAdapter5.ViewHolder> {
    private List<DataModel5> dataList;
    private Context context;
    private OnItemClickListener listener;
    private SparseBooleanArray selectedItems;

    public AlertAdapter5(List<DataModel5> dataList, Context context) {
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
        DataModel5 data = dataList.get(position);
        holder.bind(data, isSelected(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int clickedPosition = holder.getAdapterPosition(); // Get the position using the ViewHolder
                toggleSelection(clickedPosition);
                Log.d("Alert", "Position only");
                data.toggleSelected(data.getUnid());

            }
        });
    }

    public Set<String> getSelectedItemsIds() {
        Set<String> selectedIds = new HashSet<>();
        for (DataModel5 data : dataList) {
            if (!data.getSelectedIds().isEmpty()) {
                selectedIds.addAll(data.getSelectedIds());
            }
        }
        return selectedIds;
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

        public void bind(DataModel5 data, boolean isSelected) {
            // bind your data to views
            textView.setText(data.getAlertTitle()); // Ensure that DataModelSched has getTitleAlerts() method

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
        void onItemClick(DataModel5 data); // Update the interface to use DataModelSched
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
