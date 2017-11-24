package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 15-11-2017.
 */

public class AditGetterSetter {
    public String auditTable;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String status;

    public String getCurrectanswerCd() {
        return currectanswerCd;
    }

    public void setCurrectanswerCd(String currectanswerCd) {
        this.currectanswerCd = currectanswerCd;
    }

    public String currectanswerCd;
    public String getCurrectanswer() {
        return currectanswer;
    }

    public void setCurrectanswer(String currectanswer) {
        this.currectanswer = currectanswer;
    }

    public String currectanswer;
    ArrayList<String>quest_id=new ArrayList<>();
    ArrayList<String>quest=new ArrayList<>();
    ArrayList<String>quest_type=new ArrayList<>();

    public String getAuditTable() {
        return auditTable;
    }

    public void setAuditTable(String auditTable) {
        this.auditTable = auditTable;
    }

    public ArrayList<String> getQuest_id() {
        return quest_id;
    }

    public void setQuest_id(String quest_id) {
        this.quest_id.add(quest_id);
    }

    public ArrayList<String> getQuest() {
        return quest;
    }

    public void setQuest(String quest) {
        this.quest.add(quest);
    }

    public ArrayList<String> getQuest_type() {
        return quest_type;
    }

    public void setQuest_type(String quest_type) {
        this.quest_type.add(quest_type);
    }

    public ArrayList<String> getAns_id() {
        return ans_id;
    }

    public void setAns_id(String ans_id) {
        this.ans_id.add(ans_id);
    }

    public ArrayList<String> getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans.add(ans);
    }

    ArrayList<String>ans_id=new ArrayList<>();
    ArrayList<String>ans=new ArrayList<>();
}
