package com.cpm.voto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.dailyentry.DailyEntryScreen;
import com.cpm.dailyentry.PerformanceActivty;
import com.cpm.dailyentry.SupervisorDailyEntry;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.download.CompleteDownloadActivity;
import com.cpm.download.DownloadPerformanceActivity;
import com.cpm.fragment.HelpFragment;
import com.cpm.fragment.MainFragment;
import com.cpm.message.AlertMessage;
import com.cpm.pdfviewer.DocumentActivityActivity;
import com.cpm.retrofit.PostApi;
import com.cpm.retrofit.PostApiForFile;
import com.cpm.retrofit.RetrofitClass;
import com.cpm.retrofit.StringConverterFactory;
import com.cpm.upload.CheckoutNUpload;
import com.cpm.upload.UploadDataActivity;
import com.cpm.upload.UploadOptionActivity;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.SupTeamGetterSetter;
import com.cpm.xmlGetterSetter.SupervisorAttendenceGetterSetter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.cpm.compressimage.Utilities.saveBitmapToFile;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    GSKDatabase database;
    ArrayList<JourneyPlanGetterSetter> jcplist;
    ArrayList<SupTeamGetterSetter> teamsuplist = new ArrayList<>();
    private SharedPreferences preferences = null;
    private String visit_date, user_name, user_type, SUPERVISOR_JCP_TYPE;
    TextView tv_username, tv_usertype;
    SupervisorAttendenceGetterSetter supervisorAttendenceGetterSetter;
    FrameLayout frameLayout;
    NavigationView navigationView;
    String result = "";
    boolean isvalid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = preferences.getString(CommonString.KEY_USERNAME, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main_menu2, navigationView, false);
        navigationView.addHeaderView(headerView);
        tv_username = (TextView) headerView.findViewById(R.id.nav_user_name);
        tv_usertype = (TextView) headerView.findViewById(R.id.nav_user_type);
        tv_username.setText(user_name);
        tv_usertype.setText(user_type);
        navigationView.setNavigationItemSelectedListener(this);
        database = new GSKDatabase(this);
        database.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        SUPERVISOR_JCP_TYPE = preferences.getString(CommonString.KEY_SUPERVISOR_JCP_TYPE, null);
        getSupportActionBar().setTitle("Main Menu -" + visit_date);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_daily) {
            // Handle the camera action
            if (!user_type.equalsIgnoreCase("ISD")) {
                supervisorAttendenceGetterSetter = database.getsupervisorAttendenceData(visit_date);
                if (supervisorAttendenceGetterSetter.getEntry_allow() != null && supervisorAttendenceGetterSetter.getEntry_allow().equals("0")) {
                    Snackbar.make(frameLayout, "You have not selected Present OR Meeting . So you can not work in this store. ",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else if (supervisorAttendenceGetterSetter.getStatus() != null && supervisorAttendenceGetterSetter.getStatus().equalsIgnoreCase("D")
                        || supervisorAttendenceGetterSetter.getStatus() != null && supervisorAttendenceGetterSetter.getStatus().equalsIgnoreCase("U")) {
                    Intent startDownload = new Intent(this, SupervisorDailyEntry.class);
                    startActivity(startDownload);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                } else {
                    Intent startDownload = new Intent(this, SupervisorAttendenceActivity.class);
                    startActivity(startDownload);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                }
            } else {
                Intent startDownload = new Intent(this, DailyEntryScreen.class);
                startActivity(startDownload);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
        } else if (id == R.id.nav_download) {
            if (checkNetIsAvailable()) {
                if (database.isCoverageDataFilled(visit_date)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Parinaam");
                    builder.setMessage("Please Upload Previous Data First")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent startUpload = new Intent(MainMenuActivity.this, CheckoutNUpload.class);
                                    startActivity(startUpload);
                                    finish();

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    try {
                        database.open();
                        database.deletePreviousUploadedData(visit_date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent startDownload = new Intent(getApplicationContext(), CompleteDownloadActivity.class);
                    startActivity(startDownload);
                    finish();
                }
            } else {
                Snackbar.make(frameLayout, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

            }

        } else if (id == R.id.nav_upload) {
            if (checkNetIsAvailable()) {
                jcplist = database.getJCPData(visit_date);
                boolean flag = true;
                if (jcplist.size() == 0) {
                    Snackbar.make(frameLayout, "Please Download Data First", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    ArrayList<CoverageBean> cdata = new ArrayList<CoverageBean>();
                    cdata = database.getCoverageData(visit_date);
                    if (cdata.size() == 0) {
                        Snackbar.make(frameLayout, AlertMessage.MESSAGE_NO_DATA, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                    if (cdata.size() > 0) {
                        for (int i = 0; i < cdata.size(); i++) {
                            if (cdata.get(i).getOutTime().equals("")) {
                                flag = false;
                                Snackbar.make(frameLayout, "Please Checkout current store", Snackbar.LENGTH_LONG).show();
                                break;
                            }
                        }
                        if (flag) {
                            Intent i = new Intent(getBaseContext(), UploadDataActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Snackbar.make(frameLayout, "No data for upload!", Snackbar.LENGTH_LONG).show();

                    }
                }
            } else {
                Snackbar.make(frameLayout, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        } else if (id == R.id.nav_performance) {
            jcplist = database.getJCPData(visit_date);
            teamsuplist = database.getISDlist();
            if (!user_type.equalsIgnoreCase("ISD")) {
                if (SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
                    if (jcplist.size() == 0) {
                        Snackbar.make(frameLayout, "Please Download Data First", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    } else {
                        if (checkNetIsAvailable()) {
                            startActivity(new Intent(this, DownloadPerformanceActivity.class));
                        } else {
                            Intent performance = new Intent(this, PerformanceActivty.class);
                            startActivity(performance);
                        }
                    }

                } else {
                    if (jcplist.size() == 0 && teamsuplist.size() == 0) {
                        Snackbar.make(frameLayout, "Please Download Data First", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    } else {
                        if (checkNetIsAvailable()) {
                            startActivity(new Intent(this, DownloadPerformanceActivity.class));
                        } else {
                            Intent performance = new Intent(this, PerformanceActivty.class);
                            startActivity(performance);
                        }
                    }
                }
            } else {
                if (jcplist.size() == 0) {
                    Snackbar.make(frameLayout, "Please Download Data First", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    if (checkNetIsAvailable()) {
                        startActivity(new Intent(this, DownloadPerformanceActivity.class));
                    } else {
                        Intent performance = new Intent(this, PerformanceActivty.class);
                        startActivity(performance);
                    }
                }
            }

        } else if (id == R.id.nav_export) {
            if (checkNetIsAvailable()) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainMenuActivity.this);
                builder1.setMessage("Are you sure you want to take the backup of your data")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @SuppressWarnings("resource")
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    File file = new File(Environment.getExternalStorageDirectory(), "Voto_backup");
                                    if (!file.isDirectory()) {
                                        file.mkdir();
                                    }
                                    File sd = Environment.getExternalStorageDirectory();
                                    File data = Environment.getDataDirectory();
                                    if (sd.canWrite()) {
                                        String currentDBPath = "//data//com.cpm.voto//databases//" + GSKDatabase.DATABASE_NAME;
                                        String backupDBPath = user_name + "_Voto_backup_" +
                                                visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".db";
                                        File currentDB = new File(data, currentDBPath);
                                        File backupDB = new File("/mnt/sdcard/Voto_backup/", backupDBPath);
                                        if (currentDB.exists()) {
                                            FileChannel src = new FileInputStream(currentDB).getChannel();
                                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                            dst.transferFrom(src, 0, src.size());
                                            src.close();
                                            dst.close();
                                        }
                                    }
                                    File dir = new File(CommonString.BACKUP_PATH);
                                    ArrayList<String> list = new ArrayList();
                                    list = getFileNames(dir.listFiles());

                                    if (list.size() > 0) {
                                        for (int i1 = 0; i1 < list.size(); i1++) {
                                            if (list.get(i1).contains("Voto_backup")) {
                                                File originalFile = new File(CommonString.BACKUP_PATH + list.get(i1));
                                                Object result = uploadBackup(MainMenuActivity.this, originalFile.getName(), "DBBackup");
                                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

                                                }
                                            }
                                        }
                                    }

                                    Snackbar.make(frameLayout, "Database Exported And Uploaded Successfully", Snackbar.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert1 = builder1.create();
                alert1.show();
            } else {
                Snackbar.make(frameLayout, "Check internet connection!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

            }

        } else if (id == R.id.nav_pdfdocument) {
            jcplist = database.getJCPData(visit_date);
            teamsuplist = database.getISDlist();
            if (jcplist.size() > 0 || teamsuplist.size() > 0) {
                startActivity(new Intent(this, DocumentActivityActivity.class));
            } else {
                Snackbar.make(frameLayout, "Please Download Data First", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        } else if (id == R.id.nav_exit) {
            Intent startDownload = new Intent(this, LoginActivity.class);
            startActivity(startDownload);
            finish();
        } else if (id == R.id.nav_help) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            HelpFragment cartfrag = new HelpFragment();
            fragmentManager.beginTransaction().replace(R.id.frame_layout, cartfrag).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragment cartfrag = new MainFragment();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, cartfrag).commit();
    }

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }

    private String uploadBackup(final Context context, String file_name, String folder_name) {
        RequestBody body1;
        result = "";
        isvalid = false;
        final File originalFile = new File(CommonString.BACKUP_PATH + file_name);
        RequestBody photo = RequestBody.create(MediaType.parse("application/octet-stream"), originalFile);
        body1 = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", originalFile.getName(), photo)
                .addFormDataPart("FolderName", folder_name)
                .build();
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(CommonString.URLBACKUPUPLOADRETROFIT)
                .addConverterFactory(new StringConverterFactory())
                .build();
        PostApiForFile api = adapter.create(PostApiForFile.class);
        Call<String> call = api.getUploadImage(body1);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response) {
                if (response.toString() != null) {
                    if (response.body().contains(CommonString.KEY_SUCCESS)) {
                        isvalid = true;
                        result = CommonString.KEY_SUCCESS;
                        originalFile.delete();
                    } else {
                        result = "Servererror!";
                    }
                } else {
                    result = "Servererror!";
                }
            }

            @Override
            public void onFailure(Throwable t) {
                isvalid = true;
                if (t instanceof UnknownHostException) {
                    result = AlertMessage.MESSAGE_SOCKETEXCEPTION;
                } else {
                    result = AlertMessage.MESSAGE_SOCKETEXCEPTION;
                }
                Toast.makeText(context, originalFile.getName() + " not uploaded", Toast.LENGTH_SHORT).show();
            }
        });

        return result;
    }
}
