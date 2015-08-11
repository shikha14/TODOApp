package com.shikha.todoapp.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.shikha.todoapp.R;
import com.shikha.todoapp.framework.object.Category;
import com.shikha.todoapp.framework.object.Task;
import com.shikha.todoapp.ui.receiver.AlarmReceiver;
import com.shikha.todoapp.utils.AppUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddNewTask extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    private PendingIntent pendingIntent;
    private FloatingActionButton mFabDone;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private TextView mDueDate;
    private Spinner mCategorySpinner;
    private EditText mTaskTile, mTaskContent, mCustomCategory;
    private TextInputLayout mCategoryContainer;
    private SwitchCompat mReminderSwitch;
    private Context mContext;
    private Boolean setReminder = false;
    private long dueDate = 0;
    private String customCategoryList = "n/a";
    private String category = "n/a";


    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    List<String> categoryList;

    @Override
    public void onBackPressed() {
        performBackpress();
        super.onBackPressed();
    }

    private void performBackpress() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        initializeComponent();
    }


    private void initializeComponent() {
        setUpActionBar();
        setCurrentDay();
        mContext = this;
        mFabDone = (FloatingActionButton) findViewById(R.id.fabDone);
        mFabDone.setOnClickListener(this);

        mDueDate = (TextView) findViewById(R.id.txtDueDate);
        mDueDate.setOnClickListener(this);

        mTaskTile = (EditText) findViewById(R.id.etTaskName);
        mTaskContent = (EditText) findViewById(R.id.etTaskContent);
        mCustomCategory = (EditText) findViewById(R.id.etCategory);

        mCategoryContainer = (TextInputLayout) findViewById(R.id.categoryTextContainer);

        mReminderSwitch = (SwitchCompat) findViewById(R.id.switchReminder);
        mReminderSwitch.setOnCheckedChangeListener(this);

        setUpCategorySpinner();

    }

    /*
    * Function to setup category spinner
    * */
    private void setUpCategorySpinner() {
        mCategorySpinner = (Spinner) findViewById(R.id.spnCategory);
        categoryList = new ArrayList<>();
        categoryList.add(AppUtils.SELECT_CATEGORY);
        List<String> tempList = AppUtils.getAllCategoryName();
        categoryList.addAll(tempList);
        int size = categoryList.size();
        categoryList.add(size, AppUtils.CREATE_CATEGORY);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(dataAdapter);
        mCategorySpinner.setOnItemSelectedListener(this);
    }

    private void setUpActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabDone:
                performSave();
                break;

            case R.id.txtDueDate:
                displayDatePicker();
                break;

        }
    }

    private void displayDatePicker() {
        showDialog(AppUtils.DATE_PICKER_ID);
    }

    /**
     * Function to set current date
     */
    private void setCurrentDay() {
        Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
    }

    /**
     * Function to perform save task operation
     */
    private void performSave() {
        if (validate()) {
            int taskId = AppUtils.getLatestTaskId() + 1;
            int catId;
            if (category.equals(AppUtils.CREATE_CATEGORY)) {
                catId = saveNewCategory();
            } else {
                catId = AppUtils.getCategoryIdByName(category);
            }
            saveNewTask(taskId, catId);
            clearAll();
            redirectToViewTask();
        }

    }

    /**
     * Function to validate required fields
     */
    private boolean validate() {
        if (category.equals("n/a")) {
            AppUtils.displayToast(mContext, "Please select category!!!");
            return false;
        } else if (category.equals(AppUtils.CREATE_CATEGORY) && mCustomCategory.getText().toString().length() == 0) {
            AppUtils.displayToast(mContext, "Please enter custom category name!!!");
            return false;
        } else if (mTaskTile.getText().toString().length() == 0) {
            AppUtils.displayToast(mContext, "Please enter task Title!!!");
            return false;
        } else if (dueDate == 0) {
            AppUtils.displayToast(mContext, "Please enter due date!!");
            return false;
        } else if (mTaskContent.getText().toString().length() == 0) {
            AppUtils.displayToast(mContext, "Please enter task content!!!");
            return false;
        } else
            return true;


    }

    private void redirectToViewTask() {
        Intent intent = new Intent(AddNewTask.this, ViewTask.class);
        startActivity(intent);
        finish();
    }

    /**
     * Function to save created task in db
     */
    private void saveNewTask(int taskId, int catId) {
        Task task = new Task();
        task.setCategoryId(catId);
        task.setTaskId(taskId);
        task.setTaskContent(mTaskContent.getText().toString());
        task.setDueDate(dueDate);
        if (setReminder)
            task.setReminder(1);
        else
            task.setReminder(0);
        task.setStatus(0);
        task.setTaskTitle(mTaskTile.getText().toString());
        task.save();
        if (setReminder)
            setAlarm(task);
    }


//    private void setAlarm(Task task) {
//        Intent myIntent = new Intent(AddNewTask.this, AlarmReceiver.class);
//        myIntent.putExtra(AppUtils.SEND_TASK_TITLE, task.getTaskTitle());
//        myIntent.putExtra(AppUtils.SEND_TASK_CONTENT, task.getTaskContent());
//        pendingIntent = PendingIntent.getBroadcast(AddNewTask.this, 0, myIntent, 0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC, dueDate, pendingIntent);
//    }

    private void setAlarm(Task task){

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra(AppUtils.SEND_TASK_TITLE, task.getTaskTitle());
        intent.putExtra(AppUtils.SEND_TASK_CONTENT, task.getTaskContent());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),task.getTaskId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, dueDate, pendingIntent);

    }

    private int saveNewCategory() {
        int catId = AppUtils.getLatestCategoryId();
        Category cat = new Category();
        cat.setCategoryId(catId + 1);
        cat.setCategoryName(mCustomCategory.getText().toString());
        cat.save();
        return catId + 1;
    }

    private void clearAll() {
        mCategorySpinner.setSelection(0);
        mTaskTile.setText("");
        mTaskContent.setText("");
        mCustomCategory.setText("");
        mReminderSwitch.setChecked(false);
        mDueDate.setText("Due Date");
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case AppUtils.DATE_PICKER_ID:
                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                DatePickerDialog dialog = new DatePickerDialog(this, pickerListener, year, month, day);
                dialog.getDatePicker().setMinDate(new Date().getTime());
                return dialog;


            case AppUtils.TIME_DIALOG_ID:
                // open time picker dialog.
                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        false);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // This method is called when dialog is closed
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            showDialog(AppUtils.TIME_DIALOG_ID);

        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hour = hourOfDay;
            minute = minutes;
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);
            mDueDate.setText(calendar.getTime().toString());
            dueDate = calendar.getTimeInMillis();

        }

    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Ignoring 1st position
        if (position != 0) {
            category = parent.getItemAtPosition(position).toString();
            if (category.equals(AppUtils.CREATE_CATEGORY)) {
                mCategoryContainer.setVisibility(View.VISIBLE);
                mCustomCategory.requestFocus();
            } else {
                mCategoryContainer.setVisibility(View.GONE);
                mTaskTile.requestFocus();
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchReminder:
                setReminder = isChecked;
                break;

        }
    }


}
