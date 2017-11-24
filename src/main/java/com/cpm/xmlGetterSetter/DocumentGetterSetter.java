package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

/**
 * Created by yadavendras on 26-09-2017.
 */

public class DocumentGetterSetter {

    public String getTable_HR_DOCUMENTS() {
        return table_HR_DOCUMENTS;
    }

    public void setTable_HR_DOCUMENTS(String table_HR_DOCUMENTS) {
        this.table_HR_DOCUMENTS = table_HR_DOCUMENTS;
    }

    String table_HR_DOCUMENTS;

    ArrayList<String> document_id = new ArrayList<>();
    ArrayList<String> document_name = new ArrayList<>();
    ArrayList<String> document_descriiption = new ArrayList<>();
    ArrayList<String> document_url = new ArrayList<>();

    public ArrayList<String> getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id.add(document_id);
    }

    public ArrayList<String> getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name.add(document_name);
    }

    public ArrayList<String> getDocument_descriiption() {
        return document_descriiption;
    }

    public void setDocument_descriiption(String document_descriiption) {
        this.document_descriiption.add(document_descriiption);
    }

    public ArrayList<String> getDocument_url() {
        return document_url;
    }

    public void setDocument_url(String document_url) {
        this.document_url.add(document_url);
    }
}
