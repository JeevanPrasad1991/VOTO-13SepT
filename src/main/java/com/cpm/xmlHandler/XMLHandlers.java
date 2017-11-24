package com.cpm.xmlHandler;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.cpm.xmlGetterSetter.AditGetterSetter;
import com.cpm.xmlGetterSetter.DocumentGetterSetter;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.JCPGetterSetter;
import com.cpm.xmlGetterSetter.JcpTypeGetterSetter;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.LoginGetterSetter;
import com.cpm.xmlGetterSetter.ModelGetterSetter;
import com.cpm.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.xmlGetterSetter.NonWrkingMasterGetterSetter;
import com.cpm.xmlGetterSetter.PerformanceGetterSetter;
import com.cpm.xmlGetterSetter.QuestionGetterSetter;
import com.cpm.xmlGetterSetter.SupTeamGetterSetter;


public class XMLHandlers {
    // LOGIN XML HANDLER
    public static LoginGetterSetter loginXMLHandler(XmlPullParser xpp,
                                                    int eventType) {
        LoginGetterSetter lgs = new LoginGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("RESULT")) {
                        lgs.setResult(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_VERSION")) {
                        lgs.setVERSION(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_PATH")) {
                        lgs.setPATH(xpp.nextText());
                    }
                    if (xpp.getName().equals("CURRENTDATE")) {
                        lgs.setDATE(xpp.nextText());
                    }

                    if (xpp.getName().equals("RIGHTNAME")) {
                        lgs.setRIGHTNAME(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lgs;
    }


    public static DocumentGetterSetter DocumentHandler(XmlPullParser xpp,
                                                       int eventType) {
        DocumentGetterSetter document = new DocumentGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        document.setTable_HR_DOCUMENTS(xpp.nextText());
                    }

                    if (xpp.getName().equals("document_id")) {
                        document.setDocument_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("document_name")) {
                        document.setDocument_name(xpp.nextText());
                    }
                    if (xpp.getName().equals("document_descriiption")) {
                        document.setDocument_descriiption(xpp.nextText());
                    }
                    if (xpp.getName().equals("document_url")) {
                        document.setDocument_url(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return document;
    }

    // FAILURE XML HANDLER
    public static FailureGetterSetter failureXMLHandler(XmlPullParser xpp,
                                                        int eventType) {
        FailureGetterSetter failureGetterSetter = new FailureGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("STATUS")) {
                        failureGetterSetter.setStatus(xpp.nextText());
                    }
                    if (xpp.getName().equals("ERRORMSG")) {
                        failureGetterSetter.setErrorMsg(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return failureGetterSetter;
    }

    // JCP XML HANDLER
    public static JourneyPlanGetterSetter JCPXMLHandler(XmlPullParser xpp, int eventType) {
        JourneyPlanGetterSetter jcpGetterSetter = new JourneyPlanGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        jcpGetterSetter.setTable_journey_plan(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE_CD")) {
                        jcpGetterSetter.setStore_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("EMP_CD")) {
                        jcpGetterSetter.setEmp_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("VISIT_DATE")) {
                        jcpGetterSetter.setVISIT_DATE(xpp.nextText());
                    }
                    if (xpp.getName().equals("KEYACCOUNT")) {
                        jcpGetterSetter.setKey_account(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORENAME")) {
                        jcpGetterSetter.setStore_name(xpp.nextText());
                    }
                    if (xpp.getName().equals("CITY")) {
                        jcpGetterSetter.setCity(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORETYPE")) {
                        jcpGetterSetter.setStore_type(xpp.nextText());
                    }
                    if (xpp.getName().equals("UPLOAD_STATUS")) {
                        jcpGetterSetter.setUploadStatus(xpp.nextText());
                    }
                    if (xpp.getName().equals("CHECKOUT_STATUS")) {
                        jcpGetterSetter.setCheckOutStatus(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jcpGetterSetter;
    }


    public static SupTeamGetterSetter SupTeamXML(XmlPullParser xpp,
                                                 int eventType) {
        SupTeamGetterSetter nonworking = new SupTeamGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setSupteamTable(xpp.nextText());
                    }
                    if (xpp.getName().equals("EMP_CD")) {
                        nonworking.setEmp_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("ISD")) {
                        nonworking.setEmp(xpp.nextText());
                    }
                    if (xpp.getName().equals("DESIGNATION")) {
                        nonworking.setDesignation(xpp.nextText());
                    }

                    if (xpp.getName().equals("USERNAME")) {
                        nonworking.setUserN(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }


    public static ModelGetterSetter ModelXML(XmlPullParser xpp,
                                             int eventType) {
        ModelGetterSetter nonworking = new ModelGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setModeltable(xpp.nextText());
                    }
                    if (xpp.getName().equals("MODEL_CD")) {
                        nonworking.setModel_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("MODEL")) {
                        nonworking.setModel(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }


    public static PerformanceGetterSetter PerformanceDataXML(XmlPullParser xpp,
                                                             int eventType) {
        PerformanceGetterSetter nonworking = new PerformanceGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setPerformanceTable(xpp.nextText());
                    }
                    if (xpp.getName().equals("MY_TARGET")) {
                        nonworking.setTarget(xpp.nextText());
                    }
                    if (xpp.getName().equals("MTD_SALE")) {
                        nonworking.setMtdSale(xpp.nextText());
                    }
                    if (xpp.getName().equals("TODAYS_SALE")) {
                        nonworking.setTodaySale(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }


    public static NonWorkingReasonGetterSetter nonWorkinReasonXML(XmlPullParser xpp,
                                                                  int eventType) {
        NonWorkingReasonGetterSetter nonworking = new NonWorkingReasonGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setNonworking_table(xpp.nextText());
                    }
                    if (xpp.getName().equals("REASON_CD")) {
                        nonworking.setReason_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("REASON")) {
                        nonworking.setReason(xpp.nextText());
                    }
                    if (xpp.getName().equals("ENTRY_ALLOW")) {
                        nonworking.setEntry_allow(xpp.nextText());
                    }
                    if (xpp.getName().equals("IMAGE_ALLOW")) {
                        nonworking.setImage_allow(xpp.nextText());
                    }
                    if (xpp.getName().equals("FOR_ATT")) {
                        nonworking.setFor_attendence(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }


    public static AditGetterSetter auditQuestionXML(XmlPullParser xpp,
                                                    int eventType) {
        AditGetterSetter nonworking = new AditGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setAuditTable(xpp.nextText());
                    }
                    if (xpp.getName().equals("QUESTION_ID")) {
                        nonworking.setQuest_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("QUESTION")) {
                        nonworking.setQuest(xpp.nextText());
                    }
                    if (xpp.getName().equals("QUESTION_TYPE")) {
                        nonworking.setQuest_type(xpp.nextText());
                    }

                    if (xpp.getName().equals("ANSWER_ID")) {
                        nonworking.setAns_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("ANSWER")) {
                        nonworking.setAns(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }

    public static JcpTypeGetterSetter JcpTypeXMLHandler(XmlPullParser xpp, int eventType) {
        JcpTypeGetterSetter qnsGetterSetter = new JcpTypeGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        qnsGetterSetter.setTable(xpp.nextText());
                    }

                    if (xpp.getName().equals("JCP_TYPE")) {
                        qnsGetterSetter.setJcp_type(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return qnsGetterSetter;
    }



    //Question Data

    public static QuestionGetterSetter QuestionXMLHandler(XmlPullParser xpp, int eventType) {
        QuestionGetterSetter qnsGetterSetter = new QuestionGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        qnsGetterSetter.setTable_question_today(xpp.nextText());
                    }

                    if (xpp.getName().equals("QUESTION_ID")) {
                        qnsGetterSetter.setQuestion_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("QUESTION")) {
                        qnsGetterSetter.setQuestion(xpp.nextText());
                    }
                    if (xpp.getName().equals("ANSWER_ID")) {
                        qnsGetterSetter.setAnswer_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("ANSWER")) {
                        qnsGetterSetter.setAnswer(xpp.nextText());
                    }
                    if (xpp.getName().equals("RIGHT_ANSWER")) {
                        qnsGetterSetter.setRight_answer(xpp.nextText());
                    }
                    if (xpp.getName().equals("STATUS")) {
                        qnsGetterSetter.setStatus(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return qnsGetterSetter;
    }

}
