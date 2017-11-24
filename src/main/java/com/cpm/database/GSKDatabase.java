package com.cpm.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cpm.Constants.CommonString;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.TableBean;
import com.cpm.xmlGetterSetter.AditGetterSetter;
import com.cpm.xmlGetterSetter.DocumentGetterSetter;
import com.cpm.xmlGetterSetter.JCPGetterSetter;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.ModelGetterSetter;
import com.cpm.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.xmlGetterSetter.PerformanceGetterSetter;
import com.cpm.xmlGetterSetter.SaleEntryGetterSetter;
import com.cpm.xmlGetterSetter.SupTeamGetterSetter;
import com.cpm.xmlGetterSetter.SupervisorAttendenceGetterSetter;

public class GSKDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "VOTO_DATABASES_10";
    public static final int DATABASE_VERSION = 11;
    private SQLiteDatabase db;
    // ********************************************Extract
    // Database*********************************************

    // ***********************************END COPY
    // DATABASE************************************************************
    public GSKDatabase(Context completeDownloadActivity) {
        super(completeDownloadActivity, DATABASE_NAME, null, DATABASE_VERSION);
    }// TODO Auto-generated constructor stub }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
        }
    }

    public void close() {
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // CREATING TABLE FOR GTGSK
        db.execSQL(TableBean.getSupteamtable());
        db.execSQL(TableBean.getAuditquestiontable());
        db.execSQL(TableBean.getjcptable());
        db.execSQL(TableBean.getNonworkingtable());
        db.execSQL(TableBean.getModeltable());
        db.execSQL(TableBean.getPerformancetable());
        db.execSQL(TableBean.getHrDocuments());
        db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
        db.execSQL(CommonString.CREATE_TABLE_INSERT_SALE_ENTRY_DATA);
        db.execSQL(CommonString.CREATE_TABLE_INSERT_STOCK_ENTRY_DATA);
        db.execSQL(CommonString.CREATE_TABLE_SUPERVISOR_ATTENDENCE_DATA);
        db.execSQL(CommonString.CREATE_TABLE_INSERT_AUDIT_DATA);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);
    }


    public void deleteSpecificStoreData(String storeid) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_SALE_ENTRY_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_STOCK_ENTRY_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_AUDIT_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);

        // db.delete(CommonString.TABLE_INSERT_SUPERVISOR_ATTENDENCE_DATA,null, null);


    }

    public void deleteSpecificStoreDataDailyE(String storeid) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_SALE_ENTRY_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_STOCK_ENTRY_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);

        db.delete(CommonString.TABLE_INSERT_AUDIT_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);

        // db.delete(CommonString.TABLE_INSERT_SUPERVISOR_ATTENDENCE_DATA,null, null);


    }


    public void deleteAllTables() {
        // DELETING TABLES GTGSK
        db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
        db.delete(CommonString.TABLE_INSERT_SALE_ENTRY_DATA, null, null);
        db.delete(CommonString.TABLE_INSERT_STOCK_ENTRY_DATA, null, null);

        db.delete(CommonString.TABLE_INSERT_AUDIT_DATA, null, null);

        // db.delete(CommonString.TABLE_INSERT_SUPERVISOR_ATTENDENCE_DATA, null, null);
    }

    //JCP data
    public void insertJCPData(JourneyPlanGetterSetter data) {
        db.delete("JOURNEY_PLAN", null, null);
        ContentValues values = new ContentValues();
        try {

            for (int i = 0; i < data.getStore_cd().size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.getStore_cd().get(i)));
                values.put("EMP_CD", Integer.parseInt(data.getEmp_cd().get(i)));
                values.put("VISIT_DATE", data.getVISIT_DATE().get(i));
                values.put("KEYACCOUNT", data.getKey_account().get(i));
                values.put("STORENAME", data.getStore_name().get(i));
                values.put("CITY", data.getCity().get(i));
                values.put("STORETYPE", data.getStore_type().get(i));
                values.put("UPLOAD_STATUS", data.getUploadStatus().get(i));
                values.put("CHECKOUT_STATUS", data.getCheckOutStatus().get(i));
                db.insert("JOURNEY_PLAN", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert JCP Data ",
                    ex.toString());
        }

    }


    public void insertJCPDataforSup(ArrayList<JourneyPlanGetterSetter> data) {
        db.delete("JOURNEY_PLAN", "STORE_CD" + "='" + data.get(0).getStore_cd().get(0) + "'AND VISIT_DATE='" + data.get(0).getStore_cd().get(0) + "'", null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.get(i).getStore_cd().get(0)));
                values.put("EMP_CD", Integer.parseInt(data.get(i).getEmp_cd().get(0)));
                values.put("VISIT_DATE", data.get(i).getVISIT_DATE().get(0));
                values.put("KEYACCOUNT", data.get(i).getKey_account().get(0));
                values.put("STORENAME", data.get(i).getStore_name().get(0));
                values.put("CITY", data.get(i).getCity().get(0));
                values.put("STORETYPE", data.get(i).getStore_type().get(0));
                values.put("UPLOAD_STATUS", data.get(i).getUploadStatus().get(0));
                values.put("CHECKOUT_STATUS", data.get(i).getCheckOutStatus().get(0));
                db.insert("JOURNEY_PLAN", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert JCP Data ", ex.toString());
        }
    }


    //Non Working data
    public void insertNonWorkingReasonData(NonWorkingReasonGetterSetter data) {
        db.delete("NON_WORKING_REASON", null, null);
        ContentValues values = new ContentValues();
        try {

            for (int i = 0; i < data.getReason_cd().size(); i++) {
                values.put("REASON_CD", Integer.parseInt(data.getReason_cd().get(i)));
                values.put("REASON", data.getReason().get(i));
                values.put("ENTRY_ALLOW", data.getEntry_allow().get(i));


                values.put("IMAGE_ALLOW", data.getImage_allow().get(i));
                values.put("FOR_ATT", data.getFor_attendence().get(i));

                db.insert("NON_WORKING_REASON", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Non Working Data ",
                    ex.toString());
        }

    }


    public ArrayList<ModelGetterSetter> getmodeldata() {
        ArrayList<ModelGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from MODEL_MASTER ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    ModelGetterSetter sb = new ModelGetterSetter();
                    sb.setModel_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MODEL_CD")));
                    sb.setModel(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MODEL")));
                    sb.setStockQuantity("");
                    sb.setStaus("");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    //insertModelData data
    public void insertModelData(ModelGetterSetter data) {
        db.delete("MODEL_MASTER", null, null);
        ContentValues values = new ContentValues();
        try {

            for (int i = 0; i < data.getModel_cd().size(); i++) {
                values.put("MODEL_CD", Integer.parseInt(data.getModel_cd().get(i)));
                values.put("MODEL", data.getModel().get(i));
                db.insert("MODEL_MASTER", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Non Working Data ",
                    ex.toString());
        }

    }


    public void insertSupTeamData(SupTeamGetterSetter data) {
        db.delete("TEAM_LIST_SUP", null, null);
        ContentValues values = new ContentValues();
        try {

            for (int i = 0; i < data.getEmp_cd().size(); i++) {
                values.put("EMP_CD", Integer.parseInt(data.getEmp_cd().get(i)));
                values.put("ISD", data.getEmp().get(i));
                values.put("DESIGNATION", data.getDesignation().get(i));
                values.put("USERNAME", data.getUserN().get(i));
                db.insert("TEAM_LIST_SUP", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Non Working Data ",
                    ex.toString());
        }

    }


    public ArrayList<SupTeamGetterSetter> getISDlist() {
        Log.d("FetchingStoredata--------------->Start<------------", "------------------");
        ArrayList<SupTeamGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from TEAM_LIST_SUP ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    SupTeamGetterSetter sb = new SupTeamGetterSetter();
                    sb.setEmp_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD")));
                    sb.setEmp(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ISD")));
                    sb.setDesignation(dbcursor.getString(dbcursor.getColumnIndexOrThrow("DESIGNATION")));
                    sb.setUserN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("USERNAME")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    public ArrayList<SupTeamGetterSetter> getselectedISDSpinData(String emp_id) {
        ArrayList<SupTeamGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from TEAM_LIST_SUP  where EMP_CD = '" + emp_id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    SupTeamGetterSetter sb = new SupTeamGetterSetter();
                    sb.setEmp(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ISD")));
                    //  sb.setUserN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("USERNAME")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!", e.toString());
            dbcursor.close();
            return list;
        }
        dbcursor.close();
        return list;
    }


    public ArrayList<PerformanceGetterSetter> getperformancedata() {
        ArrayList<PerformanceGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from MY_PERFORMANCE ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PerformanceGetterSetter sb = new PerformanceGetterSetter();
                    sb.setTarget(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MY_TARGET")));
                    sb.setMtdSale(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MTD_SALE")));
                    sb.setTodaySale(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TODAYS_SALE")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    //insertMyPerformanceData data
    public void insertMyPerformanceData(PerformanceGetterSetter data) {
        db.delete("MY_PERFORMANCE", null, null);
        ContentValues values = new ContentValues();
        try {

            for (int i = 0; i < data.getTodaySale().size(); i++) {
                values.put("MY_TARGET", Integer.parseInt(data.getTarget().get(i)));
                values.put("MTD_SALE", data.getMtdSale().get(i));
                values.put("TODAYS_SALE", data.getTodaySale().get(i));
                db.insert("MY_PERFORMANCE", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Non Working Data ",
                    ex.toString());
        }


    }


    public void insertauditQuestionData(AditGetterSetter data) {
        db.delete("AUDIT_QUESTION_SUP", null, null);
        ContentValues values = new ContentValues();
        try {

            for (int i = 0; i < data.getAns_id().size(); i++) {
                values.put("QUESTION_ID", Integer.parseInt(data.getQuest_id().get(i)));
                values.put("QUESTION", data.getQuest().get(i));
                values.put("QUESTION_TYPE", data.getQuest_type().get(i));
                values.put("ANSWER_ID", data.getAns_id().get(i));
                values.put("ANSWER", data.getAns().get(i));
                db.insert("AUDIT_QUESTION_SUP", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Non Working Data ",
                    ex.toString());
        }


    }


    public ArrayList<AditGetterSetter> getauditQuestion() {
        ArrayList<AditGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT DISTINCT QUESTION_ID,QUESTION  from AUDIT_QUESTION_SUP", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AditGetterSetter df = new AditGetterSetter();
                    df.setQuest_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_ID")));
                    df.setQuest(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION")));
                    df.setCurrectanswer("");
                    df.setCurrectanswerCd("");
                    list.add(df);
                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }
        } catch (Exception e) {

            return list;
        }

        return list;

    }


    public ArrayList<AditGetterSetter> getauditAnswerData(String question_id) {
        ArrayList<AditGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT DISTINCT ANSWER_ID,ANSWER ,QUESTION_TYPE from AUDIT_QUESTION_SUP WHERE QUESTION_ID ='" + question_id + "' ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AditGetterSetter df = new AditGetterSetter();
                    df.setAns_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER_ID")));
                    df.setAns(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER")));
                    df.setQuest_type(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_TYPE")));


                    list.add(df);
                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }
        } catch (Exception e) {

            return list;
        }

        return list;

    }


    //get Document Data

    public ArrayList<DocumentGetterSetter> getDocumentData() {
        ArrayList<DocumentGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from HR_DOCUMENTS", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    DocumentGetterSetter df = new DocumentGetterSetter();
                    df.setDocument_id(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("DOCUMENT_ID")));
                    df.setDocument_name(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("DOCUMENT_NAME")));
                    df.setDocument_descriiption(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("DOCUMENT_DESCRIPTION")));
                    df.setDocument_url(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("DOCUMENT_URL")));

                    list.add(df);
                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }
        } catch (Exception e) {

            return list;
        }

        return list;

    }

    public void insertDocumentData(DocumentGetterSetter data) {

        db.delete("HR_DOCUMENTS", null, null);
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < data.getDocument_id().size(); i++) {

                values.put("DOCUMENT_ID", data.getDocument_id().get(i));
                values.put("DOCUMENT_NAME", data.getDocument_name().get(i));
                values.put("DOCUMENT_DESCRIPTION", data.getDocument_descriiption().get(i));
                values.put("DOCUMENT_URL", data.getDocument_url().get(i));

                db.insert("HR_DOCUMENTS", null, values);
            }

        } catch (Exception ex) {

        }

    }


    //get JCP Data

    public ArrayList<JourneyPlanGetterSetter> getJCPData(String date) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<JourneyPlanGetterSetter>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN where VISIT_DATE = '" + date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setKey_account(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEYACCOUNT")));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORENAME"))));
                    sb.setCity((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY"))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setVISIT_DATE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE"))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    //get JCP Data

    public ArrayList<JourneyPlanGetterSetter> getAllJCPData() {
        Log.d("FetchingStoredata--------------->Start<------------", "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<JourneyPlanGetterSetter>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setKey_account(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEYACCOUNT")));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORENAME"))));
                    sb.setCity((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY"))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setVISIT_DATE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE"))));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


//get DeepFreezerType Data


    //check if table is empty
    public boolean isCompetitionDataFilled(String storeId) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {

            dbcursor = db
                    .rawQuery(
                            "SELECT * FROM FACING_COMPETITOR_DATA WHERE STORE_CD= '" + storeId + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }


    public ArrayList<NonWorkingReasonGetterSetter> getNonWorkingData(boolean flag) {
        Log.d("FetchingAssetdata--------------->Start<------------", "------------------");
        ArrayList<NonWorkingReasonGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM NON_WORKING_REASON ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (flag) {
                        NonWorkingReasonGetterSetter sb = new NonWorkingReasonGetterSetter();
                        String name = dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW"));
                        if (name.equals("1")) {
                            sb.setReason_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_CD")));
                            sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON")));
                            sb.setEntry_allow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW")));
                            list.add(sb);
                        }
                    } else {
                        NonWorkingReasonGetterSetter sb = new NonWorkingReasonGetterSetter();
                        sb.setReason_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_CD")));
                        sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON")));
                        sb.setEntry_allow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW")));
                        list.add(sb);
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Non working!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching non working data---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    // get Asset data
    public ArrayList<NonWorkingReasonGetterSetter> getNonWorkingDataforAttendence() {
        Log.d("FetchingAssetdata--------------->Start<------------",
                "------------------");
        ArrayList<NonWorkingReasonGetterSetter> list = new ArrayList<NonWorkingReasonGetterSetter>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM NON_WORKING_REASON WHERE FOR_ATT ='1'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonWorkingReasonGetterSetter sb = new NonWorkingReasonGetterSetter();
                    sb.setReason_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_CD")));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON")));
                    sb.setEntry_allow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Non working!!!!!!!!!!!", e.toString());
            return list;
        }
        Log.d("Fetching non working data---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

//Update Midday Data with Brand


    //check if table is empty
    public boolean isAssetDataFilled(String storeId) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {

            dbcursor = db
                    .rawQuery(
                            "SELECT * FROM ASSET_DATA WHERE STORE_CD= '" + storeId + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }


//Get Asset Upload Data 


    //Get Promotion Upload Data


    //Get Promotion Upload Data


    //check if table is empty
    public boolean isPromotionDataFilled(String storeId) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {

            dbcursor = db
                    .rawQuery(
                            "SELECT * FROM PROMOTION_DATA WHERE STORE_CD= '" + storeId + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

//Get Promotion Upload Data 


//Get Promotion Database Data 


    //check if table is empty


//Get Stock Upload Data 


    //Get Opening and Midday Stock for validation


    //opening stock


    //opening stock


    //check if table is empty
    public boolean isOpeningDataFilled(String storeId) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {

            dbcursor = db
                    .rawQuery(
                            "SELECT OPENING_TOTAL_STOCK, FACING FROM STOCK_DATA WHERE STORE_CD= '" + storeId + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("OPENING_TOTAL_STOCK")).equals("") || dbcursor.getString(dbcursor.getColumnIndexOrThrow("FACING")).equals("")) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }

                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }


    public long InsertCoverageData(CoverageBean data) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, "STORE_ID" + "='" + data.getStoreId() + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_STORE_ID, data.getStoreId());
            values.put(CommonString.KEY_USER_ID, data.getUserId());
            values.put(CommonString.KEY_IN_TIME, data.getInTime());
            values.put(CommonString.KEY_OUT_TIME, data.getOutTime());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
            values.put(CommonString.KEY_LATITUDE, data.getLatitude());
            values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());
            values.put(CommonString.KEY_COVERAGE_STATUS, data.getStatus());
            values.put(CommonString.KEY_IMAGE, data.getImage());
            values.put(CommonString.KEY_IMAGE02, data.getImage02());
            values.put(CommonString.KEY_COVERAGE_REMARK, data.getRemark());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());
            values.put(CommonString.KEY_EMP_CD, data.getEmp_cd());
            l = db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Closes Data ", ex.toString());
        }
        return l;
    }


    public void deletePreviousUploadedData(String visit_date) {
        Cursor dbcursor = null;
        Cursor dbcursor1 = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from COVERAGE_DATA where VISIT_DATE < '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
                    db.delete(CommonString.TABLE_INSERT_SALE_ENTRY_DATA, null, null);
                    db.delete(CommonString.TABLE_INSERT_STOCK_ENTRY_DATA, null, null);
                    db.delete(CommonString.TABLE_INSERT_AUDIT_DATA, null, null);
                    db.delete(CommonString.TABLE_INSERT_SUPERVISOR_ATTENDENCE_DATA, null, null);
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());

        }
    }


    // getCoverageData
    public ArrayList<CoverageBean> getCoverageData(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setImage02(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE02)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setEmp_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMP_CD)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }

    public ArrayList<CoverageBean> getCoverageDataReason(String visitdate, String store_cd) {

        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT  * from " +
                    CommonString.TABLE_COVERAGE_DATA + " where " +
                    CommonString.KEY_VISIT_DATE + "='" + visitdate + "' AND " +
                    CommonString.KEY_STORE_ID + "='" + store_cd + "'", null);


            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setImage02(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE02)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setEmp_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMP_CD)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    public ArrayList<CoverageBean> getCoveragDailyEntry(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setImage02(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE02)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));

                    sb.setEmp_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMP_CD)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    public ArrayList<CoverageBean> getCoverageStoreData(String store_cd) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_STORE_ID + "='" + store_cd + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setImage02(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE02)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));

                    sb.setEmp_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMP_CD)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!", e.toString());

        }

        return list;

    }


    public long updateOutTime(CoverageBean data, String StoreId, String VisitDate) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, data.getStatus());
            values.put(CommonString.KEY_OUT_TIME, data.getOutTime());
            values.put(CommonString.KEY_IMAGE02, data.getImage02());
            l = db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_STORE_ID + "='" + StoreId + "' AND " + CommonString.KEY_VISIT_DATE + "='" + VisitDate + "'", null);
        } catch (Exception e) {

        }
        return l;
    }


    // getCoverageData
    public ArrayList<CoverageBean> getCoverageSpecificData(String store_id) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_STORE_ID + "='" + store_id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setImage02(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE02)));

                    sb.setEmp_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMP_CD)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }

    //check if table is empty
    public boolean isCoverageDataFilled(String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            //  dbcursor = db.rawQuery("SELECT * FROM COVERAGE_DATA " + "where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + " '", null);
            dbcursor = db.rawQuery("SELECT * FROM COVERAGE_DATA " + "where "
                    + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "' AND Coverage <> 'INVALID'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }


    public void updateCoverageStatus(int mid, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_ID + "=" + mid, null);
        } catch (Exception e) {

        }
    }


    public void updateCoverageStoreStatus(String StoreId, String VisitDate, String status) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_STORE_ID + "='" + StoreId + "' AND " + CommonString.KEY_VISIT_DATE + "='" + VisitDate + "'", null);
        } catch (Exception e) {

        }
    }


    public void updateStoreStatusOnLeave(String storeid, String visitdate, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put("UPLOAD_STATUS", status);
            db.update("JOURNEY_PLAN", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
        } catch (Exception e) {
        }
    }


    public void updateStoreStatusOnCheckout(String storeid, String visitdate, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_CHECKOUT_STATUS, status);
            db.update("JOURNEY_PLAN", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
        } catch (Exception e) {
        }
    }


    public long updateAuditStatus(String storeid, String visitdate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("STATUS", status);
            l = db.update("AUDIT_DATA", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND VISIT_DATE ='" + visitdate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    public ArrayList<AditGetterSetter> getinsertedDatafromDatabasedata(String store_id) {
        ArrayList<AditGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from "
                    + CommonString.TABLE_INSERT_AUDIT_DATA + " where " + CommonString.KEY_STORE_CD + "='" + store_id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AditGetterSetter sb = new AditGetterSetter();
                    sb.setQuest(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION")));
                    sb.setQuest_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CD")));
                    sb.setCurrectanswer(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER")));
                    sb.setCurrectanswerCd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER_CD")));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STATUS")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());
        }
        return list;

    }

    public long insertAuditdata(String store_cd, String visit_date, String user, ArrayList<AditGetterSetter> list) {
        db.delete(CommonString.TABLE_INSERT_AUDIT_DATA, "STORE_CD" + "='" + store_cd + "'AND VISIT_DATE='" + visit_date + "'", null);
        long l = 0;
        ContentValues values = new ContentValues();
        try {

            for (int i = 0; i < list.size(); i++) {
                values.put("STORE_CD", store_cd);
                values.put("VISIT_DATE", visit_date);
                values.put("USER_ID", user);
                values.put("STATUS", "N");
                values.put("QUESTION", list.get(i).getQuest().get(0));
                values.put("QUESTION_CD", list.get(i).getQuest_id().get(0));
                values.put("ANSWER", list.get(i).getCurrectanswer());
                values.put("ANSWER_CD", list.get(i).getCurrectanswerCd());

                l = db.insert(CommonString.TABLE_INSERT_AUDIT_DATA, null, values);
            }
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ",
                    ex.toString());
        }
        return l;
    }


    public long insertSalesEntrydata(String store_cd, String user_name, String visit_date, ArrayList<SaleEntryGetterSetter> list) {
        db.delete(CommonString.TABLE_INSERT_SALE_ENTRY_DATA, "STORE_CD" + "='" + store_cd + "'AND VISIT_DATE='" + visit_date + "'", null);
        long l = 0;
        ContentValues values = new ContentValues();
        try {

            for (int i = 0; i < list.size(); i++) {
                values.put("STORE_CD", store_cd);
                values.put("USER_ID", user_name);
                values.put("VISIT_DATE", visit_date);
                values.put("STATUS", list.get(i).getSatus());
                values.put("IMEI", list.get(i).getImeino());
                values.put("MODEL_NO", list.get(i).getModelno());
                values.put("MODEL", list.get(i).getModel());
                l = db.insert(CommonString.TABLE_INSERT_SALE_ENTRY_DATA, null, values);
            }
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ",
                    ex.toString());
        }
        return l;
    }


    public ArrayList<SaleEntryGetterSetter> getinsertedSalesEntrydata(String store_id) {
        ArrayList<SaleEntryGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_INSERT_SALE_ENTRY_DATA +
                    " where " + CommonString.KEY_STORE_CD + "='" + store_id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    SaleEntryGetterSetter sb = new SaleEntryGetterSetter();
                    sb.setImeino(dbcursor.getString(dbcursor.getColumnIndexOrThrow("IMEI")));
                    sb.setModelno(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MODEL_NO")));
                    sb.setModel(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MODEL")));
                    sb.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEY_ID")));
                    sb.setSatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STATUS")));
                    sb.setVisit_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());
        }
        return list;

    }

    public void remove(String user_id) {
        // String string =String.valueOf(user_id);
        db.execSQL("DELETE FROM SALE_data WHERE KEY_ID = '" + user_id + "'");
    }


    public long updateJaurneyPlanSpecificStoreStatus(String storeid, String visit_date, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("CHECKOUT_STATUS", status);
            l = db.update("JOURNEY_PLAN", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND VISIT_DATE ='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    public long updateSaleDataStatus(String storeid, String key_id, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("STATUS", status);
            l = db.update(CommonString.TABLE_INSERT_SALE_ENTRY_DATA, values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND KEY_ID ='" + key_id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    //check if table is empty
    public boolean isClosingDataFilled(String storeId) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT CLOSING_STOCK FROM STOCK_DATA WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("CLOSING_STOCK")).equals("")) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }

                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;


    }


    public boolean isNoSaleSEntryFilled(String storeId) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT IMEI FROM SALE_data WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("IMEI")).equals("0")) {
                        filled = true;
                        break;
                    } else {
                        filled = false;
                    }

                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;


    }

    public boolean isMiddayDataFilled(String storeId) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT IMEI FROM SALE_data WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("IMEI")).equals("")) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }

                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }


    public long insertStockEntrydata(String user_name, String store_cd, String visit_date,
                                     ArrayList<ModelGetterSetter> list) {
        db.delete(CommonString.TABLE_INSERT_STOCK_ENTRY_DATA, "STORE_CD" + "='" + store_cd + "'", null);
        long l = 0;
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < list.size(); i++) {
                values.put("STORE_CD", store_cd);
                values.put("VISIT_DATE", visit_date);
                values.put("USER_ID", user_name);
                values.put("STATUS", "N");
                values.put("MODEL", list.get(i).getModel().get(0));
                values.put("MODEL_CD", list.get(i).getModel_cd().get(0));
                values.put("QUANTITY", list.get(i).getStockQuantity());
                l = db.insert(CommonString.TABLE_INSERT_STOCK_ENTRY_DATA, null, values);
            }
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ", ex.toString());
        }
        return l;
    }


    public long updateStockentryStatus(String storeid, String visitdate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("STATUS", status);
            l = db.update("STOCK_ENTRY_DATA", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND VISIT_DATE ='" + visitdate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    public ArrayList<ModelGetterSetter> getinsertedStockEntryData(String date, String store_cd) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<ModelGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from STOCK_ENTRY_DATA where VISIT_DATE = '" + date + "'AND STORE_CD ='" + store_cd + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    ModelGetterSetter sb = new ModelGetterSetter();
                    sb.setStockQuantity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUANTITY")));
                    sb.setModel(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MODEL")));
                    sb.setModel_cd((dbcursor.getString(dbcursor.getColumnIndexOrThrow("MODEL_CD"))));
                    sb.setStaus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STATUS"))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    public boolean isStockEntryWithFilled(String storeId) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT QUANTITY FROM STOCK_ENTRY_DATA WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUANTITY")).equals("")) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;


    }


    public boolean isAuditEntryFilled(String storeId) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT ANSWER_CD FROM AUDIT_DATA WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER_CD")).equals("")) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;


    }


    public boolean isStockEntryFilled(String storeId) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT STATUS FROM STOCK_ENTRY_DATA WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("STATUS")).equals("N")) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }

                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }
        return filled;
    }


    public long updateSupAttendenceStatus(String visitdate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("STATUS", status);
            l = db.update("SUPERVISOR_ATTENDENCE_DATA", values, " VISIT_DATE ='" + visitdate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    public SupervisorAttendenceGetterSetter getsupervisorAttendenceData(String visit_date) {
        SupervisorAttendenceGetterSetter sb = new SupervisorAttendenceGetterSetter();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_INSERT_SUPERVISOR_ATTENDENCE_DATA + " where  VISIT_DATE='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REMARK")));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON")));
                    sb.setReason_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_CD")));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STATUS")));
                    sb.setEntry_allow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW")));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("IMAGE")));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());
        }
        return sb;

    }

    public long insertsupervisorattendencedata(String user_name, String visit_date,
                                               String remark, String reason, String reason_cd, String entry_allow, String image) {
        db.delete(CommonString.TABLE_INSERT_SUPERVISOR_ATTENDENCE_DATA,
                "USER_ID" + "='" + user_name + "' AND VISIT_DATE='" + visit_date + "'", null);
        long l = 0;
        ContentValues values = new ContentValues();
        try {
            values.put("USER_ID", user_name);
            values.put("VISIT_DATE", visit_date);
            values.put("STATUS", "D");
            values.put("REMARK", remark);
            values.put("REASON", reason);
            values.put("REASON_CD", reason_cd);
            values.put("ENTRY_ALLOW", entry_allow);
            values.put("IMAGE", image);
            l = db.insert(CommonString.TABLE_INSERT_SUPERVISOR_ATTENDENCE_DATA, null, values);

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ", ex.toString());
        }
        return l;
    }


    public boolean isSupAttendenceDataFilled(String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            //  dbcursor = db.rawQuery("SELECT * FROM COVERAGE_DATA " + "where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + " '", null);
            dbcursor = db.rawQuery("SELECT * FROM SUPERVISOR_ATTENDENCE_DATA " + "where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + " '", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }


    public void jcpjourneyPlan(String name) {
        db.execSQL(name);
    }

    public void InsertTEMPJCPDATA(JourneyPlanGetterSetter data) {
        db.delete("JOURNEY_PLAN_SUP", null, null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.getStore_cd().size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.getStore_cd().get(i)));
                values.put("EMP_CD", Integer.parseInt(data.getEmp_cd().get(i)));
                values.put("VISIT_DATE", data.getVISIT_DATE().get(i));
                values.put("KEYACCOUNT", data.getKey_account().get(i));
                values.put("STORENAME", data.getStore_name().get(i));
                values.put("CITY", data.getCity().get(i));
                values.put("STORETYPE", data.getStore_type().get(i));
                values.put("UPLOAD_STATUS", data.getUploadStatus().get(i));
                values.put("CHECKOUT_STATUS", data.getCheckOutStatus().get(i));
                db.insert("JOURNEY_PLAN_SUP", null, values);
            }


        } catch (Exception ex) {
            Log.d("Database Exception while Insert Journey plan ",
                    ex.toString());
        }
    }


    public void  deleteSupervisorJaurneyPlanData(String store_cd){
        db.delete("JOURNEY_PLAN", CommonString.KEY_STORE_CD + "='" + store_cd + "'", null);

    }

    public ArrayList<JourneyPlanGetterSetter> getJourneyPlanTempData(String date) {
        Log.d("FetchingStoredata--------------->Start<------------", "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<JourneyPlanGetterSetter>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN_SUP where VISIT_DATE = '" + date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();

                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setEmp_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD")));
                    sb.setVISIT_DATE(dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE")));

                    sb.setKey_account(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEYACCOUNT")));
                    sb.setStore_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORENAME")));
                    sb.setCity((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY"))));
                    sb.setStore_type(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORETYPE")));

                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS")));
                    sb.setCheckOutStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


}
