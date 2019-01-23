package com.mrbreak.todo.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.birbit.android.jobqueue.JobManager;
import com.mrbreak.todo.R;
import com.mrbreak.todo.jobs.AddEditToDoJob;
import com.mrbreak.todo.model.ToDo;
import com.mrbreak.todo.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoViewHolder> {

    private List<ToDo> toDos;
    private OnItemClickListener onItemClickListener;
    private SparseBooleanArray mSelectedItemsIds;
    private JobManager jobManager;
    private EventBus eventBus;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public ToDoAdapter(List<ToDo> toDos, OnItemClickListener onItemClickListener,
                       JobManager jobManager, EventBus eventBus) {
        this.toDos = toDos;
        this.onItemClickListener = onItemClickListener;
        this.jobManager = jobManager;
        this.eventBus = eventBus;
    }

    @Override
    public int getItemCount() {
        return toDos.size();
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder toDoViewHolder, int i) {
        toDoViewHolder.category.setText(toDos.get(i).getCategory());
        toDoViewHolder.content.setText(toDos.get(i).getContent());

        Date date = Utils.convertStringToDate(toDos.get(i).getDueDate());
        String days = String.valueOf(Utils.getDaysDifference(new Date(), date));
        int daysInt = Integer.parseInt(days);

        if (toDos.get(i).isDone()) {
            toDoViewHolder.doneToggleButton.setEnabled(false);
            days = "Completed on " + toDos.get(i).getCompletedDate();
        } else {
            if (days.contentEquals("0") && Utils.checkTimings(toDos.get(i).getStartTime(),
                    toDos.get(i).getEndTime())) {
                days = "Due in " + toDos.get(i).getEndTime();
            } else if (daysInt < 0) {
                days = "Times up!!";
            } else {
                days = "Due in " + days + " Days";
            }
        }

        toDoViewHolder.dueDate.setText(days);

        date = Utils.convertStringToDate(toDos.get(i).getCreatedDate());
        days = String.valueOf(Utils.getDaysDifference(new Date(), date));

        if (days.contentEquals("0")) {
            days = "Created Today";
        } else {
            days = "Created " + days + " days ago";
        }

        toDoViewHolder.createdDate.setText(days);

        switch (toDos.get(i).getPriority()) {
            case 0:
                toDoViewHolder.priorityLine.setBackgroundResource(R.drawable.high_priority_list);
                break;
            case 1:
                toDoViewHolder.priorityLine.setBackgroundResource(R.drawable.medium_priority_list);
                break;
            case 2:
                toDoViewHolder.priorityLine.setBackgroundResource(R.drawable.low_priority_list);
                break;
            default:
        }

        toDoViewHolder.doneToggleButton.setChecked(toDos.get(i).isDone());
        toDoViewHolder.lockImageView.setColorFilter(ContextCompat.getColor(toDoViewHolder.
                lockImageView.getContext(), R.color.androidDefaultColor));
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.to_do_item, viewGroup, false);
        final ToDoViewHolder toDoViewHolder = new ToDoViewHolder(v);
        toDoViewHolder.category.setText(toDos.get(position).getCategory());
        toDoViewHolder.content.setText(toDos.get(position).getContent());

        Date date = Utils.convertStringToDate(toDos.get(position).getDueDate());
        String createdDays = String.valueOf(Utils.getDaysDifference(new Date(), date));
        toDoViewHolder.dueDate.setText(createdDays);

        date = Utils.convertStringToDate(toDos.get(position).getCreatedDate());
        createdDays = String.valueOf(Utils.getDaysDifference(new Date(), date));
        toDoViewHolder.createdDate.setText(createdDays);

        toDoViewHolder.doneToggleButton.setChecked(toDos.get(position).isDone());

        toDoViewHolder.doneToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToDo toDo = toDos.get(toDoViewHolder.getAdapterPosition());
                toDo.setDone(true);
                toDo.setCompletedDate(Utils.convertDateToString(new Date()));
                jobManager.addJobInBackground(new AddEditToDoJob(eventBus, toDo));
                //   Utils.displaySnackBar("Well done for completing this task", toDoViewHolder.itemView).show();
            }
        });

        toDoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, toDoViewHolder.getAdapterPosition());
            }
        });

        return toDoViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void remove(ToDo object) {
        toDos.remove(object);
        notifyDataSetChanged();
    }

    public List<ToDo> getToDos() {
        return toDos;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}

class ToDoViewHolder extends RecyclerView.ViewHolder {
    TextView category;
    TextView priority;
    TextView priorityLine;
    TextView content;
    TextView dueDate;
    TextView createdDate;
    Switch doneToggleButton;
    ImageView lockImageView;

    ToDoViewHolder(View itemView) {
        super(itemView);
        category = itemView.findViewById(R.id.category);
        priority = itemView.findViewById(R.id.priority);
        content = itemView.findViewById(R.id.content);
        dueDate = itemView.findViewById(R.id.dueDate);
        createdDate = itemView.findViewById(R.id.createdDate);
        priorityLine = itemView.findViewById(R.id.priorityLine);
        doneToggleButton = itemView.findViewById(R.id.doneToggleButton);
        lockImageView = itemView.findViewById(R.id.lock_image_view);
    }
}
