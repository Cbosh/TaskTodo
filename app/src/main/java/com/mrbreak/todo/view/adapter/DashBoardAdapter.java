package com.mrbreak.todo.view.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrbreak.todo.R;
import com.mrbreak.todo.enums.CategoryEnum;
import com.mrbreak.todo.enums.PriorityEnum;
import com.mrbreak.todo.model.LegendModel;

import java.util.List;

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardViewHolder> {

    private List<LegendModel> legends;
    private OnItemClickListener onItemClickListener;

    //TODO: when item click load all todo's related to the selected item.
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public DashBoardAdapter(List<LegendModel> legends, OnItemClickListener onItemClickListener) {
        this.legends = legends;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return legends.size();
    }

    @Override
    public void onBindViewHolder(DashBoardViewHolder dashboardViewHolder, int i) {
        if (PriorityEnum.HIGH.getIntValue() == legends.get(i).getLegend()) {
            dashboardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashboardViewHolder.legend.getContext(), R.drawable.high_priority));
        } else if (PriorityEnum.MEDIUM.getIntValue() == legends.get(i).getLegend()) {
            dashboardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashboardViewHolder.legend.getContext(), R.drawable.medium_priority));
        } else if (PriorityEnum.LOW.getIntValue() == legends.get(i).getLegend()) {
            dashboardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashboardViewHolder.legend.getContext(), R.drawable.low_priority));
        } else if (CategoryEnum.GENERAL.getIntValue() == legends.get(i).getLegend()) {
            dashboardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashboardViewHolder.legend.getContext(), R.drawable.dashboard_general));
        } else if (CategoryEnum.WORK.getIntValue() == legends.get(i).getLegend()) {
            dashboardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashboardViewHolder.legend.getContext(), R.drawable.dashboard_work));
        } else if (CategoryEnum.STUDIES.getIntValue() == legends.get(i).getLegend()) {
            dashboardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashboardViewHolder.legend.getContext(), R.drawable.dashboard_studies));
        } else if (CategoryEnum.BUSINESS.getIntValue() == legends.get(i).getLegend()) {
            dashboardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashboardViewHolder.legend.getContext(), R.drawable.dashboard_business));
        } else if (CategoryEnum.PERSONAL.getIntValue() == legends.get(i).getLegend()) {
            dashboardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashboardViewHolder.legend.getContext(), R.drawable.dashboard_personal));
        }

        dashboardViewHolder.percentage.setText(legends.get(i).getPercentage());
    }

    @Override
    public DashBoardViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dash_board_item,
                viewGroup, false);
        final DashBoardViewHolder dashBoardViewHolder = new DashBoardViewHolder(v);

        if (PriorityEnum.HIGH.getIntValue() == legends.get(position).getLegend()) {
            dashBoardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashBoardViewHolder.legend.getContext(), R.drawable.dashboard_high_priority));
        } else if (PriorityEnum.MEDIUM.getIntValue() == legends.get(position).getLegend()) {
            dashBoardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashBoardViewHolder.legend.getContext(), R.drawable.dashboard_medium_priority));
        } else if (PriorityEnum.LOW.getIntValue() == legends.get(position).getLegend()) {
            dashBoardViewHolder.legend.setBackground(ContextCompat.getDrawable(
                    dashBoardViewHolder.legend.getContext(), R.drawable.dashboard_low_priority));
        }

        dashBoardViewHolder.percentage.setText(legends.get(position).getPercentage());
        dashBoardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, dashBoardViewHolder.getAdapterPosition());
            }
        });

        return dashBoardViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void remove(LegendModel object) {
        legends.remove(object);
        notifyDataSetChanged();
    }

    public List<LegendModel> getLegends() {
        return legends;
    }

    public int getSelectedCount() {
        return legends.size();
    }
}


class DashBoardViewHolder extends RecyclerView.ViewHolder {
    TextView legend;
    TextView percentage;

    DashBoardViewHolder(View itemView) {
        super(itemView);
        legend = itemView.findViewById(R.id.legend);
        percentage = itemView.findViewById(R.id.percentage);
    }
}
