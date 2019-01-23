package com.mrbreak.todo.model;


public class Legend {
    private int legend;
    private String percentage;

    public Legend(int legend, String percentage) {
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
