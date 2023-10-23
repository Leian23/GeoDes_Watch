package com.example.geodes_____watch.AlertSection.alerts_adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.R;

import java.util.List;

public class Adapter extends WearableRecyclerView.Adapter<Adapter.ViewHolder> {
    private List<DataModel> dataList;
    private Context context;
    private OnItemClickListener listener;

    public Adapter(List<DataModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alerts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel data = dataList.get(position);
        holder.bind(data);

        // You may need to handle onClickListeners for other interactive elements
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click here
                Toast.makeText(context, "Clicked: " + data.getTitleAlerts(), Toast.LENGTH_SHORT).show();

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

    public class ViewHolder extends WearableRecyclerView.ViewHolder {
        TextView TitleAlerts;
        TextView NotesAlerts;
        ImageView Imgcalendar;
        TextView repeatDescription;
        ImageView pinIcon;

        TextView ListAlarms;
        Switch alertSwitch;

        ImageView alertPref;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


             TitleAlerts = itemView.findViewById(R.id.TitleAlert);
             NotesAlerts = itemView.findViewById(R.id.NotesAlert);
             Imgcalendar = itemView.findViewById(R.id.calendarImage);
             repeatDescription = itemView.findViewById(R.id.Repeat);
             pinIcon = itemView.findViewById(R.id.pinalerts);
             ListAlarms = itemView.findViewById(R.id.alarmLists);
             alertSwitch = itemView.findViewById(R.id.AlertSwitch);
             alertPref = itemView.findViewById(R.id.AlertPreference);
        }

        public void bind(DataModel data) {
            TitleAlerts.setText(data.getTitleAlerts());
            NotesAlerts.setText(data.getNotesAlerts());
            Imgcalendar.setImageResource(data.getImgcalendar());
            repeatDescription.setText(data.getRepeatDescription());
            pinIcon.setImageResource(data.getPinIcon());
            ListAlarms.setText(data.getListAlarms());
            alertPref.setImageResource(data.getAlertPref());
            alertSwitch.setChecked(data.getAlertSwitch1());
        }
    }



    public interface OnItemClickListener {
        void onItemClick(DataModel data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
