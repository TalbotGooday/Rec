package com.example.talbotgooday.rec.model;


import java.util.Comparator;

public class ResultModel{
    private String name;
    private double value;

    public ResultModel(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public String getValueString()
    {
        return String.valueOf(value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static class ResultComparator  implements Comparator<ResultModel> {
        @Override
        public int compare(ResultModel lhs, ResultModel rhs) {

            return lhs.getValue() > rhs.getValue() ? 1 : -1;
        }
    }
}
