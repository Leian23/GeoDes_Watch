package com.example.geodes_____watch.recentAlerts_Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import com.example.geodes_____watch.R;

import java.util.List;

public class Adapter3 extends WearableRecyclerView.Adapter<Adapter3.ViewHolder> {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sched_item, parent, false);
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
                Toast.makeText(context, "Clicked: " + data.getTitleSetSched(), Toast.LENGTH_SHORT).show();

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
        TextView timeSched;
        TextView alarmsSaved;
        TextView titleSetSched;
        Switch alertSwitch;
        ImageButton entryImageButton;
        ImageView clockIc;
        ImageView calendarIc;

        ImageView alarmIc;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSched = itemView.findViewById(R.id.TimeSched);
            alarmsSaved = itemView.findViewById(R.id.alarmsSaved);
            titleSetSched = itemView.findViewById(R.id.TitleSetSched);
            alertSwitch = itemView.findViewById(R.id.AlertSwitch);
            entryImageButton = itemView.findViewById(R.id.Alerts);
            clockIc = itemView.findViewById(R.id.clockSchedIc);
            calendarIc = itemView.findViewById(R.id.calendarImage);
            alarmIc = itemView.findViewById(R.id.AlarmIc);




        }

        public void bind(DataModel3 data) {
            timeSched.setText(data.getTimeSched());
            alarmsSaved.setText(data.getAlarmsSaved());
            titleSetSched.setText(data.getTitleSetSched());
            alertSwitch.setChecked(data.isAlertSwitch());
            alarmIc.setImageResource(data.getAlertIc());
            clockIc.setImageResource(data.getClockIc());
            calendarIc.setImageResource(data.getCalendarIc());
        }
    }



    public interface OnItemClickListener {
        void onItemClick(DataModel3 data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
