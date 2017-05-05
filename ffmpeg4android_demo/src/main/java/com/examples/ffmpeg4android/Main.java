package com.examples.ffmpeg4android;


import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.examples.ffmpeg4android_demo.R;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.loader.LoadJNI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class Main extends Activity {
/*
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(Prefs.TAG, "Main on resume handling log copy in case of a crash");
        String demoVideoFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videokit/";
        String workFolder = getApplicationContext().getFilesDir() + "/";
        String vkLogPath = workFolder + "vk.log";
        GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        GeneralUtils.checkForPermissionsMAndAbove(Main.this, false);

        Button simpleAct = (Button) findViewById(R.id.startSimpleAct);

        File file = new File( Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG20161220140926.jpg");
        if (file != null && file.exists()) {
            System.out.println(" file exists ");
        } else {
            System.out.println(" file does not exists ");
        }

        LoadJNI vk = new LoadJNI();
        try {
            String workFolder = getApplicationContext().getFilesDir().getAbsolutePath();
            //	String[] complexCommand ={ffmpeg -i birds.mp4 -i watermark2.png -filter_complex "pad=height=ih+80:width=iw+80:x=40:y=40:color=violet" birds5.mp4};

         //   String[] complexCommand = {"ffmpeg", "-i", Environment.getExternalStorageDirectory() + "/videokit/out1.mp4", "-i", Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG20161220140926.jpg", "-filter_complex", Environment.getExternalStorageDirectory() + "/videokit/out3.mp4"};
            //	String[] complexCommand = {"ffmpeg","-i", "/Phone/videokit/in.mp4"};
            	String[] complexCommand = {"ffmpeg","-y" ,"-i",Environment.getExternalStorageDirectory() + "/videokit/out1.mp4","-strict","experimental", "-vf", "movie="+Environment.getExternalStorageDirectory() + "/videokit/watermark.png"+" [watermark]; [in][watermark] overlay=main_w-overlay_w-10:10 [out]","-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050", Environment.getExternalStorageDirectory() + "/videokit/out2.mp4"};
            //	String[] complexCommand = {	"ffmpeg", "-i", "/sdcard/videokit/in.mp4", "-i",  "/sdcard/DCIM/Camera/IMG201612201140926.jpg", "-filter_complex", "overlay=10:10", "/sdcard/videokit/out6.mp4"};
            vk.run(complexCommand, workFolder, getApplicationContext());
            Log.i("test", "ffmpeg4android finished successfully");
        } catch (Throwable e) {
            Log.e("test", "vk run exception.", e);
        }
*/

    private Uri fileUri;
    File targetFile;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static Main ActivityContext = null;
    public static TextView output;
    int BUFFER_LEN = 1024;
    String inputvideo, outputvideo;
    private static File mediaStorageDir;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mediaStorageDir = new File(getFilesDir(), "MyCameraVideo");
        ActivityContext = this;

        //saving watermark image to memory of device
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("images");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        for (String s : files) {
            System.out.println(" the file name is " + files);
        }


        InputStream is;
        try {
            is = assetManager.open("images/watermark.png");
            //  is = assetManager.open("watermark.png");
            targetFile = new File(Environment.getExternalStorageDirectory() + "/videokit/watermark.png");
            final byte[] buf = new byte[1024]; // 32k
//            OutputStream outStream = new FileOutputStream(targetFile);
//            byte[] buffer = new byte[is.available()];
//            outStream.write(buffer);

            OutputStream os = new FileOutputStream(targetFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            //read from is to buffer
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            //flush OutputStream to write any buffered data to file
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Button buttonRecording = (Button) findViewById(R.id.recording);
        output = (TextView) findViewById(R.id.output);

        buttonRecording.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // create new Intentwith with Standard Intent action that can be
                // sent to have the camera application capture an video and return it.
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                // create a file to save the video
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

                // set the image file name
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                // set the video image quality to high
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                // start the Video Capture Intent
                startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

            }
        });


    }

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {

        // Check that the SDCard is mounted
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MyCameraVideo");




        // Create the storage directory(MyCameraVideo) if it does not exist
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {

                output.setText("Failed to create directory MyCameraVideo.");

                Toast.makeText(ActivityContext, "Failed to create directory MyCameraVideo.",
                        Toast.LENGTH_LONG).show();

                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }


        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date = new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());

        File mediaFile;

        if (type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(Environment.getExternalStorageDirectory() + "/videokit/newvideo.mp4");


        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // After camera screen this code will excuted
        inputvideo = Environment.getExternalStorageDirectory() + "/videokit/newvideo.mp4";
        outputvideo = Environment.getExternalStorageDirectory() + "/videokit/newvideo3.mp4";


        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                output.setText("Video File : " + data.getData());

                LoadJNI vk = new LoadJNI();
                try {
                    String workFolder = getApplicationContext().getFilesDir().getAbsolutePath();
                    //	String[] complexCommand ={ffmpeg -i birds.mp4 -i watermark2.png -filter_complex "pad=height=ih+80:width=iw+80:x=40:y=40:color=violet" birds5.mp4};

                    //   String[] complexCommand = {"ffmpeg", "-i", Environment.getExternalStorageDirectory() + "/videokit/out1.mp4", "-i", Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG20161220140926.jpg", "-filter_complex", Environment.getExternalStorageDirectory() + "/videokit/out3.mp4"};
                    //	String[] complexCommand = {"ffmpeg","-i", "/Phone/videokit/in.mp4"};

                    // String[] complexCommand = {"ffmpeg","-y" ,"-i",Environment.getExternalStorageDirectory() + "/videokit/newvideo.mp4","-strict","experimental", "-vf", "movie="+Environment.getExternalStorageDirectory() + "/videokit/watermark.png"+" [watermark]; [in][watermark] overlay=10:main_h-overlay_h-10 [out]","-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050", Environment.getExternalStorageDirectory() + "/videokit/newvideo1.mp4"};
                    System.out.print("MY VIDEO LINK : " + targetFile);
                    String[] complexCommand = {"ffmpeg", "-y", "-i", inputvideo, "-i", targetFile.getAbsolutePath().toString(), "-filter_complex", "overlay=main_w-overlay_w-10:main_h-overlay_h-10", "-b", "920k", "-codec:a", "copy", outputvideo};
                    //
                    //
                    //    String[] complexCommand={"ffmpeg","-i", Environment.getExternalStorageDirectory() + "/videokit/newvideo.mp4" ,"-i",Environment.getExternalStorageDirectory() + "/videokit/watermark.png","-filter_complex","overlay=10:main_h-overlay_h-10", Environment.getExternalStorageDirectory() + "/videokit/newvideo2.mp4"};

                    //    String[] complexCommand = {"ffmpeg","-y" ,"-i",Environment.getExternalStorageDirectory() + "/videokit/newvideo.mp4","-strict","experimental", "-vf", "movie="+Environment.getExternalStorageDirectory() + "/videokit/watermark.png"+" [watermark]; [in][watermark] overlay=10:main_h-overlay_h-10 [out]","-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050", Environment.getExternalStorageDirectory() + "/videokit/newvideo1.mp4"};

                    //    String[] complexCommand = {"ffmpeg", "-y", "-i", Environment.getExternalStorageDirectory() + "/videokit/newvideo.mp4", "-i", Environment.getExternalStorageDirectory() + "/videokit/watermark.png", "-filter_complex", "[1:v] rotate=0*PI/180:c=none:ow=rotw(iw):oh=roth(ih) [rotate];[0:v][rotate] overlay=main_w-overlay_w-10:10", "-codec:a", "copy", Environment.getExternalStorageDirectory() + "/videokit/newvideo1.mp4"};


                    //    String[] complexCommand = {"ffmpeg", "-y", "-i", Environment.getExternalStorageDirectory() + "/videokit/newvideo.mp4", "-i", Environment.getExternalStorageDirectory() + "/videokit/watermark.png", "-filter_complex", "[1:v] rotate=0*PI/180:c=none:ow=rotw(iw):oh=roth(ih) [rotate];[0:v][rotate] overlay=main_w-overlay_w-10:10 [out]","-s", "480x320","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050", Environment.getExternalStorageDirectory() + "/videokit/newvideo1.mp4"};

                    //  String[] complexCommand = {"ffmpeg","-y" ,"-i", Environment.getExternalStorageDirectory() + "/videokit/newvideo.mp4","-strict","experimental", "-vf", "movie="+Environment.getExternalStorageDirectory() + "/videokit/watermark.png"+" [watermark]; [in][watermark] transpose=2 [out_transpose];[out_transpose] overlay=main_w-overlay_w-10:10 [out]","-s", "480x320","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050", Environment.getExternalStorageDirectory() + "/videokit/newvideo1.mp4"};
                    //	String[] complexCommand = {	"ffmpeg", "-i", "/sdcard/videokit/in.mp4", "-i",  "/sdcard/DCIM/Camera/IMG201612201140926.jpg", "-filter_complex", "overlay=10:10", "/sdcard/videokit/out6.mp4"};
                    vk.run(complexCommand, workFolder, getApplicationContext());
                    Log.i("test", "ffmpeg4android finished successfully");
                } catch (Throwable e) {
                    Log.e("test", "vk run exception.", e);
                }


                try {
                    if (new File(outputvideo).exists()) {
                        new File(inputvideo).delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to: " +
                        data.getData(), Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {

                output.setText("User cancelled the video capture.");

                // User cancelled the video capture
                Toast.makeText(this, "User cancelled the video capture.",
                        Toast.LENGTH_LONG).show();

            } else {

                output.setText("Video capture failed.");

                // Video capture failed, advise user
                Toast.makeText(this, "Video capture failed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


}
