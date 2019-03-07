package com.mrbreak.todo.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.mrbreak.todo.R;
import com.mrbreak.todo.components.CustomTextView;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.customspinners.CategoriesAdapter;
import com.mrbreak.todo.customspinners.PriorityAdapter;
import com.mrbreak.todo.databinding.FragmentDetailBinding;
import com.mrbreak.todo.model.Category;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.JsonUtil;
import com.mrbreak.todo.util.Utils;
import com.mrbreak.todo.view.ChangeHeaderBar;
import com.mrbreak.todo.viewmodel.ToDoDetailViewModel;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ToDoDetailFragment extends Fragment {
    private ChangeHeaderBar changeHeaderBar;
    private ToDoModel toDo;
    private int priority;
    private String category;
    private boolean isDone;
    private List<Category> categories;

    private ToDoDetailViewModel toDoDetailViewModel;
    private FragmentDetailBinding binding;
    private String startTime;
    private String endTime;
    private String returnTime;
    private String formattedDueDate;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String guid = UUID.randomUUID().toString();
        if (toDo != null) {
            guid = toDo.getToDoGuid();
        }

        ToDoDetailViewModel.Factory factory = new ToDoDetailViewModel.Factory(getActivity().getApplication(), guid);
        toDoDetailViewModel = ViewModelProviders.of(this, factory).get(ToDoDetailViewModel.class);

        String[] priorityNames = {Constants.HIGH, Constants.MEDIUM, Constants.LOW};
        categories = new ArrayList<>();
        try {
            categories = JsonUtil.loadCategories(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.shareLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDo == null) {
                    Utils.displaySnackBar(getString(R.string.cannot_share_empty_task), binding.shareLinearLayout).show();
                    return;
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, binding.content.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Task to do"));
            }
        });

        binding.closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        binding.saveTextView.setTextSize(10);
        binding.deleteTextView.setTextSize(10);
        binding.shareTextView.setTextSize(10);
        binding.dueDate.setTextSize(14);

        binding.dueDate.setTextSize(14);
        binding.startTime.setTextSize(14);
        binding.endTime.setTextSize(14);
        binding.content.setTextSize(16);

        binding.content.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.maxLimit.setVisibility(View.GONE);
        binding.dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePickerDialog();
            }
        });

        binding.startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(Utils.getCurrentHour(), Utils.getCurrentMinutes(), true);
            }
        });

        binding.endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(Utils.getCurrentHour(), Utils.getCurrentMinutes(), false);
            }
        });

        binding.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDescriptionDialog();
            }
        });

        binding.deleteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDo == null) {
                    Utils.displaySnackBar(getString(R.string.cannot_delete_empty_task), binding.deleteLinearLayout).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.delete_massage);
                builder.setCancelable(true);

                builder.setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                toDoDetailViewModel.delete(toDo);
                                getActivity().onBackPressed();
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

        binding.saveLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToDoDetails();
            }
        });

        final CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getContext(), categories);
        binding.categorySpinner.setAdapter(categoriesAdapter);

        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories.get(position).getCategoryName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final PriorityAdapter priorityAdapter = new PriorityAdapter(getContext(), priorityNames);
        binding.prioritySpinner.setAdapter(priorityAdapter);

        binding.prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priority = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.doneToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isDone = isChecked;
                if (isChecked && isValidInput() && toDo != null) {
                    Utils.displaySnackBar(getString(R.string.task_complete_message),
                            binding.doneToggleButton).show();
                    toDo.setDone(isDone);
                    toDo.setCompletedDate(Utils.getCompletedDateTime());
                    toDoDetailViewModel.update(toDo);
                }
            }
        });

        setImageColor();

        binding.setTodomodel(toDo);
        binding.setTododetail(toDoDetailViewModel);

        observeViewModel(toDoDetailViewModel);
    }

    private void observeViewModel(final ToDoDetailViewModel viewModel) {
        viewModel.getToDo().observe(this, new Observer<ToDoModel>() {

            @Override
            public void onChanged(@Nullable ToDoModel toDoModel) {
                if (toDoModel != null) {
                    toDoDetailViewModel.setToDoModelObservableField(toDoModel);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChangeHeaderBar) {
            changeHeaderBar = (ChangeHeaderBar) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ChangeHeaderBar");
        }
    }

    private void setImageColor() {
        binding.saveImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        binding.shareImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        binding.moreImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
        binding.deleteImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.teal));
    }

    @Override
    public void onResume() {
        super.onResume();

        changeHeaderBar.changeHeaderBar();
        Utils.dismissKeyBoard(getContext());

        if (getArguments() != null && getArguments().containsKey(Constants.ISEDIT) &&
                getArguments().getBoolean(Constants.ISEDIT)) {
            binding.saveImageView.setEnabled(true);

            binding.deleteImageView.setEnabled(true);
            binding.editImageView.setEnabled(true);
        } else {
            binding.doneToggleButton.setEnabled(false);
            binding.deleteImageView.setEnabled(false);
            binding.editImageView.setEnabled(false);
            binding.maxLimit.setVisibility(View.GONE);
            binding.deleteImageView.setVisibility(View.VISIBLE);
        }

        if (toDo != null) {
            displayDetails(toDo);
        }

        binding.setTodomodel(toDo);
    }

    private void showTimePickerDialog(int h, int m, final boolean isStartTime) {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.timepicker_header, null);

        TimePickerDialog builder = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
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

                        if (isStartTime) {
                            startTime = time + daytime;
                        } else {
                            endTime = time + daytime;
                        }

                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.TIME_HH_MM_PATTERN);
                            Date date = simpleDateFormat.parse(time);
                            returnTime = new SimpleDateFormat(Constants.TIME_KK_MM_PATTERN).format(date) + daytime;
                            if (isStartTime) {
                                binding.startTime.setText(startTime);
                            } else {
                                binding.endTime.setText(endTime);
                            }
                        } catch (final ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, h, m, true);
        builder.setCustomTitle(view);
        builder.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        builder.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void getToDoDetails() {
        if (!isValidInput()) {
            Utils.displaySnackBar(getString(R.string.add_task_details), binding.saveLinearLayout).show();
            return;
        }

        if (toDo != null && toDo.isDone()) {
            Utils.displaySnackBar(getString(R.string.update_saved_task_message),
                    binding.doneToggleButton).show();
            return;
        }

        ToDoModel newToDo = new ToDoModel();

        if (isDone) {
            newToDo.setCompletedDate(Utils.getCompletedDateTime());
        }

        newToDo.setCategory(category);
        newToDo.setContent(binding.content.getText().toString().trim());

        String minutes = String.valueOf(Utils.getCurrentHour());
        String hours = String.valueOf(Utils.getCurrentHour());

        if (minutes.length() == 1) {
            minutes = Constants.ZERO_STRING + minutes;
        }

        String currentTime = "";
        if (Utils.getCurrentHour() >= 12) {
            currentTime = hours + Constants.COLON + minutes + Constants.SPACE + Constants.PM;
        } else {
            currentTime = hours + Constants.COLON + minutes + Constants.SPACE + Constants.AM;
        }

        String strStartFrom = Utils.getFormattedTime(binding.dueDate.getText().toString().trim(), currentTime);

        newToDo.setDueDate(strStartFrom);

        if (!TextUtils.isEmpty(startTime)) {
            newToDo.setStartTime(Utils.getFormattedTime(binding.dueDate.getText().toString().trim(), startTime));
        } else {
            newToDo.setStartTime(toDo.getStartTime());
        }

        if (!TextUtils.isEmpty(endTime)) {
            newToDo.setEndTime(Utils.getFormattedTime(binding.dueDate.getText().toString().trim(), endTime));
        } else {
            newToDo.setEndTime(toDo.getEndTime());
        }

        newToDo.setPriority(priority);
        newToDo.setDone(isDone);

        if (toDo != null) {
            newToDo.setCreatedDate(toDo.getCreatedDate());
            newToDo.setToDoGuid(toDo.getToDoGuid());
            newToDo.setId(toDo.getId());
            toDoDetailViewModel.update(newToDo);
        } else {
            newToDo.setCreatedDate(Utils.convertDateToString(new Date()));
            newToDo.setToDoGuid(UUID.randomUUID().toString());
            toDoDetailViewModel.insert(newToDo);
        }

        getActivity().onBackPressed();
    }

    //when done is clicked update db with model data and done indicator
    private ToDoModel assembleData() {

        return new ToDoModel();
    }

    private boolean isValidInput() {
        return !TextUtils.isEmpty(binding.content.getText()) &&
                !TextUtils.isEmpty(binding.dueDate.getText()) &&
                !TextUtils.isEmpty(binding.startTime.getText()) &&
                !TextUtils.isEmpty(binding.endTime.getText());
    }

    private void displayDetails(ToDoModel toDo) {
        binding.prioritySpinner.setSelection(toDo.getPriority());
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCategoryName().contains(toDo.getCategory())) {
                binding.categorySpinner.setSelection(i);
            }
        }

        if (toDo.isDone()) {
            enableEditText(false);
        }
    }

    private void enableEditText(boolean enabled) {
        binding.categorySpinner.setEnabled(enabled);
        binding.content.setEnabled(enabled);
        binding.dueDate.setEnabled(enabled);
        binding.startTime.setEnabled(enabled);
        binding.endTime.setEnabled(enabled);
        binding.prioritySpinner.setEnabled(enabled);
        binding.doneToggleButton.setEnabled(enabled);
        binding.saveImageView.setEnabled(enabled);
    }

    private void openDateTimePickerDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.date_time_dialog);

        final DatePicker myDatePicker = (DatePicker) dialog.findViewById(R.id.datePicker);

        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        TextView okButton = dialog.findViewById(R.id.okButton);

        myDatePicker.updateDate(myDatePicker.getYear(), myDatePicker.getMonth(), myDatePicker.getDayOfMonth());
        myDatePicker.setMinDate(System.currentTimeMillis() - 1000);

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
                String selectedDate = year + Constants.SPACE + Constants.ZERO_STRING + monthOfYear + Constants.SPACE + dayOfMonth;
                binding.dueDate.setText(Utils.getOutputDateFormt(selectedDate));
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        ;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void openDescriptionDialog() {
        final Dialog dialog;
        dialog = new Dialog(getContext(), R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.description_dialog);

        final EditText description = dialog.findViewById(R.id.description);
        final CustomTextView header = dialog.findViewById(R.id.headerTextView);
        header.setTextSize(18);

        Button saveButton = dialog.findViewById(R.id.saveButton);
        ImageView closeImageView = dialog.findViewById(R.id.close);
        ImageView saveImageView = dialog.findViewById(R.id.saveDescription);
        description.setText(binding.content.getText().toString());
        description.setSelection(description.getText().length());

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(description.getText().toString())) {
                    binding.content.setText(description.getText().toString().trim());
                }
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(description.getText().toString())) {
                    binding.content.setText(description.getText().toString().trim());
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
}
