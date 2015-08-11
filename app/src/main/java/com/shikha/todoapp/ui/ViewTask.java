package com.shikha.todoapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shikha.todoapp.R;
import com.shikha.todoapp.framework.object.Category;
import com.shikha.todoapp.framework.object.Task;
import com.shikha.todoapp.ui.objects.TaskViewHolder;
import com.shikha.todoapp.utils.AppUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ViewTask extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    FloatingActionButton mFabCreate;
    Toolbar mToolbar;
    ActionBar mActionBar;
    boolean hasTask = false;
    private ListView mTaskList;
    private LinearLayout mNoTaskFound;


    private TaskListAdapter mAdapter;
    private ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        initializeComponent();
    }

    private void initializeComponent() {
        setUpActionBar();
        mContext = this;
        taskList = new ArrayList<>();
        mFabCreate = (FloatingActionButton) findViewById(R.id.fabCreate);
        mFabCreate.setOnClickListener(this);
        mTaskList = (ListView) findViewById(R.id.taskList);
        mNoTaskFound = (LinearLayout) findViewById(R.id.noTaskContainer);
        mAdapter = new TaskListAdapter(mContext, taskList);
        mTaskList.setAdapter(mAdapter);
        checkForTask();

    }

    /**
     * Function to check existing task in db
     */
    private void checkForTask() {
        AppUtils.checkCategoryInDb();
        hasTask = AppUtils.checkTaskInDb();
        toggleListVisibility(hasTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForTask();
        notifyAdapter();
    }

    /**
     * Function to set action bar
     */
    private void setUpActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();

    }

    /**
     * Function to show/hide provided view
     */
    private void toggleListVisibility(boolean hasTask) {
        if (hasTask) {
            mTaskList.setVisibility(View.VISIBLE);
            mNoTaskFound.setVisibility(View.GONE);
        } else {
            mTaskList.setVisibility(View.GONE);
            mNoTaskFound.setVisibility(View.VISIBLE);
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabCreate:
                performCreate();
                break;

        }
    }

    /**
     * Function to redirect to create task activity
     */
    private void performCreate() {
        Intent intent = new Intent(ViewTask.this, AddNewTask.class);
        startActivity(intent);
    }

    /**
     * Custom Adapter class for task list
     */
    public class TaskListAdapter extends BaseAdapter {
        private Context context;
        ArrayList<Task> objects;
        private LayoutInflater inflater;

        public TaskListAdapter(Context context,
                               ArrayList<Task> objects) {
            this.context = context;
            this.objects = objects;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Task getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        TaskViewHolder viewHolder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder = new TaskViewHolder();
            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.add_task_description, null);
            }
            viewHolder.setmTaskTitle((TextView) convertView.findViewById(R.id.taskTitle));
            viewHolder.setmTaskContent((TextView) convertView.findViewById(R.id.taskContent));
            viewHolder.setmCategory((TextView) convertView.findViewById(R.id.categoryName));
            viewHolder.setmDueDate((TextView) convertView.findViewById(R.id.dueDate));
            viewHolder.setmAlarm((ImageView) convertView.findViewById(R.id.alarm));
            viewHolder.setmDelete((ImageView) convertView.findViewById(R.id.delete));


            final Task item = getItem(position);

            viewHolder.getmTaskTitle().setText(item.getTaskTitle());
            viewHolder.getmTaskContent().setText(item.getTaskContent());
            Long dueDate = item.getDueDate();
            Timestamp stamp = new Timestamp(dueDate);
            Date date = new Date(stamp.getTime());
            viewHolder.getmDueDate().setText(date.toString());
            int catId = item.getCategoryId();
            String catName = AppUtils.getCategoryNameById(catId);
            viewHolder.getmCategory().setText(catName);
            viewHolder.getmDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performDelete(item);

                }
            });

            if (item.getReminder() == 1)
                viewHolder.getmAlarm().setVisibility(View.VISIBLE);
            else
                viewHolder.getmAlarm().setVisibility(View.GONE);


            return convertView;
        }
    }

    /**
     * Function to perform delete task
     */
    private void performDelete(Task item) {
        item.setStatus(1);
        item.save();
        notifyAdapter();
    }

    /**
     * Function to notify data set change to the adapter
     */

    private void notifyAdapter() {
        checkForTask();
        if (hasTask) {
            ArrayList<Task> tempList = (ArrayList<Task>) AppUtils.getTaskFromDb();
            taskList.clear();
            taskList.addAll(tempList);
            mAdapter.notifyDataSetChanged();
        }

    }
}
