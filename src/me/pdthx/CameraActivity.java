package me.pdthx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CameraActivity extends BaseActivity {
	
	/**private static final int MEDIA_TYPE_IMAGE = 1;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Button capture;
    private ImageView blah;**/
	
	private Button btn;
	private ImageView img;
	private TextView field;
	private String imagePath;
	private boolean isTaken;
	
	protected static final String PHOTO_TAKEN = "photo_taken";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker.trackPageView("Take Check Image");
        showCameraPreview();
    }
    public void showCameraPreview()
    {
    	setContentView(R.layout.camera_preview);
/**
        // Create an instance of Camera
        mCamera = getCameraInstance();
       // blah = (ImageView) findViewById(R.id.blaaah);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.preview);
        preview.addView(capture);
//        preview.addView(blah);
        preview.addView(mPreview);
     	

       // Add a listener to the Capture button
       Button captureButton = (Button) findViewById(R.id.takepicture);
       captureButton.setOnClickListener(
           new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   // get an image from the camera
                   mCamera.takePicture(null, null, mPicture);                 
               }
           });
           
           **/
    	
    	
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    
    /** Create a File for saving the image */
	private static File getOutputMediaFile(int type){

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");

	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }
	    File mediaFile;
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_.jpg");

	    return mediaFile;
	}

    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("TAG", "Error creating media file, check storage permissions:");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                
                Intent pic = new Intent();
                pic.putExtra("index", pictureFile.toString());
				setResult(RESULT_OK, pic);
                finish();
                
            } catch (FileNotFoundException e) {
                Log.d("TAG", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("TAG", "Error accessing file: " + e.getMessage());
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
}