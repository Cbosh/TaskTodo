package com.mrbreak.todo.fragments;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrbreak.todo.R;
import com.mrbreak.todo.adapter.DashBoardAdapter;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.databinding.FragmentDashBoardBinding;
import com.mrbreak.todo.enums.CategoryEnum;
import com.mrbreak.todo.enums.FilterEnum;
import com.mrbreak.todo.enums.PriorityEnum;
import com.mrbreak.todo.model.DashBoardFilterModel;
import com.mrbreak.todo.model.Legend;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.Utils;
import com.mrbreak.todo.viewmodel.DashBoardListViewModel;

import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

public class DashBoardFragment extends Fragment {
    private List<ToDoModel> toDoList;
    private EditText dateFromEditText;
    private EditText dateToEditText;

    private String dateFromString;
    private String dateToString;
    private DashBoardListViewModel dashBoardListViewModel;
    private FragmentDashBoardBinding binding;

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
            toDoList = new Gson().fromJson(stringList, new TypeToken<ArrayList<ToDoModel>>() {
            }.getType());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dash_board, container, false);

        binding.priorityLegendListView.setHasFixedSize(true);
        binding.legendListView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.legendListView.setLayoutManager(linearLayoutManager);

        LinearLayoutManager priorityLayoutManager = new LinearLayoutManager(getContext());
        priorityLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.priorityLegendListView.setLayoutManager(priorityLayoutManager);

        binding.closeImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));

        binding.closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toolBar.setVisibility(View.GONE);
                getActivity().onBackPressed();
            }
        });

        binding.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog(v);
            }
        });

        dashBoardListViewModel = ViewModelProviders.of(this).get(DashBoardListViewModel.class);

        displayData();

        binding.emptyText.setTextSize(16);
        binding.header.setTextSize(20);

        return binding.getRoot();
    }

    private void displayData() {
        dashBoardListViewModel.getToDoList().observe(this, new Observer<List<ToDoModel>>() {
            @Override
            public void onChanged(@Nullable List<ToDoModel> toDoModels) {
                toDoList = toDoModels;
            }
        });
    }

    public void displayDashBoard(int filterBy, View view) {
        binding.piechart.clearChart();

        int high = Math.round(getPercentageByPriority(PriorityEnum.HIGH.getIntValue(), dateFromString, dateToString));
        int medium = Math.round(getPercentageByPriority(PriorityEnum.MEDIUM.getIntValue(), dateFromString, dateToString));
        int low = Math.round(getPercentageByPriority(PriorityEnum.LOW.getIntValue(), dateFromString, dateToString));

        List<Legend> legends = new ArrayList<>();
        String stringBuilder;

        if (high > 0) {
            stringBuilder = Constants.SPACE +
                    Constants.HIGH + Constants.PRIORITY;
            Legend legend = new Legend(PriorityEnum.HIGH.getIntValue(),
                    high + Constants.PERCENTAGE + stringBuilder);
            legends.add(legend);
        }

        if (medium > 0) {
            stringBuilder = Constants.SPACE +
                    Constants.MEDIUM + Constants.PRIORITY;
            Legend legend = new Legend(PriorityEnum.MEDIUM.getIntValue(),
                    medium + Constants.PERCENTAGE + stringBuilder);
            legends.add(legend);
        }

        if (low > 0) {
            stringBuilder = Constants.SPACE +
                    Constants.LOW + Constants.PRIORITY;
            Legend legend = new Legend(PriorityEnum.LOW.getIntValue(),
                    low + Constants.PERCENTAGE + stringBuilder);
            legends.add(legend);
        }

        displayPriorityLegend(legends);


        legends = new ArrayList<>();

        int work = Math.round(getPercentageByCategory(CategoryEnum.WORK.toString(), dateFromString, dateToString));
        if (work > 0) {
            binding.piechart.addPieSlice(new PieModel(CategoryEnum.WORK.toString(),
                    getPercentageByCategory(CategoryEnum.WORK.toString(), dateFromString, dateToString),
                    Color.parseColor("#ffbf00")));

            stringBuilder = CategoryEnum.WORK.toString() +
                    Constants.SPACE + work + Constants.PERCENTAGE;
            Legend legend = new Legend(CategoryEnum.WORK.getIntValue(), stringBuilder);
            legends.add(legend);
        }

        int studies = Math.round(getPercentageByCategory(CategoryEnum.STUDIES.toString(), dateFromString, dateToString));
        if (studies > 0) {
            binding.piechart.addPieSlice(new PieModel(CategoryEnum.STUDIES.toString(),
                    getPercentageByCategory(CategoryEnum.STUDIES.toString(), dateFromString, dateToString),
                    Color.parseColor("#3dcab9")));

            stringBuilder = CategoryEnum.STUDIES.toString() +
                    Constants.SPACE + studies + Constants.PERCENTAGE;
            Legend legend = new Legend(CategoryEnum.STUDIES.getIntValue(), stringBuilder);
            legends.add(legend);
        }

        int personal = Math.round(getPercentageByCategory(CategoryEnum.PERSONAL.toString(), dateFromString, dateToString));
        if (personal > 0) {
            binding.piechart.addPieSlice(new PieModel(CategoryEnum.PERSONAL.toString(),
                    getPercentageByCategory(CategoryEnum.PERSONAL.toString(), dateFromString, dateToString),
                    Color.parseColor("#4a6850")));

            stringBuilder = CategoryEnum.PERSONAL.toString() +
                    Constants.SPACE + personal + Constants.PERCENTAGE;
            Legend legend = new Legend(CategoryEnum.PERSONAL.getIntValue(), stringBuilder);
            legends.add(legend);
        }

        int general = Math.round(getPercentageByCategory(CategoryEnum.GENERAL.toString(), dateFromString, dateToString));
        if (general > 0) {
            binding.piechart.addPieSlice(new PieModel(CategoryEnum.GENERAL.toString(),
                    getPercentageByCategory(CategoryEnum.GENERAL.toString(), dateFromString, dateToString),
                    Color.parseColor("#941717")));

            stringBuilder = CategoryEnum.GENERAL.toString() +
                    Constants.SPACE + general + Constants.PERCENTAGE;
            Legend legend = new Legend(CategoryEnum.GENERAL.getIntValue(), stringBuilder);
            legends.add(legend);
        }

        int business = Math.round(getPercentageByCategory(CategoryEnum.BUSINESS.toString(), dateFromString, dateToString));
        if (business > 0) {
            binding.piechart.addPieSlice(new PieModel(CategoryEnum.BUSINESS.toString(),
                    getPercentageByCategory(CategoryEnum.BUSINESS.toString(), dateFromString, dateToString),
                    Color.parseColor("#7f8e9e")));

            stringBuilder = CategoryEnum.BUSINESS.toString() +
                    Constants.SPACE + business + Constants.PERCENTAGE;
            Legend legend = new Legend(CategoryEnum.BUSINESS.getIntValue(), stringBuilder);
            legends.add(legend);
        }

        displayLegend(legends);

        binding.piechart.setVisibility(View.VISIBLE);
        binding.piechart.startAnimation();
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
            float numberOfItems = Utils.getNumberOfItemsByCategory(category, toDoList, dateFrom, dateTo);
            if (numberOfItems != 0) {
                return (numberOfItems / toDoList.size()) * 100;
            }
        }
        return 0f;
    }

    private void openFilterDialog(final View view) {
        final Dialog dialog = new Dialog(getContext(), R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dash_board_filter);

        final RadioButton toDoRadioButton = dialog.findViewById(R.id.toDoRadioButton);
        final RadioButton doneRadioButton = dialog.findViewById(R.id.doneRadioButton);

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

                String startTime = Constants.STRING_00 + Constants.COLON + Constants.STRING_00
                        + Constants.SPACE + Constants.AM;
                String endTime = Constants.STRING_23 + Constants.COLON + Constants.STRING_59
                        + Constants.SPACE + Constants.PM;

                String dateFrom = Utils.getFormattedTime(dateFromEditText.getText().toString(), startTime);
                String dateTo = Utils.getFormattedTime(dateToEditText.getText().toString(), endTime);

                if (toDoRadioButton.isChecked()) {
                    loadData(false, dateFrom, dateTo);
                    displayDashBoard(FilterEnum.CATEGORY.getIntValue(), view);
                    displayDashBoard(FilterEnum.PRIORITY.getIntValue(), view);
                }

                if (doneRadioButton.isChecked()) {
                    loadData(true, dateFrom, dateTo);
                    displayDashBoard(FilterEnum.CATEGORY.getIntValue(), view);
                    displayDashBoard(FilterEnum.PRIORITY.getIntValue(), view);
                }

                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ;
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void loadData(boolean isDone, String startDate, String endDate) {
        DashBoardFilterModel filtering = new DashBoardFilterModel(isDone, startDate, endDate);
        toDoList = dashBoardListViewModel.getLiveDataList(filtering);
        if (toDoList == null || toDoList.size() == 0) {
            binding.emptyText.setVisibility(View.VISIBLE);
            Utils.displaySnackBar(getString(R.string.empty_list_dash_board), binding.emptyText).show();
        } else {
            binding.emptyText.setVisibility(View.GONE);
        }
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();

    }

    private void displayLegend(final List<Legend> legends) {
        if (legends.size() > 0) {
            binding.emptyText.setVisibility(View.GONE);
        }
        DashBoardAdapter adapter = new DashBoardAdapter(legends,
                new DashBoardAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }
                });
        binding.legendListView.setAdapter(adapter);
        binding.legendListView.startAnimation(AnimationUtils.
                loadAnimation(getContext(), R.anim.slide_in));
    }

    private void displayPriorityLegend(final List<Legend> legends) {
        if (legends.size() > 0) {
            binding.emptyText.setVisibility(View.GONE);
        }
        DashBoardAdapter adapter = new DashBoardAdapter(legends,
                new DashBoardAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }
                });
        binding.priorityLegendListView.setAdapter(adapter);
        binding.priorityLegendListView.startAnimation(AnimationUtils.
                loadAnimation(getContext(), R.anim.slide_in));
    }
}
