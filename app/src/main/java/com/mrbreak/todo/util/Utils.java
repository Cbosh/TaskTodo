package com.mrbreak.todo.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.repository.model.ToDoModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat(Constants.DAY_MONTH_DATE_FORMAT);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String getDisplayDueDate(String dueDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date date = dateFormat.parse(dueDate);
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.DAY_MONTH_DATE_FORMAT);

            return formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static Date getDueDate(String strDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        try {
            return dateFormat.parse(strDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getTime(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        Date date = null;
        try {
            date = dateFormat.parse(dateTime);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int hours = cal.get(Calendar.HOUR_OF_DAY);
            int minutes = cal.get(Calendar.MINUTE);

            String strHours = String.valueOf(hours);
            String strMinutes = String.valueOf(minutes);
            if (strHours.length() == 1) {
                strHours = "0" + strHours;
            }

            if (strMinutes.length() == 1) {
                strMinutes = "0" + strMinutes;
            }

            return strHours + ":" + strMinutes;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getCompletedDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        return dateFormat.format(c.getTime());
    }

    public static String getCompletedDateTimeDisplay(String completedDate) {
        if (TextUtils.isEmpty(completedDate)) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        Date date = null;
        try {
            date = formatter.parse(completedDate);
            formatter = new SimpleDateFormat(Constants.DAY_MONTH_DATE_FORMAT);

            return formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int getHour(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return 0;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        Date date = null;
        try {
            date = dateFormat.parse(dateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.HOUR_OF_DAY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getMinutes(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return 0;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        Date date = null;
        try {
            date = dateFormat.parse(dateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MINUTE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getFormattedTime(String dueDate, String time) {
        try {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("d MMM yyyy HH:mm a");

            String dateTime = dueDate + " " + time;
            Date date = timeStampFormat.parse(dateTime);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MMMM dd HH:mm:ss zzzz");
            Date startDate = simpleDateFormat.parse(simpleDateFormat.format(date));

            return startDate.toString();

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public static String convertDateToString(Date date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YEAR_MONTH_DATE_FORMAT);
            String dateTime = dateFormat.format(date);
            return dateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertDateToStringDateFirst(Date date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DAY_MONTH_DATE_FORMAT);
            String dateTime = dateFormat.format(date);
            return dateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
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
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int getNumberOfItemsByPriority(int priority, List<ToDoModel> list, String dateFromStr, String dateToStr) {
        int num = 0;
        for (ToDoModel toDo : list) {
            if (toDo.getPriority() == priority) {
                if (!TextUtils.isEmpty(dateFromStr) && !TextUtils.isEmpty(dateToStr)) {
                    Date dateFrom = Utils.convertStringToDate(dateFromStr);
                    Date dateTo = Utils.convertStringToDate(dateToStr);
                    Date toDoDate = Utils.convertStringToDate(toDo.getDueDate());

                    if (dateFrom != null && dateTo != null) {
                        if ((toDoDate.equals(dateFrom) || toDoDate.after(dateFrom))
                                && (toDoDate.equals(dateTo) || toDoDate.before(dateTo))) {
                            num++;
                        }
                    }

                } else {
                    num++;
                }
            }
        }

        return num;
    }

    public static int getNumberOfItemsByCategory(String category, List<ToDoModel>
            list, String dateFromStr, String dateToStr) {

        int num = 0;
        for (ToDoModel toDo : list) {
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
    public static List<ToDoModel> getToDos(boolean done, List<ToDoModel> list) {
        List<ToDoModel> doneList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ToDoModel toDo : list) {
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

    public static List<ToDoModel> filterToDoList(List<ToDoModel> list, int priority, String category,
                                                 String dateFromStr, String dateToStr) {

        List<ToDoModel> toDoList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ToDoModel toDo : list) {
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
    public static List<ToDoModel> getOverDueList(List<ToDoModel> list) {
        List<ToDoModel> overDue = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (ToDoModel toDo : list) {
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

    public static boolean checkTimings(String startTime, String endTime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(startTime);
            Date date2 = sdf.parse(endTime);

            if (date1.before(date2)) {
                return true;
            } else {
                return endTime.contains(Constants.PM);
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
