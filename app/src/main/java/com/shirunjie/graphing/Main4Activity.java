package com.shirunjie.graphing;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Main4Activity extends Activity {

    private boolean needSaveImage = false;
    private PerimetryDataView dataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        dataView = (PerimetryDataView) findViewById(R.id.data_view);
        PerimetryData entries = new PerimetryData(new ArrayList<Entry>());
//        for (int i = -27; i <= 27; i += 6) {
//            for (int j = -27; j <= 27; j += 6) {
//                //                if (Math.pow(i, 2) + Math.pow(j, 2) < 750) {
////                entries.add(new Entry(i, j, String.format("(%d,%d)", i, j)));
//                entries.add(new Entry(i, j, (i+20)));
//                //                }
//            }
//        }
//        entries.add(new Entry(0, 0, "34"));
//        entries.add(new Entry(-10, 10, "34"));


        entries.add(new Entry(-27, -3, 32));
        entries.add(new Entry(-27, 3, 32));

        entries.add(new Entry(-21, -9, 30));
        entries.add(new Entry(-21, -3, 31));
        entries.add(new Entry(-21, 3, 33));
        entries.add(new Entry(-21, 9, 33));

        entries.add(new Entry(-15, -15, 28));
        entries.add(new Entry(-15, -9, 32));
        entries.add(new Entry(-15, -3, 33));
        entries.add(new Entry(-15, 3, 34));
        entries.add(new Entry(-15, 9, 32));
        entries.add(new Entry(-15, 15, 30));

        entries.add(new Entry(-9, -21, 15));
        entries.add(new Entry(-9, -15, 28));
        entries.add(new Entry(-9, -9, 32));
        entries.add(new Entry(-9, -3, 33));
        entries.add(new Entry(-9, 3, 33));
        entries.add(new Entry(-9, 9, 32));
        entries.add(new Entry(-9, 15, 29));
        entries.add(new Entry(-9, 21, 27));

        entries.add(new Entry(-3, -21, 11));
        entries.add(new Entry(-3, -15, 29));
        entries.add(new Entry(-3, -9, 32));
        entries.add(new Entry(-3, -3, 34));
        entries.add(new Entry(-3, 3, 35));
        entries.add(new Entry(-3, 9, 32));
        entries.add(new Entry(-3, 15, 29));
        entries.add(new Entry(-3, 21, 29));

        entries.add(new Entry(3, -21, 26));
        entries.add(new Entry(3, -15, 28));
        entries.add(new Entry(3, -9, 31));
        entries.add(new Entry(3, -3, 31));
        entries.add(new Entry(3, 3, 34));
        entries.add(new Entry(3, 9, 32));
        entries.add(new Entry(3, 15, 31));
        entries.add(new Entry(3, 21, 28));

        entries.add(new Entry(9, -21, 18));
        entries.add(new Entry(9, -15, 26));
        entries.add(new Entry(9, -9, 33));
        entries.add(new Entry(9, -3, 34));
        entries.add(new Entry(9, 3, 32));
        entries.add(new Entry(9, 9, 32));
        entries.add(new Entry(9, 15, 30));
        entries.add(new Entry(9, 21, 28));

        entries.add(new Entry(15, -15, 26));
        entries.add(new Entry(15, -9, 27));
        entries.add(new Entry(15, -3, 6));
        entries.add(new Entry(15, 3, 1));
        entries.add(new Entry(15, 9, 32));
        entries.add(new Entry(15, 15, 30));

        entries.add(new Entry(21, -9, 0));
        entries.add(new Entry(21, -3, 19));
        entries.add(new Entry(21, 3, 32));
        entries.add(new Entry(21, 9, 31));

//        entries.add(new Entry(13, -3, 28));
//        entries.add(new Entry(15, -3, 0));
//        entries.add(new Entry(17, -3, 30));
//        entries.add(new Entry(19, -3, 32));
//        entries.add(new Entry(21, -3, 34));

        dataView.setBackgroundColor(0xFFFFFFFF);
        dataView.setData(entries);
        dataView.setDrawingCacheEnabled(true);
        //        dataView.invalidate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            needSaveImage = true;
        } else {
            saveImage();
        }

    }

    private void saveImage() {
        dataView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dataView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                needSaveImage = false;
                final AsyncTask<Bitmap, Void, String> saveImageAsyncTask = new AsyncTask<Bitmap, Void, String>() {
                    @Override
                    protected String doInBackground(Bitmap... params) {
                        if (params != null) {
                            Bitmap bitmap = params[0];
                            if (bitmap != null) {

                                bitmap = cropBitmap(bitmap);

                                //                                File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                String sdCard = Environment.getExternalStorageDirectory() + "/Document/html/images/";
                                File   file   = new File(sdCard, "image.png");
                                file.getParentFile().mkdirs();
                                try {
                                    FileOutputStream fos = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                    fos.close();
                                    return file.toString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String string) {
                        super.onPostExecute(string);
                        if (string != null) {
                            Toast.makeText(Main4Activity.this, "Saved image to " + string, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Main4Activity.this, "Save image failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                };
                saveImageAsyncTask.execute(dataView.getDrawingCache());
            }
        });
    }

    private static Bitmap cropBitmap(Bitmap bitmap) {
        return BitmapHelper.cropBorder(bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needSaveImage) {
            needSaveImage = false;
            saveImage();
        }
    }
}
