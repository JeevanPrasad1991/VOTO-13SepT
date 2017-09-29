package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 18-09-2017.
 */

public class ModelGetterSetter {
    String modeltable;

    public String getStaus() {
        return staus;
    }

    public void setStaus(String staus) {
        this.staus = staus;
    }

    String staus="N";

    public String getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    String  stockQuantity="";
    ArrayList<String>model=new ArrayList<>();

    public String getModeltable() {
        return modeltable;
    }

    public void setModeltable(String modeltable) {
        this.modeltable = modeltable;
    }

    public ArrayList<String> getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model.add(model);
    }

    public ArrayList<String> getModel_cd() {
        return model_cd;
    }

    public void setModel_cd(String model_cd) {
        this.model_cd.add(model_cd);
    }

    ArrayList<String>model_cd=new ArrayList<>();
}
