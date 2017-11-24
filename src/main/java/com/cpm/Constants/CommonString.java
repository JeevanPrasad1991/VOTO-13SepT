package com.cpm.Constants;

import android.os.Environment;

public class CommonString {
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/VotoImages/";

    public static final String BACKUP_PATH = Environment.getExternalStorageDirectory() + "/Voto_backup/";
    public static final String HRDOCUMENTS_PATH = Environment.getExternalStorageDirectory() + "/Hr_Documents/";

    public static final String URL_Notice_Board = "http://voto.parinaam.in/notice/notice.html";

    public static final String ONBACK_ALERT_MESSAGE = "Unsaved data will be lost - Do you want to continue?";

    public static final String DATA_DELETE_ALERT_MESSAGE = "Saved data will be lost - Do you want to continue?";
    public static final String PDF_URL = "PDF_URL";
    // preferenec keys
    public static final String KEY_NAME = "NAME";
    public static final String KEY_EMP_CD = "EMP_CD";
    public static final String FOLDER_PATH = Environment.getExternalStorageDirectory().toString() + "/Hr_Documents/";
    public static final String KEY_QUESTION_CD = "question_cd";
    public static final String KEY_ANSWER_CD = "answer_cd";
    public static final String KEY_IS_QUIZ_DONE = "is_quiz_done";
    public static final String METHOD_UPLOAD_DR_STORE_COVERAGE = "UPLOAD_COVERAGE1";
    public static final String KEY_STOREVISITED_STATUS = "STOREVISITED_STATUS";
    public static final String KEY_SUPERVISOR_ATTENDENCE_STATUS = "N";
    public static final String METHOD_UPLOAD_XML = "DrUploadXml";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REMEMBER = "remember";
    public static final String KEY_PATH = "path";
    public static final String KEY_VERSION = "version";
    public static final String MEHTOD_UPLOAD_COVERAGE_STATUS = "UploadCoverage_Status";
    public static final String KEY_USER_TYPE = "RIGHTNAME";
    public static final String KEY_SUPERVISOR_JCP_TYPE = "SUPERVISOR_JCP_TYPE";
    public static final String KEY_DATE = "date";
    public static final String KEY_PERFORMACE_TIME = "PERFORMANCE_TIME";

    public static final String KEY_P = "P";

    public static final String KEY_U = "U";


    public static final String KEY_INVALID = "INVALID";
    public static final String STORE_STATUS_LEAVE = "L";
    public static final String KEY_VALID = "Valid";

    public static final String SOAP_ACTION = "http://tempuri.org/";
    public static final String KEY_MERCHANDISER_ID = "MERCHANDISER_ID";


    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_FAILURE = "Failure";
    public static final String KEY_FALSE = "False";
    public static final String KEY_CHANGED = "Changed";

    public static final String KEY_IMAGE = "IMAGE1";
    public static final String KEY_IMAGE02 = "IMAGE2";
    public static final String KEY_COVERAGE_REMARK = "REMARK";
    public static final String METHOD_UPLOAD_IMAGE = "GetImageWithFolderName";
    public static final String SOAP_ACTION_UPLOAD_IMAGE = "http://tempuri.org/" + METHOD_UPLOAD_IMAGE;

    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String URL = "http://voto.parinaam.in/VOTOWebService.asmx";
    public static final String URLFORRETROFIT = "http://voto.parinaam.in/VOTOWebService.asmx/";

    public static final String URLBACKUPUPLOADRETROFIT = "http://voto.parinaam.in/VOTOWebService.asmx/";


