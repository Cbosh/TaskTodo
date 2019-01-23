package com.mrbreak.todo.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.birbit.android.jobqueue.JobManager;
import com.mrbreak.todo.R;
import com.mrbreak.todo.activities.DetailActivity;
import com.mrbreak.todo.adapter.ToDoAdapter;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.customspinners.CategoriesAdapter;
import com.mrbreak.todo.customspinners.PriorityAdapter;
import com.mrbreak.todo.enums.CategoryEnum;
import com.mrbreak.todo.events.AddEditToDoFinished;
import com.mrbreak.todo.events.DeleteToDoFinished;
import com.mrbreak.todo.events.DisplayFragmentStarted;
import com.mrbreak.todo.events.FilterTodos;
import com.mrbreak.todo.events.GetToDosFinished;
import com.mrbreak.todo.events.GetToDosStarted;
import com.mrbreak.todo.events.ReLaunchToDoList;
import com.mrbreak.todo.jobmanager.ToDoJobManager;
import com.mrbreak.todo.jobs.GetToDoJob;
import com.mrbreak.todo.model.Category;
import com.mrbreak.todo.model.ToDo;
import com.mrbreak.todo.util.JsonUtil;
import com.mrbreak.todo.util.SharedPrefUtil;
import com.mrbreak.todo.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;


public class ToDoListFragment extends Fragment {

    private List<ToDo> toDoList;
    private List<Category> categories;
    private String[] priorityNames;
    private JobManager jobManager;
    private RecyclerView toDosRecyclerView;
    private FloatingActionButton floatingActionButton;
    private EditText dateFromEditText;
    private EditText dateToEditText;
    private int priority = -1;
    private String category = "";
    private String dateFromString;
    private String dateToString;

    public ToDoListFragment() {
    }

    public static ToDoListFragment newInstance() {
        return new ToDoListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new GetToDosStarted(EventBus.getDefault()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ReLaunchToDoList event) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list, container, false);
        floatingActionButton = view.findViewById(R.id.addToDoFloatingActionButton);
        floatingActionButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                int toDoId = 0;
                bundle.putInt(Constants.ID, toDoId);
                bundle.putBoolean(Constants.ISEDIT, false);
                ToDoDetailFragment toDoDetailFragment = new ToDoDetailFragment();
                toDoDetailFragment.setArguments(bundle);

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(Constants.ISEDIT, bundle);
                startActivity(intent);
            }
        });

        ToDoJobManager toDoJobManager = new ToDoJobManager();
        toDosRecyclerView = view.findViewById(R.id.to_dos_recycler_view);
        toDosRecyclerView.setHasFixedSize(true);

        toDosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        priorityNames = new String[]{Constants.HIGH, Constants.MEDIUM, Constants.LOW};
        categories = new ArrayList<>();
        try {
            categories = JsonUtil.loadCategories(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        toDosRecyclerView.setLayoutManager(linearLayoutManager);
        jobManager = toDoJobManager.getJobManager(getContext());

        return view;
    }

    private void toDoClick() {
        floatingActionButton.show();
        initializeData(Utils.getToDos(false, toDoList));
    }

    private void doneClick() {
        floatingActionButton.show();
        EventBus.getDefault().post(new DisplayFragmentStarted(EventBus.getDefault(),
                new ToDoDoneFragment()));
        List<ToDo> list = Utils.getToDos(true, toDoList);
    }

    private void overDueClick() {
        floatingActionButton.show();
        List<ToDo> list = Utils.getOverDueList(toDoList);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetToDosFinished event) {
        if (event != null && event.getToDoList() != null && event.getToDoList().size() > 0) {
            toDoList = event.getToDoList();
            toDoClick();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FilterTodos event) {
        openFilterDialog();
    }

    @Subscribe
    public void onEvent(GetToDosStarted event) {
        jobManager.addJobInBackground(new GetToDoJob(EventBus.getDefault()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeleteToDoFinished event) {
        EventBus.getDefault().post(new GetToDosStarted(EventBus.getDefault()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AddEditToDoFinished event) {
        EventBus.getDefault().post(new GetToDosStarted(EventBus.getDefault()));
    }

    private void initializeData(final List<ToDo> toDos) {
        ToDoAdapter adapter = new ToDoAdapter(toDos, new ToDoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SharedPrefUtil.saveCurrentFragment(getApplicationContext(),
                        Constants.ZERO_INT);
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.ISEDIT, true);
                bundle.putParcelable(Constants.TODO, toDos.get(position));

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(Constants.ISEDIT, bundle);
                startActivity(intent);
                //displayDetailFragmentListener.displayFragment(bundle);
            }
        }, jobManager, EventBus.getDefault());
        toDosRecyclerView.setAdapter(adapter);
    }

    private void openFilterDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.to_do_filter);

        dateFromEditText = dialog.findViewById(R.id.dateFrom);
        dateFromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePickerDialog(dateFromEditText);
            }
        });

        dateToEditText = dialog.findViewById(R.id.dateTo);
        dateToEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePickerDialog(dateToEditText);
            }
        });


        TextView applyTextView = dialog.findViewById(R.id.apply);
        TextView cancelTextView = dialog.findViewById(R.id.cancel);

        Spinner prioritySpinner = dialog.findViewById(R.id.prioritySpinner);
        final PriorityAdapter priorityAdapter = new PriorityAdapter(getApplicationContext(), priorityNames);
        prioritySpinner.setAdapter(priorityAdapter);

        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (priorityNames[position].equalsIgnoreCase(Constants.HIGH)) {
                    priority = 0;
                } else if (priorityNames[position].equalsIgnoreCase(Constants.MEDIUM)) {
                    priority = 1;
                } else if (priorityNames[position].equalsIgnoreCase(Constants.LOW)) {
                    priority = 2;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner categorySpinner;
        categorySpinner = dialog.findViewById(R.id.categorySpinner);
        final CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getApplicationContext(), categories);
        categorySpinner.setAdapter(categoriesAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categories.get(position).getCategoryName();
                if (categories.get(position).getCategoryName().equalsIgnoreCase(CategoryEnum.WORK.toString())) {
                    category = CategoryEnum.WORK.toString();
                } else if (categories.get(position).getCategoryName().equalsIgnoreCase(CategoryEnum.STUDIES.toString())) {
                    category = CategoryEnum.STUDIES.toString();
                } else if (categories.get(position).getCategoryName().equalsIgnoreCase(CategoryEnum.PERSONAL.toString())) {
                    category = CategoryEnum.PERSONAL.toString();
                } else if (categories.get(position).getCategoryName().equalsIgnoreCase(CategoryEnum.BUSINESS.toString())) {
                    category = CategoryEnum.BUSINESS.toString();
                } else if (categories.get(position).getCategoryName().equalsIgnoreCase(CategoryEnum.GENERAL.toString())) {
                    category = CategoryEnum.GENERAL.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
                initializeData(Utils.filterToDoList(toDoList, priority, category,
                        dateFromString, dateToString));
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = 900;
        layoutParams.height = 1200;
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void openDateTimePickerDialog(final EditText editText) {
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
                String selectedDate = year + Constants.SPACE +
                        Constants.ZERO_STRING + monthOfYear + Constants.SPACE + dayOfMonth;

                if (editText.getId() == dateFromEditText.getId()) {
                    dateFromString = dateFromEditText.getText().toString();
                    dateFromEditText.setText(Utils.getOutputDateFormt(selectedDate));
                } else if (editText.getId() == dateToEditText.getId()) {
                    dateToString = dateToEditText.getText().toString();
                    dateToEditText.setText(Utils.getOutputDateFormt(selectedDate));
                }

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
}
