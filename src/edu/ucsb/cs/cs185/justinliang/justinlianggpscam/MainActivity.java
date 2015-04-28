package edu.ucsb.cs.cs185.justinliang.justinlianggpscam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements LocationListener {

	private LocationManager locationManager;
	private String provider;
	private static String currentPhoto;
	private static String slat = "";
    private static String slng = "";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
     // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);        
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        // Initialize the location fields
        if (location != null) 
          onLocationChanged(location);
        else
          Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
    }
    
    /* Request updates at startup */
    @Override
    protected void onResume() {
      super.onResume();
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
      super.onPause();
      locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
      double lat = (double) (location.getLatitude());
      double lng = (double) (location.getLongitude());
      slat = "" + lat;
      slng = "" + lng;
//      Toast.makeText(this, slat, Toast.LENGTH_SHORT).show();
//      Toast.makeText(this, slng, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
      Toast.makeText(this, "Enabled new provider " + provider,
          Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
      Toast.makeText(this, "Disabled provider " + provider,
          Toast.LENGTH_SHORT).show();
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	 MenuInflater inflater = getMenuInflater();
    	 inflater.inflate(R.menu.main, menu);
    	 return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId()) 
    	{
	        case R.id.action_photo:
	        	takePhoto();
	            return true;
	        case R.id.action_settings:
	        	settingsDialog();
	            return true;
	        case R.id.action_help:
	        	helpDialog();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    public void settingsDialog()
    {
    	AlertDialog alert = new AlertDialog.Builder(this).create();
    	alert.setMessage("Haha no settings!");
    	alert.setButton("Done", new DialogInterface.OnClickListener()
    	{
    		@Override
    		public void onClick(DialogInterface dialog, int which)
    		{
    			dialog.dismiss();
    		}
    	});
    	alert.show();
    }
    
    
    public void helpDialog()
    {
    	AlertDialog alert = new AlertDialog.Builder(this).create();
    	alert.setMessage("Name: Justin Liang" + "\n" + "Version: 1.0");
    	alert.setButton("Done", new DialogInterface.OnClickListener()
    	{
    		@Override
    		public void onClick(DialogInterface dialog, int which)
    		{
    			dialog.dismiss();
    		}
    	});
    	alert.show();
    }
   
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;    
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }

    static int count = 1;
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

    	String root = Environment.getExternalStorageDirectory() + "/GPSPics/";
        File mediaStorageDir = new File(root);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
        	if(count >= 1 || count <= 9)
        	{
	            mediaFile = new File(Environment.getExternalStorageDirectory() + "/GPSPics/", "photo-00"+ count + ".jpg");
	            currentPhoto = "photo-00"+count;
	            count++;
	            return mediaFile;
        	}
        	else if(count >= 10 || count <= 99)
        	{
        		mediaFile = new File(Environment.getExternalStorageDirectory() + "/GPSPics/", "photo-0"+ count + ".jpg");
        		currentPhoto = "photo-0"+count;
        		count++;
	            return mediaFile;
        	}
        	else
        	{
        		mediaFile = new File(Environment.getExternalStorageDirectory() + "/GPSPics/", "photo-"+ count + ".jpg");
        		currentPhoto = "photo-"+count;
        		count++;
	            return mediaFile;
        	}
        } 
        else 
        {
            return null;
        }
    }
    
    // Take a photo and store it into a directory
    public void takePhoto()
    {
    	// create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);        
    }
    
    // Take a photo for the bottom take photo button
    public void takePhotoBottomButton(View view)
    {
    	takePhoto();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	// Set background to recently taken photo
    	File img = new File(fileUri.getPath());
    	
    	// Downsample the images in case it's too big
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		
		Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath(), options);
    	Drawable drawableBitmap = new BitmapDrawable(bitmap);
    	findViewById(R.id.container).setBackgroundDrawable(drawableBitmap);
    	
    	// Create XML file and append photo/GPS information to it
    	File xmlfile = new File(Environment.getExternalStorageDirectory() + "/GPSPics/", "PicListGPS.xml");
            
    	try
    	{
//    		Toast.makeText(this, "I'M HERE", Toast.LENGTH_SHORT).show();
    		FileWriter filewriter = new FileWriter(xmlfile, true);
    		BufferedWriter out = new BufferedWriter(filewriter);
    		out.write("<image>" + "\n");
    		out.write("<name>" + currentPhoto + "</name>" + "\n");
    		out.write("<lat>" + slat + "</lat>" + "\n");
    		out.write("<long>" + slng + "</long>" + "\n");
    		out.write("</image>" + "\n");
    		out.write("\n");
    		out.close();
    	}
    	catch (IOException e) 
    	{
            Log.e("TAG", "Could not write file " + e.getMessage());
        }
    }
}