    // public static final String METHOD_LOGIN = "UserLoginDetail";
    public static final String METHOD_LOGIN = "UserLoginDetailnew";
    public static final String SOAP_ACTION_LOGIN = "http://tempuri.org/" + METHOD_LOGIN;
    public static final String TABLE_COVERAGE_DATA = "COVERAGE_DATA";
    // FOR JCP DOWNLOAD
    public static final String KEY_ID = "_id";
    public static final String KEY_STORE_ID = "STORE_ID";
    public static final String KEY_STORE_NAME = "STORE_NAME";

    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_IN_TIME = "IN_TIME";
    public static final String KEY_OUT_TIME = "OUT_TIME";
    public static final String KEY_VISIT_DATE = "VISIT_DATE";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_COVERAGE_STATUS = "Coverage";
    public static final String KEY_REASON_ID = "REASON_ID";
    public static final String KEY_REASON = "REASON";
    public static final String KEY_STATUS = "STATUS";
    public static final String KEY_CHECKOUT_STATUS = "CHECKOUT_STATUS";
    public static final String KEY_STORE_CD = "STORE_CD";
    public static final String METHOD_NAME_UNIVERSAL_DOWNLOAD = "Download_Universal";
    public static final String SOAP_ACTION_UNIVERSAL = "http://tempuri.org/" + METHOD_NAME_UNIVERSAL_DOWNLOAD;

    public static final String CREATE_TABLE_COVERAGE_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_COVERAGE_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_ID
            + " VARCHAR,USER_ID VARCHAR, " + KEY_IN_TIME + " VARCHAR,"
            + KEY_OUT_TIME + " VARCHAR,"

            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_LATITUDE + " VARCHAR," + KEY_LONGITUDE + " VARCHAR,"
            + KEY_COVERAGE_STATUS + " VARCHAR," + KEY_IMAGE + " VARCHAR,"

            + KEY_IMAGE02 + " VARCHAR,"
            + KEY_EMP_CD + " INTEGER,"

            + KEY_REASON_ID + " VARCHAR," + KEY_COVERAGE_REMARK
            + " VARCHAR," + KEY_REASON + " VARCHAR)";


    public static final String TABLE_INSERT_SALE_ENTRY_DATA = "SALE_data";
    public static final String TABLE_INSERT_AUDIT_DATA = "AUDIT_DATA";
    public static final String CREATE_TABLE_INSERT_SALE_ENTRY_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_SALE_ENTRY_DATA
            + "("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "IMEI"
            + " VARCHAR,"
            + "MODEL_NO"
            + " VARCHAR,"
            + "MODEL"
            + " VARCHAR,"
            + "VISIT_DATE"
            + " VARCHAR,"
            + "STATUS"
            + " VARCHAR,"
            + "USER_ID"
            + " VARCHAR,"
            + "STORE_CD" + " INTEGER)";


    public static final String CREATE_TABLE_INSERT_AUDIT_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_AUDIT_DATA
            + "("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "QUESTION"
            + " VARCHAR,"
            + "QUESTION_CD"
            + " INTEGER,"
            + "ANSWER"
            + " VARCHAR,"
            + "ANSWER_CD"
            + " INTEGER,"
            + "VISIT_DATE"
            + " VARCHAR,"
            + "STATUS"
            + " VARCHAR,"
            + "USER_ID"
            + " VARCHAR,"
            + "STORE_CD" + " INTEGER)";


    public static final String TABLE_INSERT_STOCK_ENTRY_DATA = "STOCK_ENTRY_DATA";
    public static final String TABLE_INSERT_SUPERVISOR_ATTENDENCE_DATA = "SUPERVISOR_ATTENDENCE_DATA";

    public static final String CREATE_TABLE_INSERT_STOCK_ENTRY_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_STOCK_ENTRY_DATA
            + "("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "QUANTITY"
            + " INTEGER,"
            + "MODEL_CD"
            + " VARCHAR,"
            + "MODEL"
            + " VARCHAR,"
            + "VISIT_DATE"
            + " VARCHAR,"
            + "STATUS"
            + " VARCHAR,"
            + "USER_ID"
            + " VARCHAR,"
            + "STORE_CD" + " INTEGER)";


    public static final String CREATE_TABLE_SUPERVISOR_ATTENDENCE_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_SUPERVISOR_ATTENDENCE_DATA
            + "("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"

            + "ENTRY_ALLOW"
            + " INTEGER,"
            + "REASON_CD"
            + " INTEGER,"
            + "REASON"
            + " VARCHAR,"
            + "REMARK"
            + " VARCHAR,"

            + "IMAGE"
            + " VARCHAR,"

            + "VISIT_DATE"
            + " VARCHAR,"
            + "STATUS"
            + " VARCHAR,"
            + "USER_ID"
            + " VARCHAR)";



}
