package com.hammad.todo_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.todo_app.MainActivity;
import com.hammad.todo_app.Model.TasksModel;
import com.hammad.todo_app.R;
import com.hammad.todo_app.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private List<TasksModel> tasksList;
    private MainActivity activity;
    private DatabaseHandler db;

    public TasksAdapter(MainActivity activity, DatabaseHandler db){
        this.activity = activity;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        TasksModel item = tasksList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(item.isStatus());
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if(isChecked){
                    db.updateStatus(item.getId(), 1);}
                    else {
                        db.updateStatus(item.getId(), 0);
                    }
                    activity.refreshRadioButton();
                }
                activity.showActiveItemCount();
            }
        });
    }

    public int getItemCount(){
        return tasksList.size();
    }

    public void setTask(List<TasksModel> tasksList){
        this.tasksList = new ArrayList<>();
        this.tasksList = tasksList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todo_checkbox);
        }
    }

}
