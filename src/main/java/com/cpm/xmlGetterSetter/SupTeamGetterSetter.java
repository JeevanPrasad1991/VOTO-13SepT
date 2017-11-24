package com.cpm.xmlGetterSetter;
import java.util.ArrayList;

/**
 * Created by jeevanp on 14-11-2017.
 */

public class SupTeamGetterSetter {
    public String supteamTable;
    ArrayList<String>emp_cd=new ArrayList<>();
    ArrayList<String>emp=new ArrayList<>();

    public ArrayList<String> getUserN() {
        return userN;
    }

    public void setUserN(String userN) {
        this.userN.add(userN);
    }

    ArrayList<String>userN=new ArrayList<>();

    public String getSupteamTable() {
        return supteamTable;
    }

    public void setSupteamTable(String supteamTable) {
        this.supteamTable = supteamTable;
    }

    public ArrayList<String> getEmp_cd() {
        return emp_cd;
    }

    public void setEmp_cd(String emp_cd) {
        this.emp_cd.add(emp_cd);
    }

    public ArrayList<String> getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp.add(emp);
    }

    public ArrayList<String> getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation.add(designation);
    }

    ArrayList<String>designation=new ArrayList<>();

}
