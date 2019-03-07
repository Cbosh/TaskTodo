package com.mrbreak.todo.model;

public class DashBoardFilterModel {
    private boolean isDone;
    private String startDate;
    private String endDate;

    public DashBoardFilterModel() {
    }

    public DashBoardFilterModel(boolean isDone, String startDate, String endDate) {
        this.isDone = isDone;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }


}
