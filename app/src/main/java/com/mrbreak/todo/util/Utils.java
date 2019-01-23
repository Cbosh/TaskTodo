package com.mrbreak.todo.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mrbreak.todo.R;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.model.ToDo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat(Constants.DAY_MONTH_DATE_FORMAT);
        Date date = null;
        try {
            date = format.parse(dateString);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YEAR_MONTH_DATE_FORMAT);
        String dateTime = dateFormat.format(date);
        return dateTime;
    }

    public static int getDaysDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null) {
            return 0;
        }

        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static int getHours(int days) {
        return days / (60 * 60 * 1000);
    }

    public static void dismissKeyBoard(Context context) {
        try {
            View view = ((Activity) context).getWindow().getCurrentFocus();
            if (view != null && view.getWindowToken() != null) {
                IBinder binder = view.getWindowToken();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(binder, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displyKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static String getOutputDateFormt(String dateStr) {
        if (dateStr.contains(Constants.DASH)) {
            dateStr = dateStr.replace(Constants.DASH, Constants.SPACE);
        }
        DateFormat readFormat = new SimpleDateFormat(Constants.YEAR_MONTH_WITH_SPACE_DATE_FORMAT);
        Date date = null;
        try {
            date = readFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat displayFormat = new SimpleDateFormat(Constants.DAY_MONTH_DATE_FORMAT, Locale.ENGLISH);

        if (date != null) {
            return displayFormat.format(date);
        } else {
            return Constants.EMPTY;
        }
    }

    public static int getNumberOfItemsByPriority(int priority, List<ToDo> list, String dateFromStr, String dateToStr) {
        int num = 0;
        for (ToDo toDo : list) {
            if (toDo.getPriority() == priority) {
                if (!TextUtils.isEmpty(dateFromStr) && !TextUtils.isEmpty(dateToStr)) {
                    Date dateFrom = Utils.convertStringToDate(dateFromStr);
                    Date dateTo = Utils.convertStringToDate(dateToStr);
                    Date toDoDate = Utils.convertStringToDate(toDo.getDueDate());

                    if ((toDoDate.equals(dateFrom) || toDoDate.after(dateFrom))
                            && (toDoDate.equals(dateTo) || toDoDate.before(dateTo))) {
                        num++;
                    }

                } else {
                    num++;
                }
            }
        }

        return num;
    }

    public static int getNumberOfItemsByCategory(String category, List<ToDo>
            list, String dateFromStr, String dateToStr) {

        int num = 0;
        for (ToDo toDo : list) {
            if (toDo.getCategory().equalsIgnoreCase(category)) {
                if (!TextUtils.isEmpty(dateFromStr) && !TextUtils.isEmpty(dateToStr)) {
                    Date dateFrom = Utils.convertStringToDate(dateFromStr);
                    Date dateTo = Utils.convertStringToDate(dateToStr);
                    Date toDoDate = Utils.convertStringToDate(toDo.getDueDate());

                    if ((toDoDate.equals(dateFrom) || toDoDate.after(dateFrom))
                            && (toDoDate.equals(dateTo) || toDoDate.before(dateTo))) {
                        num++;
                    }

                } else {
                    num++;
                }
            }
        }

        return num;
    }

    /*get list of todos that are done or the ones that are not done but not overdue*/
    public static List<ToDo> getToDos(boolean done, List<ToDo> list) {
        List<ToDo> doneList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ToDo toDo : list) {
                Date date = Utils.convertStringToDate(toDo.getDueDate());
                Integer days = Integer.valueOf(String.valueOf(Utils.getDaysDifference(new Date(), date)));

                if (toDo.isDone() && done) {
                    doneList.add(toDo);
                } else if (!done && days >= 0 && !toDo.isDone()) {
                    doneList.add(toDo);
                }
            }
        }

        return doneList;
    }

    /*Get filtered list by date, priority and category
     */

    public static List<ToDo> filterToDoList(List<ToDo> list, int priority, String category,
                                            String dateFromStr, String dateToStr) {

        List<ToDo> toDoList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ToDo toDo : list) {
                if (!TextUtils.isEmpty(dateFromStr) && !TextUtils.isEmpty(dateToStr)) {
                    Date dateFrom = Utils.convertStringToDate(dateFromStr);
                    Date dateTo = Utils.convertStringToDate(dateToStr);
                    Date toDoDate = Utils.convertStringToDate(toDo.getDueDate());

                    if (toDo.getPriority() == priority && toDo.getCategory().
                            equalsIgnoreCase(category)
                            && (toDoDate.equals(dateFrom) || toDoDate.after(dateFrom))
                            && (toDoDate.equals(dateTo) || toDoDate.before(dateTo))) {
                        toDoList.add(toDo);
                    }

                } else if (toDo.getPriority() == priority && toDo.getCategory().equalsIgnoreCase(category)) {
                    toDoList.add(toDo);
                }
            }
        }

        return toDoList;
    }

    /*
    get over due list of todos from the list that comes back form the DB
    * */
    public static List<ToDo> getOverDueList(List<ToDo> list) {
        List<ToDo> overDue = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (ToDo toDo : list) {
                Date date = Utils.convertStringToDate(toDo.getDueDate());
                Integer days = Integer.valueOf(String.valueOf(Utils.getDaysDifference(new Date(), date)));
                if (days < 0 && !toDo.isDone()) {
                    overDue.add(toDo);
                }
            }
        }

        return overDue;
    }

    /*check the validity of the time between current time and end time of the task */

    public static boolean checkTimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isHourValid(int hour) {
        return hour >= getCurrentHour();
    }

    public static boolean isMinutesValid(int minutes) {
        return minutes >= getCurrentMinutes();
    }

    public static Snackbar displaySnackBar(String message, View view) {
        return Snackbar.make(view, message, Snackbar.LENGTH_LONG);
    }

    public static int getCurrentHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentMinutes() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }
}
