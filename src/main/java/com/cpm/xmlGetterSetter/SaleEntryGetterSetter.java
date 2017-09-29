package com.cpm.xmlGetterSetter;

/**
 * Created by jeevanp on 14-09-2017.
 */

public class SaleEntryGetterSetter {
    String modelno;

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    String visit_date;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    String model;
    String imeino;

    public String getModelno() {
        return modelno;
    }

    public void setModelno(String modelno) {
        this.modelno = modelno;
    }

    public String getImeino() {
        return imeino;
    }

    public void setImeino(String imeino) {
        this.imeino = imeino;
    }

    public String getSatus() {
        return satus;
    }

    public void setSatus(String satus) {
        this.satus = satus;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    String satus = "N";
    String key_id;
}
