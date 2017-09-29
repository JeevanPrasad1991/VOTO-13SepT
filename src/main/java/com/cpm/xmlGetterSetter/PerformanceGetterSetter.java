package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 18-09-2017.
 */

public class PerformanceGetterSetter {
    public String performanceTable;
    ArrayList<String>target=new ArrayList<>();
    ArrayList<String>mtdSale=new ArrayList<>();

    public String getPerformanceTable() {
        return performanceTable;
    }

    public void setPerformanceTable(String performanceTable) {
        this.performanceTable = performanceTable;
    }

    public ArrayList<String> getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target.add(target);
    }

    public ArrayList<String> getMtdSale() {
        return mtdSale;
    }

    public void setMtdSale(String mtdSale) {
        this.mtdSale.add(mtdSale);
    }

    public ArrayList<String> getTodaySale() {
        return todaySale;
    }

    public void setTodaySale(String todaySale) {
        this.todaySale.add(todaySale);
    }

    ArrayList<String>todaySale=new ArrayList<>();


}
