package com.hammad.todo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hammad.todo_app.Adapter.TasksAdapter;
import com.hammad.todo_app.Model.TasksModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.hammad.todo_app.Utils.DatabaseHandler;

public class MainActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private TasksAdapter tasksAdapter;

    private DatabaseHandler db;
    private List<TasksModel> taskList;

    private EditText addTextET;
    private RadioGroup radioGroup;
    private RadioButton all_task, active_task, completed_task;
    private TextView item_left_label,clear_completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();
        radioGroup = findViewById(R.id.task_filter_radiogroup);
        all_task = findViewById(R.id.all);
        active_task = findViewById(R.id.active);
        completed_task = findViewById(R.id.completed);
        addTextET = findViewById(R.id.add_task_et);
        item_left_label = findViewById(R.id.item_left);
        clear_completed = findViewById(R.id.clear_completed);
        taskList = new ArrayList<>();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TasksAdapter(this, db);
        tasksRecyclerView.setAdapter(tasksAdapter);


        refreshRadioButton();
        showActiveItemCount();

        addTextET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    addTaskFromTextBox();
                    handled = true;
                }
                return handled;
            }
        });

        clear_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteCompletedTask();
                refreshTasksList();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
               refreshRadioButton();
            }
        });

    }

    private void showTasks(List<TasksModel> taskList){
        tasksAdapter.setTask(taskList);
    }

    @SuppressLint("NonConstantResourceId")
    public void refreshRadioButton(){
        int checkedId = radioGroup.getCheckedRadioButtonId();
        switch (checkedId){
            case R.id.all:
                refreshTasksList();
                break;
            case R.id.active:
                showActiveTask();
                break;
            case R.id.completed:
                showCompletedTask();
                break;
            default: refreshTasksList();
        }

    }

    private void refreshTasksList(){
        taskList = db.getAllTasks();
        showTasks(taskList);
        showActiveItemCount();
    }

    private void showActiveTask(){
        taskList = db.getActiveTask();
        showTasks(taskList);
    }

    private void showCompletedTask(){
        taskList = db.getCompletedTask();
        showTasks(taskList);
    }


    private void addTaskFromTextBox() {
        TasksModel task = new TasksModel();
        task.setTask(addTextET.getText().toString());
        db.insertTask(task);
        addTextET.setText("");
        refreshRadioButton();
    }

    public void showActiveItemCount(){
        int active_item_count = db.getActiveTask().size();
        StringBuilder item_left_words = new StringBuilder(String.valueOf(active_item_count)).append(" ")
                .append(active_item_count == 1 ? "Item ": "Items ")
                .append("Left");
        item_left_label.setText(item_left_words);

        if(db.getCompletedTask().size() > 0){
            clear_completed.setTypeface(null, Typeface.BOLD);
        }else{
            clear_completed.setTypeface(null, Typeface.NORMAL);
        }
    }

}
