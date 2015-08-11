package com.shikha.todoapp.ui.objects;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shikha on 10/8/15.
 */
public class TaskViewHolder {


    public TextView mTaskTitle;
    public TextView mTaskContent;
    public TextView mCategory;
    public TextView mDueDate;
    public ImageView mAlarm;
    public ImageView mDelete;


    public TextView getmTaskTitle() {
        return mTaskTitle;
    }

    public void setmTaskTitle(TextView mTaskTitle) {
        this.mTaskTitle = mTaskTitle;
    }

    public TextView getmTaskContent() {
        return mTaskContent;
    }

    public void setmTaskContent(TextView mTaskContent) {
        this.mTaskContent = mTaskContent;
    }

    public TextView getmCategory() {
        return mCategory;
    }

    public void setmCategory(TextView mCategory) {
        this.mCategory = mCategory;
    }

    public TextView getmDueDate() {
        return mDueDate;
    }

    public void setmDueDate(TextView mDueDate) {
        this.mDueDate = mDueDate;
    }

    public ImageView getmAlarm() {
        return mAlarm;
    }

    public void setmAlarm(ImageView mAlarm) {
        this.mAlarm = mAlarm;
    }

    public ImageView getmDelete() {
        return mDelete;
    }

    public void setmDelete(ImageView mDelete) {
        this.mDelete = mDelete;
    }
}
