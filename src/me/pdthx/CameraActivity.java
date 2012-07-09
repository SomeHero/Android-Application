package me.pdthx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.pdthx.Requests.UserCheckImageRequest;
import me.pdthx.Responses.UserCheckImageResponse;
import me.pdthx.Services.UserService;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends BaseActivity {

	private static final int MEDIA_TYPE_IMAGE = 1;
	private Camera mCamera;
	private CameraPreview mPreview;
	private UserCheckImageRequest checkRequest;
	private UserCheckImageResponse checkResponse;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker.trackPageView("Take Check Image");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		showCameraPreview();
	}
	
	
	public void showCameraPreview()
	{
		setContentView(R.layout.camera_preview);
//    	Camera.Parameters parameters = mCamera.getParameters();
//		parameters.setPictureFormat(ImageFormat.JPEG);
//		mCamera.setParameters(parameters);
		
		mCamera = getCameraInstance();
		if(mCamera == null)
		{
		//	finish();
		}
		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.preview);

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
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e){
			e.printStackTrace();
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
			checkRequest = new UserCheckImageRequest();
			checkResponse = new UserCheckImageResponse();
			
			checkRequest.image = data;
			checkResponse = UserService.sendCheckImage(checkRequest);

			if (checkResponse.Success) {
				Log.d("CheckResponse Name", checkResponse.name);

				Intent pic = new Intent();
				pic.putExtra("name", checkResponse.name);
				pic.putExtra("accountNumber", checkResponse.accountNumber);
				pic.putExtra("routingNumber", checkResponse.routingNumber);
				pic.putExtra("index", pictureFile.toString());
				setResult(RESULT_OK, pic);
				//Bring up a dialog for retake or keep picture
				finish();
			}
			else {
				Log.d("OCR YOIU ARE BAD", "AND SHOULD FEEL BAD");
				finish();
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();             
	}


	private void releaseCamera(){
		if (mCamera != null){
			mCamera.release();  
			mCamera = null;
		}
	}
}