package com.shikha.todoapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.orm.query.Select;
import com.shikha.todoapp.R;
import com.shikha.todoapp.framework.object.Category;
import com.shikha.todoapp.framework.object.Task;
import com.shikha.todoapp.ui.ViewTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shikha on 10/8/15.
 */
public class AppUtils {

    //Variable to hold date picker id
    public static final int DATE_PICKER_ID = 999;
    //Variable to hold time picker id
    public static final int TIME_DIALOG_ID = 9999;



    public static final String  SEND_TASK_TITLE="task_title";
    public static final String  SEND_TASK_CONTENT="task_content";
    public static final String  SELECT_CATEGORY="Select Category";
    public static final String  CREATE_CATEGORY="Create New Category";

    /**
     * Function to display toast message to user
     *
     * @param context:context of the activity
     * @param msg:message     to display to the user
     */
    public static void displayToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

    }

    /**
     * Function to check whether category table is empty or not
     * If empty the add others in it.
     */
    public static void checkCategoryInDb() {
        try {
            Category category = Category.listAll(Category.class).get(0);

        } catch (Exception e) {
            Category undefinedCategory = new Category();
            undefinedCategory.setCategoryId(1);
            undefinedCategory.setCategoryName("Others");
            undefinedCategory.save();
        }
    }

    /**
     * Function to check whether task table is empty or not
     */
    public static boolean checkTaskInDb() {

        List<Task> taskList = new ArrayList<>();
        try {
            // taskList = Task.listAll(Task.class);
            taskList= Select.from(Task.class).orderBy("due_date Desc").where("status=?", new String[] {"0"}).list();
        } catch (Exception e) {
            e.printStackTrace();

        }
        if(taskList!=null && taskList.size()>0)
            return true;
        return false;
    }

    /**
     * Function to get task list
     */
    public static List<Task> getTaskFromDb() {
        List<Task> taskList = new ArrayList<>();
        try {
           // taskList = Task.listAll(Task.class);
            taskList= Select.from(Task.class).orderBy("due_date Desc").where("status=?", new String[] {"0"}).list();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return taskList;

    }

    /**
     * Function to get all category
     */
    public static List<Category> getAllCategory() {

        List<Category> categoryList = new ArrayList<>();
        try {
            categoryList = Category.listAll(Category.class);
        } catch (Exception e) {

        }
        return categoryList;

    }


    /**
     * Function to get all category
     */
    public static List<String> getAllCategoryName() {
        List<String> list = new ArrayList<>();
        List<Category> catList=getAllCategory();
        if(catList!=null && catList.size()>0) {
            for (Category category : catList)
            {
                list.add(category.getCategoryName());
            }
        }

        return list;

    }

    /**
     * Function to get all category
     */
    public static String getCategoryNameById(int id) {

        String categoryName = "";
        try {
            Category category = Category.find(Category.class, "category_id=" + id, null).get(0);
            categoryName = category.getCategoryName();
        } catch (Exception e) {

        }
        return categoryName;

    }


    /**
     * Function to get all category
     */
    public static int getCategoryIdByName(String name) {

        int id =0;
        try {
            Category category = Category.find(Category.class, "category_name=?", name).get(0);
           id = category.getCategoryId();
        } catch (Exception e) {

        }
        return id;

    }


    /**
     * Function to get latest category id
     */
    public static int getLatestCategoryId() {

        int categoryId = 0;
        try {
            List<Category> list = Category.listAll(Category.class);
            int size = list.size();
            Category category = list.get(size - 1);
            categoryId = category.getCategoryId();

        } catch (Exception e) {

        }
        return categoryId;

    }

    /**
     * Function to get latest category id
     */
    public static int getLatestTaskId() {

        int id = 0;
        try {
            List<Task> list = Task.listAll(Task.class);
            int size = list.size();
            Task task = list.get(size - 1);
            id = task.getTaskId();

        } catch (Exception e) {
            id=0;
        }
        return id;

    }

    public static void generateNotification(Context context,String title,String content) {
        NotificationManager mManager;
        mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context,ViewTask.class);
        String msg="You have a task due with following details:\nTitile:"+title+"\nContent:"+content;
        Notification notification = new Notification(R.drawable.ic_launcher,msg, System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, "ToDoApp", msg, pendingNotificationIntent);
        mManager.notify(0, notification);
    }
}
