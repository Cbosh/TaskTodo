package com.mrbreak.todo.customspinners;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mrbreak.todo.R;

public class PriorityAdapter extends BaseAdapter {
    Context context;
    String[] PriorityNames;
    LayoutInflater inflater;

    public PriorityAdapter(Context applicationContext, String[] PriorityNames) {
        this.context = applicationContext;
        this.PriorityNames = PriorityNames;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return PriorityNames.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.priority_spinner, null);
        TextView priorityIcon = view.findViewById(R.id.priorityIcon);
        TextView priorityName = view.findViewById(R.id.priorityName);
        priorityName.setText(PriorityNames[i]);

        switch (i) {
            case 0:
                priorityIcon.setBackgroundResource(R.drawable.high_priority);
                break;
            case 1:
                priorityIcon.setBackgroundResource(R.drawable.medium_priority);
                break;
            case 2:
                priorityIcon.setBackgroundResource(R.drawable.low_priority);
                break;
            default:
        }
        view.setPadding(10, 20, view.getPaddingRight(),
                20);
        view.setBackgroundResource(R.color.off_white);
        return view;
    }
}
