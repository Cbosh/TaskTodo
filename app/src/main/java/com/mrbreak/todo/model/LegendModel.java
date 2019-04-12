package com.mrbreak.todo.model;


public class LegendModel {
    private int legend;
    private String percentage;

    public LegendModel(int legend, String percentage) {
        this.legend = legend;
        this.percentage = percentage;
    }

    public int getLegend() {
        return legend;
    }

    public void setLegend(int legend) {
        this.legend = legend;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
