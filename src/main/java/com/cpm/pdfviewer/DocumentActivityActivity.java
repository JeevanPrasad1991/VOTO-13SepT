package com.cpm.pdfviewer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKDatabase;
import com.cpm.download.DownloadHrDocumentActivty;
import com.cpm.voto.R;
import com.cpm.xmlGetterSetter.DocumentGetterSetter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class DocumentActivityActivity extends AppCompatActivity {
    GSKDatabase db;
    RecyclerView rec;
    TextView nodata_txt;
    ArrayList<DocumentGetterSetter> document_list;
    MyRecyclerAdapter adapter;
    String Path = CommonString.FOLDER_PATH;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Documents");
        db = new GSKDatabase(getApplicationContext());
        db.open();
        rec = (RecyclerView) findViewById(R.id.rec);
        nodata_txt = (TextView) findViewById(R.id.nodata_txt);
        isStoragePermissionGranted();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        this.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        document_list = db.getDocumentData();
        if (document_list.size() > 0) {
            adapter = new MyRecyclerAdapter(getApplicationContext(), document_list);
            rec.setAdapter(adapter);
            rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            nodata_txt.setVisibility(View.GONE);
            rec.setVisibility(View.VISIBLE);
        }
    }

    private static final String TAG = DocumentActivityActivity.class.getSimpleName();

    class MyRecyclerAdapter extends RecyclerView.Adapter<DocumentActivityActivity.MyRecyclerAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<DocumentGetterSetter> data = Collections.emptyList();

        public MyRecyclerAdapter(Context context, List<DocumentGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.listview_item, parent, false);

            MyRecyclerAdapter.MyViewHolder holder = new MyRecyclerAdapter.MyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final DocumentGetterSetter current = data.get(position);
            final String name = current.getDocument_name().get(0);
            final String pdfurl = current.getDocument_url().get(0);
            holder.name.setText(name);
            holder.detail.setText(current.getDocument_descriiption().get(0));
            holder.parent_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final File dir = new File(CommonString.HRDOCUMENTS_PATH);
                    ArrayList<String> list = new ArrayList();
                    list = getFileNames(dir.listFiles());
                    if (list.size() == 0) {
                        if (checkNetIsAvailable()){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //new change by jeevan rana
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("path-to-document"));
                                        intent.setType("application/pdf");
                                        PackageManager pm = getPackageManager();
                                        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
                                        if (activities.size() >0) {
                                            downloadFile(pdfurl, name + ".pdf", dir);
                                            startActivity(intent);
                                        } else {
                                            Intent web = new Intent(getApplicationContext(), PdfOpnerWebActivity.class);
                                            web.putExtra(CommonString.KEY_NAME, name);
                                            web.putExtra(CommonString.PDF_URL, current.getDocument_url().get(0));
                                            startActivity(web);

                                            // Do something else here. Maybe pop up a Dialog or Toast
                                        }

                                        // Your implementation goes here
                                   /* Intent in = new Intent(getApplicationContext(), PDFActivity.class);
                                    in.putExtra(CommonString.KEY_NAME, name);
                                    in.putExtra(CommonString.KEY_PATH, Path + name + ".pdf");
                                    startActivity(in);*/
                                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }).start();

                        }else {
                            Snackbar.make(rec,"Check internet connection",Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        //new change by jeevan rana
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("path-to-document"));
                        intent.setType("application/pdf");
                        PackageManager pm = getPackageManager();
                        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
                        if (activities.size() > 0) {
                            startActivity(intent);
                        } else {
                            Intent web = new Intent(getApplicationContext(), PdfOpnerWebActivity.class);
                            web.putExtra(CommonString.KEY_NAME, name);
                            web.putExtra(CommonString.PDF_URL, current.getDocument_url().get(0));
                            startActivity(web);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            // Do something else here. Maybe pop up a Dialog or Toast
                        }
                       /* Intent in = new Intent(getApplicationContext(), PDFActivity.class);
                        in.putExtra(CommonString.KEY_NAME, name);
                        in.putExtra(CommonString.KEY_PATH, Path + name + ".pdf");
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);*/
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return document_list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView name, detail;
            LinearLayout parent_layout;

            public MyViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.tv_name);
                detail = (TextView) itemView.findViewById(R.id.tv_details);
                parent_layout = (LinearLayout) itemView.findViewById(R.id.layout_parent);

            }
        }
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Log.v(DownloadActivity.class.getSimpleName(),"Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }


    public void downloadFile(final String fileUrl, final String directory, final File folder_path) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.getResponseCode();
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                int length = urlConnection.getContentLength();
                String size = new DecimalFormat("##.##").format((double) ((double) length / 1024)) + " KB";
                if (!new File(folder_path.getPath() + directory).exists() && !size.equalsIgnoreCase("0 KB")) {
                    File outputFile = new File(folder_path, directory);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    InputStream is1 = (InputStream) urlConnection.getInputStream();
                    int bytes = 0;
                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    while ((len1 = is1.read(buffer)) != -1) {
                        bytes = (bytes + len1);
                        fos.write(buffer, 0, len1);

                    }
                    fos.close();
                    is1.close();

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
