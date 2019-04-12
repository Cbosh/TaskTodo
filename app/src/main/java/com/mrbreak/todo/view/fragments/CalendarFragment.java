package com.mrbreak.todo.view.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.mrbreak.todo.R;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.DateUtil;
import com.mrbreak.todo.viewmodel.CalendarListViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {
    private WeekView weekView;
    private CalendarListViewModel calendarListViewModel;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ImageView closeImageView = view.findViewById(R.id.closeImageView);
            closeImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));

            closeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            weekView = view.findViewById(R.id.weekView);
            weekView.setWeekViewLoader(new MonthLoader(new MonthLoader.MonthChangeListener() {
                @Override
                public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                    return null;
                }
            }));

            calendarListViewModel = ViewModelProviders.of(this).get(CalendarListViewModel.class);

            calendarListViewModel.getToDoList().observe(this, new Observer<List<ToDoModel>>() {
                @Override
                public void onChanged(@Nullable List<ToDoModel> toDos) {
                    displayEvents(toDos);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayEvents(final List<ToDoModel> toDos) {
        weekView.setWeekViewLoader(new MonthLoader(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> events = new ArrayList<>();
                if (toDos != null && toDos.size() > 0) {
                    for (ToDoModel todo : toDos) {
                        Calendar calendar = Calendar.getInstance();
                        Date date = DateUtil.convertStringToDate(todo.getDueDate());
                        calendar.setTime(date);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        int year = calendar.get(Calendar.YEAR);

                        int month = Integer.valueOf((String) DateFormat.format(Constants.MM,
                                DateUtil.convertStringToDate(todo.getDueDate())));
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
