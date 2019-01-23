package com.mrbreak.todo.fragments;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrbreak.todo.R;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.events.GetToDosFinished;
import com.mrbreak.todo.events.GetToDosStarted;
import com.mrbreak.todo.model.ToDo;
import com.mrbreak.todo.util.SharedPrefUtil;
import com.mrbreak.todo.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {
    private WeekView weekView;
    private List<ToDo> toDoList;
   // private JobManager jobManager;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String stringList = getArguments().getString(Constants.LIST);
            toDoList = new Gson().fromJson(stringList, new TypeToken<ArrayList<ToDo>>() {
            }.getType());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        ImageView closeImageView = view.findViewById(R.id.closeImageView);
        closeImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefUtil.saveCurrentFragment(getContext(),
                        Constants.ONE_INT);
                Utils.dismissKeyBoard(getContext());
                getActivity().finish();
            }
        });

        weekView = view.findViewById(R.id.weekView);
        displayEvents(toDoList);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //EventBus.getDefault().post(new GetToDosStarted(EventBus.getDefault()));
    }

    @Subscribe
    public void onEvent(GetToDosStarted event) {
       // jobManager.addJobInBackground(new GetToDoJob(EventBus.getDefault()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetToDosFinished event) {
        if (event != null && event.getToDoList() != null && event.getToDoList().size() > 0) {
            toDoList = event.getToDoList();
            displayEvents(toDoList);
        }
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

    private void displayEvents(final List<ToDo> toDos) {
        weekView.setWeekViewLoader(new MonthLoader(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> events = new ArrayList<>();
                if (toDos != null && toDos.size() > 0) {
                    for (ToDo todo : toDos) {
                        Calendar calendar = Calendar.getInstance();
                        Date date = Utils.convertStringToDate(todo.getDueDate());
                        calendar.setTime(date);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        int year = calendar.get(Calendar.YEAR);

                        int month = Integer.valueOf((String) DateFormat.format(Constants.MM,
                                Utils.convertStringToDate(todo.getDueDate())));
                        if (newMonth == month) {
                            Calendar startTime = Calendar.getInstance();

                            startTime.set(Calendar.HOUR_OF_DAY, getHour(todo.getStartTime()));
                            startTime.set(Calendar.MINUTE, getMinutes(todo.getStartTime()));
                            startTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            startTime.set(Calendar.MONTH, month - Constants.ONE_INT);
                            startTime.set(Calendar.YEAR, year);

                            Calendar endTime = (Calendar) startTime.clone();

                            endTime.set(Calendar.HOUR_OF_DAY, getHour(todo.getEndTime()));
                            endTime.set(Calendar.MINUTE, getMinutes(todo.getEndTime()));
                            endTime.set(Calendar.MONTH, month - Constants.ONE_INT);

                            WeekViewEvent event = new WeekViewEvent(todo.getId(), todo.getCategory(), startTime, endTime);
                            if (todo.isDone()) {
                                event.setColor(getResources().getColor(R.color.green));
                            } else {
                                event.setColor(getResources().getColor(R.color.blue));
                            }
                            events.add(event);
                        }
                    }
                }
                return events;
            }
        }));

        // Set an action when any event is clicked.
        weekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {

            }
        });

        // Set long press listener for events.
        weekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

            }
        });
    }

    private int getHour(String time) {
        int returnTime = 0;
        if (!TextUtils.isEmpty(time)) {
            if (time.contains(Constants.PM)) {
                time = time.substring(Constants.ZERO_INT, time.indexOf(Constants.COLON));
                returnTime = Integer.valueOf(time);
                switch (returnTime) {
                    case 1:
                        return 13;
                    case 2:
                        return 14;
                    case 3:
                        return 15;
                    case 4:
                        return 16;
                    case 5:
                        return 15;
                    case 6:
                        return 18;
                    case 7:
                        return 19;
                    case 8:
                        return 20;
                    case 9:
                        return 21;
                    case 10:
                        return 22;
                    case 11:
                        return 23;
                    case 12:
                        return 24;
                    default:
                }
            } else {
                time = time.substring(0, time.indexOf(Constants.COLON));
                returnTime = Integer.valueOf(time);
            }
        }

        return returnTime;
    }

    private int getMinutes(String time) {
        int returnTime = 0;
        if (!TextUtils.isEmpty(time)) {
            time = time.substring(time.indexOf(Constants.COLON) + Constants.ONE_INT, time.indexOf(Constants.SPACE));
            returnTime = Integer.valueOf(time);
        }
        return returnTime;
    }
}
