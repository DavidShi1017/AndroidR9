/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nmbs.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.nmbs.R;
import com.nmbs.log.LogUtils;
import com.nmbs.util.ActivityConstant;
import com.nmbs.util.Utils;


import java.io.File;
import java.io.InputStream;
import java.util.List;


public class PDFViewActivity extends Activity implements OnPageChangeListener, OnLoadCompleteListener, OnErrorListener {

    private static final String TAG = PDFViewActivity.class.getSimpleName();

    private final static int REQUEST_CODE = 42;
    public static final int PERMISSION_CODE = 200;

    //public static final String SAMPLE_FILE = "sample.pdf";
    private static final String SAMPLE_FILE = "BEBMI_DE.pdf";
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String MOUNT_UNMOUNT_FILESYSTEMS = "android.permission.MOUNT_UNMOUNT_FILESYSTEMS";
    PDFView pdfView;
    String assetFileName;
    Uri uri;
    Integer pageNumber = 0;
    String pdfFileName;
    File file;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        final int SCREEN_ORIENTATION = getIntent().getIntExtra(ActivityConstant.SCREEN_ORIENTATION, 0);
        setRequestedOrientation(SCREEN_ORIENTATION);
        setContentView(R.layout.activity_pdf_view);
        ActivityCompat.requestPermissions(
                this,
                new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, MOUNT_UNMOUNT_FILESYSTEMS},
                PERMISSION_CODE
        );
        pdfView = (PDFView) findViewById(R.id.pdfView);
        file = (File)getIntent().getSerializableExtra(ActivityConstant.LOCAL_PDF_URL);
        assetFileName = getIntent().getStringExtra(ActivityConstant.ASSET_FILENAME);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(file == null){
            displayFromAsset(assetFileName);
        }else {
            displayFromFile(file);
        }

    }

    void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            //alert user that file manager not working
            //Toast.makeText(this, R.string.toast_pick_file_error, Toast.LENGTH_SHORT).show();
        }
    }


/*    @AfterViews
    void afterViews() {
        *//*int permissionCheck = ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);*//*

        //if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, MOUNT_UNMOUNT_FILESYSTEMS},
                    PERMISSION_CODE
            );
        //}
        requestWindowFeature(Window.FEATURE_NO_TITLE);
            *//*if (uri != null) {
            displayFromUri(uri);
        } else {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + SAMPLE_FILE);
            displayFromFile(file);
            //displayFromAsset(SAMPLE_FILE);
        }*//*
        setTitle(pdfFileName);
    }*/

    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;
        pdfView.fromAsset(pdfFileName)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
        LogUtils.e("PDFViewActivity", "Open pdf by assetFileName---->" + assetFileName);
    }

    private void displayFromStream(InputStream inputStream) {
        //pdfFileName = getFileName(uri);
        pdfView.fromStream(inputStream)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    private void displayFromFile(File file) {
        pdfFileName = file.getName();
        LogUtils.d("openPDF", "openPDF===" + pdfFileName);
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .onError(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();

    }

/*    @OnActivityResult(REQUEST_CODE)
    public void onResult(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            uri = intent.getData();
            displayFromUri(uri);
        }
    }*/

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void loadComplete(int nbPages) {
       /* PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");*/

    }

/*    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }*/

    /**
     * Listener for response to user permission request
     *
     * @param requestCode  Check that permission request code matches
     * @param permissions  Permissions that requested
     * @param grantResults Whether permissions granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //launchPicker();
                //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + SAMPLE_FILE);
                if(file == null){
                    displayFromAsset(assetFileName);
                }else {
                    displayFromFile(file);
                }
            }
        }
    }

    public static Intent createIntent(Context context, File file, String assetFileName, int screenOrientation){
        Intent intent = new Intent(context, PDFViewActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ActivityConstant.LOCAL_PDF_URL, file);
        intent.putExtra(ActivityConstant.SCREEN_ORIENTATION, screenOrientation);
        intent.putExtra(ActivityConstant.ASSET_FILENAME, assetFileName);
        return intent;
    }

    @Override
    public void onError(Throwable t) {
        if(assetFileName != null && !assetFileName.isEmpty()){
            displayFromAsset(assetFileName);
        }
    }
}
