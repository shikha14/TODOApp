package com.shikha.todoapp.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shikha.todoapp.framework.object.Task;
import com.shikha.todoapp.utils.AppUtils;

/**
 * Created by shikha on 10/8/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent != null) {
                String title = intent.getStringExtra(AppUtils.SEND_TASK_TITLE);
                String content = intent.getStringExtra(AppUtils.SEND_TASK_CONTENT);
                AppUtils.generateNotification(context, title, content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
