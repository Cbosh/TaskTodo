package com.mrbreak.todo.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.mrbreak.todo.activities.MainActivity;
import com.mrbreak.todo.R;
import com.mrbreak.todo.alarmreciever.NotificationPublisher;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.customspinners.CategoriesAdapter;
import com.mrbreak.todo.customspinners.PriorityAdapter;
import com.mrbreak.todo.events.AddEditToDoStarted;
import com.mrbreak.todo.events.BackPressedFinished;
import com.mrbreak.todo.events.BackPressedStarted;
import com.mrbreak.todo.jobmanager.ToDoJobManager;
import com.mrbreak.todo.jobs.AddEditToDoJob;
import com.mrbreak.todo.jobs.DeleteToDoJob;
import com.mrbreak.todo.model.Category;
import com.mrbreak.todo.model.ToDo;
import com.mrbreak.todo.util.JsonUtil;
import com.mrbreak.todo.util.SharedPrefUtil;
import com.mrbreak.todo.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class ToDoDetailFragment extends Fragment {
    private JobManager jobManager;
    private ToDo toDo;
    private int priority;
    private String category;
    private boolean isDone;
    private String returnTime;
    private List<Category> categories;
    private EditText contentEditText;
    private EditText dueDateEditText;
    private EditText startTimeEditText;
    private EditText endTimeEditText;
    private TextView maxLimit;

    private ImageView doneImageView;
    private ImageView editImageView;
    private ImageView deleteImageView;

    private ImageView shareImageView;
    private ImageView copyImageView;
    private ImageView lockImageView;
    private ImageView remindMeImageView;
    private ImageView notificationImageView;

    private ImageView addCategoryImageView;
    private ImageView moreImageView;
    private LinearLayout moreLinearLayout;
    private LinearLayout deleteLinearLayout;
    private LinearLayout editLinearLayout;
    private Spinner prioritySpinner;
    private Spinner categorySpinner;
    private Switch doneToggleButton;
    private boolean firstLoad = true;
    private String TAG = ToDoDetailFragment.class.getName();
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private RelativeLayout reminderLayout;
    private int remindMeBeforeTime;
    private TextView remindMeTimeTextView;
    private ToDo createToDo;

    public ToDoDetailFragment() {
        // Required empty public constructor
    }

    public static ToDoDetailFragment newInstance(Bundle bundle) {
        ToDoDetailFragment fragment = new ToDoDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(Constants.TODO)) {
                toDo = getArguments().getParcelable(Constants.TODO);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        final ToDoJobManager toDoJobManager = new ToDoJobManager();

        String[] priorityNames = {Constants.HIGH, Constants.MEDIUM, Constants.LOW};
        categories = new ArrayList<>();
        try {
            categories = JsonUtil.loadCategories(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        remindMeTimeTextView = view.findViewById(R.id.alarmSelected);
        reminderLayout = view.findViewById(R.id.reminder);
        reminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReminderDialog();
            }
        });

        contentEditText = view.findViewById(R.id.content);
        dueDateEditText = view.findViewById(R.id.dueDate);
        maxLimit = view.findViewById(R.id.maxLimit);
        startTimeEditText = view.findViewById(R.id.startTime);
        endTimeEditText = view.findViewById(R.id.endTime);

        editLinearLayout = view.findViewById(R.id.editLinearLayout);
        deleteLinearLayout = view.findViewById(R.id.deleteLinearLayout);
        moreLinearLayout = view.findViewById(R.id.moreLinearLayout);

        doneToggleButton = view.findViewById(R.id.doneToggleButton);
        contentEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        maxLimit.setVisibility(View.GONE);
        dueDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePickerDialog();
            }
        });

        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(Utils.getCurrentHour(), Utils.getCurrentMinutes(), true);
            }
        });

        endTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(Utils.getCurrentHour(), Utils.getCurrentMinutes(), false);
            }
        });

        remindMeImageView = view.findViewById(R.id.remindMeTime);
        notificationImageView = view.findViewById(R.id.alarmIcon);

        contentEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDescriptionDialog();
            }
        });

        dueDateEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                showDoneImage();
            }
        });

        ImageView closeImageView = view.findViewById(R.id.closeImageView);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.dismissKeyBoard(getContext());
                EventBus.getDefault().post(new BackPressedFinished());
            }
        });

        deleteImageView = view.findViewById(R.id.deleteImageView);
        deleteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDo == null) {
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.delete_massage);
                builder.setCancelable(true);

                builder.setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                jobManager.addJobInBackground(new DeleteToDoJob(EventBus.getDefault(),
                                        toDo));
                                EventBus.getDefault().post(new BackPressedFinished());
                            }
                        });

                builder.setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder.create();
                alert11.show();
            }
        });

        moreImageView = view.findViewById(R.id.moreImageView);
        moreLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        addCategoryImageView = view.findViewById(R.id.addCategory);
        addCategoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        editImageView = view.findViewById(R.id.editImageView);
        editLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDo != null && toDo.isDone()) {
                    return;
                }
                Utils.dismissKeyBoard(getContext());
                enableEditText(true);
            }
        });

        doneImageView = view.findViewById(R.id.doneImageView);
        doneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDo != null && toDo.isDone()) {
                    return;
                }
                Utils.dismissKeyBoard(getContext());
                getToDoDetails();

                //check for overdue
                if (toDo == null) {
                    SharedPrefUtil.saveCurrentFragment(getApplicationContext(),
                            Constants.DONE_LIST);
                    return;
                }
                setReminder();
            }
        });

        categorySpinner = view.findViewById(R.id.categorySpinner);
        final CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getApplicationContext(), categories);
        categorySpinner.setAdapter(categoriesAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories.get(position).getCategoryName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        prioritySpinner = view.findViewById(R.id.prioritySpinner);
        final PriorityAdapter priorityAdapter = new PriorityAdapter(getApplicationContext(), priorityNames);
        prioritySpinner.setAdapter(priorityAdapter);

        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO: priority object with the name, id
                priority = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        doneToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDone = isChecked;
                if (!firstLoad && isChecked) {
                    Utils.displaySnackBar("Well done for completing this task",
                            doneToggleButton).show();
                    toDo.setCompletedDate(Utils.convertDateToString(new Date()));
                } else {
                    // toDo.setCompletedDate(Utils.convertDateToString(new Date()));
                    firstLoad = false;
                }
            }
        });

        closeImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
        setImageColor();
        jobManager = toDoJobManager.getJobManager(getContext());

        return view;
    }

    private void setReminder() {
        SharedPrefUtil.saveCurrentFragment(getApplicationContext(),
                Constants.TODO_LIST);
        Class<?> cls = MainActivity.class;
        Intent intent = new Intent(getContext(), cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (createToDo != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                scheduleNotification(showNotification(getContext(), createToDo.getCategory(),
                        createToDo.getContent(), intent),
                        remindMeBeforeTime);
            } else {
                scheduleNotification(getNotification(createToDo.getContent()), remindMeBeforeTime);
            }
        }
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet, null);

        shareImageView = view.findViewById(R.id.share);
        copyImageView = view.findViewById(R.id.copy);
        lockImageView = view.findViewById(R.id.lock);

        LinearLayout shareLinearLayout = view.findViewById(R.id.shareLinearLayout);
        LinearLayout copyLinearLayout = view.findViewById(R.id.copyLinearLayout);
        LinearLayout lockLinearLayout = view.findViewById(R.id.lockLinearLayout);

        setBottomSheetImageColor();

        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());

        shareLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        toDo.getContent());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Task"));
                dialog.dismiss();
            }
        });

        copyLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard = (ClipboardManager)
                        getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", toDo.getContent());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Text Copied",
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        lockLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lock and save
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }


    private void setBottomSheetImageColor() {
        copyImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        lockImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        shareImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
    }

    private void setImageColor() {
        doneImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
        editImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        addCategoryImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        moreImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        deleteImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));

        remindMeImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.androidDefaultColor));
        notificationImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.androidDefaultColor));
    }


    private void showDoneImage() {
        if (isInputValid() || toDo == null) {
            return;
        }

        if (!TextUtils.isEmpty(toDo.getContent()) && !TextUtils.isEmpty(contentEditText.getText())) {
            if (!toDo.getContent().equalsIgnoreCase(contentEditText.getText().toString().trim())) {
                doneImageView.setEnabled(true);
                return;
            }
        }

        if (!TextUtils.isEmpty(toDo.getDueDate()) && !TextUtils.isEmpty(dueDateEditText.getText())) {
            if (!toDo.getDueDate().equalsIgnoreCase(dueDateEditText.getText().toString().trim())) {
                doneImageView.setEnabled(true);
            }
        }
    }

    private int getHours(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hours = cal.get(Calendar.HOUR_OF_DAY);

        return hours;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        Utils.dismissKeyBoard(getContext());

        if (getArguments() != null && getArguments().containsKey(Constants.ISEDIT) &&
                getArguments().getBoolean(Constants.ISEDIT)) {
            doneImageView.setEnabled(true);
            doneToggleButton.setEnabled(true);
            deleteImageView.setEnabled(true);
            editImageView.setEnabled(true);
            enableEditText(false);
        } else {
            enableEditText(true);
            deleteImageView.setEnabled(false);
            doneToggleButton.setEnabled(false);
            editImageView.setEnabled(false);
            maxLimit.setVisibility(View.GONE);
            editImageView.setVisibility(View.VISIBLE);
            deleteImageView.setVisibility(View.VISIBLE);
        }

        boolean enabled = false;
        if (toDo != null) {
            displayDetails(toDo);
//            if (Utils.getDaysDifference(new Date(), Utils.convertStringToDate(toDo.getDueDate())) == 0) {
//                enabled = true;
//            }
        }
    }

    private void enableEditText(boolean enabled) {
        categorySpinner.setEnabled(enabled);
        prioritySpinner.setEnabled(enabled);
        dueDateEditText.setEnabled(enabled);
        contentEditText.setEnabled(enabled);
        startTimeEditText.setEnabled(enabled);
        endTimeEditText.setEnabled(enabled);
        reminderLayout.setEnabled(enabled);
        if (getArguments() != null && getArguments().containsKey(Constants.ISEDIT)) {
            doneToggleButton.setEnabled(enabled);
        }
    }

    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(getContext(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, createToDo.getId());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }


    private Notification getNotification(String content) {
        Class<?> cls = MainActivity.class;

        Intent intent = new Intent(getContext(), cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(intent);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int DAILY_REMINDER_REQUEST_CODE = 1;
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                DAILY_REMINDER_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(getContext());
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSound(alarmSound).setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }


    public Notification showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Todo";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .addAction(R.mipmap.ic_launcher, "Done", resultPendingIntent)  // #1
                .addAction(R.mipmap.ic_launcher, "Edit", resultPendingIntent)  // #2
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body));
        mBuilder.setContentIntent(resultPendingIntent);

        return mBuilder.build();
    }

    private void showTimePickerDialog(int h, int m, final boolean isStartTime) {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.timepicker_header, null);

        TimePickerDialog builder = new TimePickerDialog(getContext(), R.style.NewDialog,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {

//                        if (!Utils.isHourValid(hour)) {
//                            Utils.displaySnackBar("Please select valid time", timePicker);
//                        } else {
//                            if (!Utils.isMinutesValid(min)) {
//                                Utils.displaySnackBar("Please select valid time", timePicker);
//                                return;
//                            }
//                        }

                        String hours = String.valueOf(hour);
                        if (hours.length() == 1) {
                            hours = Constants.ZERO_STRING + hours;
                        }

                        String minutes = String.valueOf(min);
                        if (minutes.length() == 1) {
                            minutes = Constants.ZERO_STRING + minutes;
                        }

                        String time = hours + Constants.COLON + minutes;
                        String daytime = "";
                        if (hour >= 12) {
                            daytime = Constants.SPACE + Constants.PM;
                        } else {
                            daytime = Constants.SPACE + Constants.AM;
                        }

                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.TIME_HH_MM_PATTERN);
                            Date date = simpleDateFormat.parse(time);
                            returnTime = new SimpleDateFormat(Constants.TIME_KK_MM_PATTERN).format(date) + daytime;
                            if (isStartTime) {
                                startTimeEditText.setText(returnTime);
                            } else {
                                endTimeEditText.setText(returnTime);
                            }
                        } catch (final ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, h, m, true);
        builder.setCustomTitle(view);
        builder.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getToDoDetails() {
        if (isInputValid()) {
            return;
        }
        //  Date currentTime = Calendar.getInstance().getStartTime();
        ToDo newToDo = new ToDo();
        if (toDo != null) {
            newToDo.setId(toDo.getId());
        }

        newToDo.setCategory(category);
        newToDo.setContent(contentEditText.getText().toString().trim());
        newToDo.setDueDate(dueDateEditText.getText().toString().trim());
        newToDo.setStartTime(startTimeEditText.getText().toString().trim());
        newToDo.setEndTime(endTimeEditText.getText().toString().trim());
        newToDo.setPriority(priority);
        newToDo.setDone(isDone);

        if (remindMeBeforeTime == 0) {
            remindMeBeforeTime = 15;
        }

        newToDo.setRemindMeBefore(remindMeBeforeTime);
        if (isDone) {
            newToDo.setCompletedDate(Utils.convertDateToString(new Date()));
        }
        //update this
        newToDo.setCreatedDate(Utils.convertDateToString(new Date()));

        int days = Utils.getDaysDifference(new Date(), Utils.convertStringToDate(newToDo.getDueDate()));
        //  scheduleNotification(getNotification( android:layout_marginLeft="20dp");

        createToDo = newToDo;

        EventBus.getDefault().post(new AddEditToDoStarted(
                EventBus.getDefault(), newToDo));
    }

    private boolean isInputValid() {
        return TextUtils.isEmpty(contentEditText.getText()) ||
                TextUtils.isEmpty(dueDateEditText.getText());
    }

    private void displayDetails(ToDo toDo) {
        contentEditText.setText(toDo.getContent());
        dueDateEditText.setText(toDo.getDueDate());
        startTimeEditText.setText(toDo.getStartTime());
        endTimeEditText.setText(toDo.getEndTime());
        prioritySpinner.setSelection(toDo.getPriority());
        doneToggleButton.setChecked(toDo.isDone());
        String remindMe = toDo.getRemindMeBefore() + " minutes before";
        remindMeTimeTextView.setText(remindMe);
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCategoryName().contains(toDo.getCategory())) {
                categorySpinner.setSelection(i);
            }
        }

        if (toDo.isDone()) {
            enableEditText(false);
        }
    }


    @Subscribe
    public void onEvent(AddEditToDoStarted event) {
        jobManager.addJobInBackground(new AddEditToDoJob(EventBus.getDefault(), event.getToDo()));
        getActivity().finish();
    }

    @Subscribe
    public void onEvent(BackPressedStarted e) {
        getActivity().finish();
    }

    private void openDateTimePickerDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.date_time_dialog);

        final DatePicker myDatePicker = (DatePicker) dialog.findViewById(R.id.datePicker);

        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        TextView okButton = dialog.findViewById(R.id.okButton);

        // Set Dialog date and time
        myDatePicker.updateDate(myDatePicker.getYear(), myDatePicker.getMonth(), myDatePicker.getDayOfMonth());
        myDatePicker.setMinDate(System.currentTimeMillis() - 1000);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clearAllFocus();
                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int year = myDatePicker.getYear();
                String monthOfYear = String.valueOf(myDatePicker.getMonth());
                if (String.valueOf(monthOfYear).length() == 1) {
                    int month = 1 + myDatePicker.getMonth();
                    monthOfYear = Constants.EMPTY + month;
                }
                int dayOfMonth = myDatePicker.getDayOfMonth();
                String selectedDate = year + Constants.SPACE + Constants.ZERO_STRING + monthOfYear + Constants.SPACE + dayOfMonth;
                dueDateEditText.setText(Utils.getOutputDateFormt(selectedDate));
                //  onDateSet(year, monthOfYear, dayOfMonth);
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = 1000;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void openDescriptionDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.description_dialog);

        final EditText description = dialog.findViewById(R.id.description);
        Button saveButton = dialog.findViewById(R.id.saveButton);
        ImageView closeImageView = dialog.findViewById(R.id.close);
        description.setText(contentEditText.getText().toString());

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(description.getText().toString())) {
                    contentEditText.setText(description.getText().toString().trim());
                }

                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void setReminderDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reminder_dialog);

        TextView cancelTextView = dialog.findViewById(R.id.cancelTextView);
        TextView saveTextView = dialog.findViewById(R.id.saveTextView);

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remindMeSelectedTime = remindMeBeforeTime + " minutes before";
                remindMeTimeTextView.setText(remindMeSelectedTime);
                dialog.dismiss();
            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindMeBeforeTime = 15;
                dialog.dismiss();
            }
        });

        RadioButton fiveMinutesRadioButton = dialog.findViewById(R.id.fiveMinutesRadioButton);
        RadioButton tenMinutesRadioButton = dialog.findViewById(R.id.tenMinutesRadioButton);
        RadioButton fifteenMinutesRadioButton = dialog.findViewById(R.id.fifteenMinutesRadioButton);
        RadioButton thirtyMinutesRadioButton = dialog.findViewById(R.id.thirtyMinutesRadioButton);

        fiveMinutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindMeBeforeTime = 5;
            }
        });

        tenMinutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindMeBeforeTime = 10;
            }
        });

        fifteenMinutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindMeBeforeTime = 15;
            }
        });

        thirtyMinutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindMeBeforeTime = 30;
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = 1100;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    public void onDateSet(int year, int month, int day) {
        Calendar userAge = new GregorianCalendar(year, month, day);
        Calendar minAdultAge = new GregorianCalendar();
        minAdultAge.add(Calendar.YEAR, -18);

        if (minAdultAge.before(userAge)) {
        }
    }
}
