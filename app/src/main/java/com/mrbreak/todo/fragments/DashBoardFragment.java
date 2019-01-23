package com.mrbreak.todo.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.birbit.android.jobqueue.JobManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrbreak.todo.R;
import com.mrbreak.todo.adapter.DashBoardAdapter;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.enums.CategoryEnum;
import com.mrbreak.todo.enums.FilterEnum;
import com.mrbreak.todo.enums.PriorityEnum;
import com.mrbreak.todo.events.GetToDosFinished;
import com.mrbreak.todo.events.GetToDosStarted;
import com.mrbreak.todo.jobmanager.ToDoJobManager;
import com.mrbreak.todo.jobs.GetToDoJob;
import com.mrbreak.todo.model.Legend;
import com.mrbreak.todo.model.ToDo;
import com.mrbreak.todo.util.SharedPrefUtil;
import com.mrbreak.todo.util.Utils;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class DashBoardFragment extends Fragment {
    private List<ToDo> toDoList;
    private TextView emptyText;
    private RecyclerView legendRecyclerView;
    private JobManager jobManager;
    private EditText dateFromEditText;
    private EditText dateToEditText;

    private String dateFromString;
    private String dateToString;

    public DashBoardFragment() {
    }

    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String stringList = getArguments().getString("List");
            toDoList = new Gson().fromJson(stringList, new TypeToken<ArrayList<ToDo>>() {
            }.getType());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dash_board, container, false);
        emptyText = view.findViewById(R.id.emptyText);

        legendRecyclerView = view.findViewById(R.id.legendListView);
        legendRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        legendRecyclerView.setLayoutManager(linearLayoutManager);
        TextView filterTextView = view.findViewById(R.id.filter);

        ImageView closeImageView = view.findViewById(R.id.closeImageView);
        closeImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.dismissKeyBoard(getContext());
                SharedPrefUtil.saveCurrentFragment(getApplicationContext(),
                        Constants.ONE_INT);
                getActivity().finish();
            }
        });

        filterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog(view);
            }
        });

        ToDoJobManager toDoJobManager = new ToDoJobManager();
        jobManager = toDoJobManager.getJobManager(getContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new GetToDosStarted(EventBus.getDefault()));
    }

    @Subscribe
    public void onEvent(GetToDosStarted event) {
        jobManager.addJobInBackground(new GetToDoJob(EventBus.getDefault()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetToDosFinished event) {
        if (event != null && event.getToDoList() != null && event.getToDoList().size() > 0) {
            toDoList = event.getToDoList();
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

    private void displayDashBoard(int filterBy, View view) {
        PieChart mPieChart = view.findViewById(R.id.piechart);
        mPieChart.clearChart();

        if (FilterEnum.PRIORITY.getIntValue() == filterBy) {
            int high = Math.round(getPercentageByPriority(PriorityEnum.HIGH.getIntValue(), dateFromString, dateToString));
            int medium = Math.round(getPercentageByPriority(PriorityEnum.MEDIUM.getIntValue(), dateFromString, dateToString));
            int low = Math.round(getPercentageByPriority(PriorityEnum.LOW.getIntValue(), dateFromString, dateToString));

            List<Legend> legends = new ArrayList<>();
            String stringBuilder;

            if (high > 0) {
                mPieChart.addPieSlice(new PieModel(PriorityEnum.HIGH.toString(), high,
                        Color.parseColor("#FF0000")));

                stringBuilder = Constants.SPACE +
                        Constants.HIGH + Constants.PRIORITY;
                Legend legend = new Legend(PriorityEnum.HIGH.getIntValue(),
                        high + Constants.PERCENTAGE + stringBuilder);
                legends.add(legend);
            }

            if (medium > 0) {
                mPieChart.addPieSlice(new PieModel(PriorityEnum.MEDIUM.toString(), medium,
                        Color.parseColor("#FFA500")));

                stringBuilder = Constants.SPACE +
                        Constants.MEDIUM + Constants.PRIORITY;
                Legend legend = new Legend(PriorityEnum.MEDIUM.getIntValue(),
                        medium + Constants.PERCENTAGE + stringBuilder);
                legends.add(legend);
            }

            if (low > 0) {
                mPieChart.addPieSlice(new PieModel(PriorityEnum.LOW.toString(), low,
                        Color.parseColor("#FFDF00")));


                stringBuilder = Constants.SPACE +
                        Constants.LOW + Constants.PRIORITY;
                Legend legend = new Legend(PriorityEnum.LOW.getIntValue(),
                        low + Constants.PERCENTAGE + stringBuilder);
                legends.add(legend);
            }

            displayLegend(legends);

        } else if (FilterEnum.CATEGORY.getIntValue() == filterBy) {
            String stringBuilder;
            List<Legend> legends = new ArrayList<>();

            int work = Math.round(getPercentageByCategory(CategoryEnum.WORK.toString(), dateFromString, dateToString));
            if (work > 0) {
                mPieChart.addPieSlice(new PieModel(CategoryEnum.WORK.toString(),
                        getPercentageByCategory(CategoryEnum.WORK.toString(), dateFromString, dateToString),
                        Color.parseColor("#ffbf00")));

                stringBuilder = CategoryEnum.WORK.toString() +
                        Constants.SPACE + work + Constants.PERCENTAGE;
                Legend legend = new Legend(CategoryEnum.WORK.getIntValue(), stringBuilder);
                legends.add(legend);
            }

            int studies = Math.round(getPercentageByCategory(CategoryEnum.STUDIES.toString(),dateFromString, dateToString));
            if (studies > 0) {
                mPieChart.addPieSlice(new PieModel(CategoryEnum.STUDIES.toString(),
                        getPercentageByCategory(CategoryEnum.STUDIES.toString(),dateFromString, dateToString),
                        Color.parseColor("#3dcab9")));

                stringBuilder = CategoryEnum.STUDIES.toString() +
                        Constants.SPACE + studies + Constants.PERCENTAGE;
                Legend legend = new Legend(CategoryEnum.STUDIES.getIntValue(), stringBuilder);
                legends.add(legend);
            }

            int personal = Math.round(getPercentageByCategory(CategoryEnum.PERSONAL.toString(),dateFromString, dateToString));
            if (personal > 0) {
                mPieChart.addPieSlice(new PieModel(CategoryEnum.PERSONAL.toString(),
                        getPercentageByCategory(CategoryEnum.PERSONAL.toString(), dateFromString, dateToString),
                        Color.parseColor("#4a6850")));

                stringBuilder = CategoryEnum.PERSONAL.toString() +
                        Constants.SPACE + personal + Constants.PERCENTAGE;
                Legend legend = new Legend(CategoryEnum.PERSONAL.getIntValue(), stringBuilder);
                legends.add(legend);
            }

            int general = Math.round(getPercentageByCategory(CategoryEnum.GENERAL.toString(),dateFromString, dateToString));
            if (general > 0) {
                mPieChart.addPieSlice(new PieModel(CategoryEnum.GENERAL.toString(),
                        getPercentageByCategory(CategoryEnum.GENERAL.toString(), dateFromString, dateToString),
                        Color.parseColor("#941717")));

                stringBuilder = CategoryEnum.GENERAL.toString() +
                        Constants.SPACE + general + Constants.PERCENTAGE;
                Legend legend = new Legend(CategoryEnum.GENERAL.getIntValue(), stringBuilder);
                legends.add(legend);
            }

            int business = Math.round(getPercentageByCategory(CategoryEnum.BUSINESS.toString(),dateFromString, dateToString));
            if (business > 0) {
                mPieChart.addPieSlice(new PieModel(CategoryEnum.BUSINESS.toString(),
                        getPercentageByCategory(CategoryEnum.BUSINESS.toString(),dateFromString, dateToString),
                        Color.parseColor("#7f8e9e")));

                stringBuilder = CategoryEnum.BUSINESS.toString() +
                        Constants.SPACE + business + Constants.PERCENTAGE;
                Legend legend = new Legend(CategoryEnum.BUSINESS.getIntValue(), stringBuilder);
                legends.add(legend);
            }

            displayLegend(legends);
        }

        //for date select from and to date then show list based on priority and category
        mPieChart.setVisibility(View.VISIBLE);
        mPieChart.startAnimation();
    }

    private float getPercentageByPriority(int priority, String dateFrom, String dateTo) {
        if (toDoList != null && toDoList.size() > 0) {
            float numberOfItems = Utils.getNumberOfItemsByPriority(priority, toDoList, dateFrom, dateTo);
            if (numberOfItems != 0) {
                return (numberOfItems / toDoList.size()) * 100;
            }
        }
        return 0f;
    }

    private float getPercentageByCategory(String category, String dateFrom, String dateTo) {
        if (toDoList != null && toDoList.size() > 0) {
            float numberOfItems = Utils.getNumberOfItemsByCategory(category, toDoList,dateFrom, dateTo);
            if (numberOfItems != 0) {
                return (numberOfItems / toDoList.size()) * 100;
            }
        }
        return 0f;
    }

    private void openFilterDialog(final View view) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dash_board_filter);

        final RadioButton priorityRadioButton = dialog.findViewById(R.id.priorityRadioButton);
        final RadioButton categoryRadioButton = dialog.findViewById(R.id.categoryRadioButton);

        dateFromEditText = dialog.findViewById(R.id.dateFrom);
        dateToEditText = dialog.findViewById(R.id.dateTo);

        dateFromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(dateFromEditText);
            }
        });

        dateToEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(dateToEditText);
            }
        });

        TextView applyTextView = dialog.findViewById(R.id.applyFilter);
        TextView cancelTextView = dialog.findViewById(R.id.cancelFilter);

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        applyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter list
                if (priorityRadioButton.isChecked()) {
                    displayDashBoard(FilterEnum.PRIORITY.getIntValue(), view);
                }

                if (categoryRadioButton.isChecked()) {
                    displayDashBoard(FilterEnum.CATEGORY.getIntValue(), view);
                }
//
//                if (dateSwitch.isChecked()) {
//                    displayDashBoard(FilterEnum.DATE.getIntValue(), view);
//                }

                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = 940;
        layoutParams.height = 1100;
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void openDatePickerDialog(final EditText editText) {
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
                String selectedDate = year + Constants.SPACE + Constants.ZERO_STRING +
                        monthOfYear + Constants.SPACE + dayOfMonth;

                if (editText.getId() == dateFromEditText.getId()) {
                    dateFromString = dateFromEditText.getText().toString();
                } else if (editText.getId() == dateToEditText.getId()) {
                    dateToString = dateToEditText.getText().toString();
                }

                editText.setText(Utils.getOutputDateFormt(selectedDate));
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

    private void displayLegend(final List<Legend> legends) {
        if (legends.size() > 0) {
            emptyText.setVisibility(View.GONE);
        }
        DashBoardAdapter adapter = new DashBoardAdapter(legends,
                new DashBoardAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }
                });
        legendRecyclerView.setAdapter(adapter);
        legendRecyclerView.startAnimation(AnimationUtils.
                loadAnimation(getContext(), R.anim.fade_in));
    }
}
