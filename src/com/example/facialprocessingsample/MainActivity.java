package com.example.facialprocessingsample;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.PREVIEW_ROTATION_ANGLE;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Camera.PreviewCallback {

private int numFaces;
FaceData[] faceArray=null;
Camera cameraObj;
FrameLayout preview;
private CameraSurfacePreview mPreview;
private int FRONT_CAMERA_INDEX=1;
private int BACK_CAMERA_INDEX=0;
private static boolean switchCamera=false;
private boolean _qcSDKEnabled;
FacialProcessing faceProc;
Display display;
private int displayAngle;
DrawView drawView;
TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		preview=(FrameLayout) findViewById(R.id.camera_preview);
		tv=(TextView) findViewById(R.id.textView1);
		startCamera();
		Button switchCameraButton=(Button) findViewById(R.id.switchCamera);
		switchCameraButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!switchCamera)
				{
					stopCamera();
					switchCamera=true;
					startCamera();
				
				}
				else
				{
					stopCamera();
					switchCamera=false;
					startCamera();
				
					
				}
				
			}
		});
		display=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

private void startCamera()
{

	_qcSDKEnabled = FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_PROCESSING);
	if(_qcSDKEnabled && faceProc==null)
	{
		Log.e("TAG", "Facial Feature Supported!!");
		faceProc=FacialProcessing.getInstance();
	//	Toast.makeText(getApplicationContext(), "Facial Supported!!", Toast.LENGTH_LONG).show();
		
	}
	else if(!_qcSDKEnabled)
	{
		Toast.makeText(getApplicationContext(), "Sorry facial not Supported!!", Toast.LENGTH_LONG).show();
	}
	
	if(!switchCamera)
	{
		
		cameraObj=Camera.open(FRONT_CAMERA_INDEX);
	}
	else
	{
		cameraObj=Camera.open(BACK_CAMERA_INDEX);		
	}

mPreview=new CameraSurfacePreview(MainActivity.this, cameraObj);	
preview=(FrameLayout)findViewById(R.id.camera_preview);
preview.addView(mPreview);
cameraObj.setPreviewCallback(MainActivity.this);
	
}
private void stopCamera()
{
	if(cameraObj!=null)
	{
		cameraObj.stopPreview();
		cameraObj.setPreviewCallback(null);
		preview.removeView(mPreview);
		cameraObj.release();
	 if(_qcSDKEnabled)
		{
			faceProc.release();
			faceProc=null;
		}	
	}
	cameraObj=null;
}
protected void onPause()
{
	super.onPause();
	stopCamera();
}

protected void onDestroy()
{
	super.onDestroy();
}
protected void onResume()
{
	super.onResume();
	if(cameraObj!=null)
	{
		stopCamera();
		
	}
	startCamera(); 
}
@Override
public void onPreviewFrame(byte[] data, Camera camera) {
	// TODO Auto-generated method stub
int dRotation=display.getRotation();
PREVIEW_ROTATION_ANGLE angleEnum=PREVIEW_ROTATION_ANGLE.ROT_0;

switch(dRotation)
{
case 0:
displayAngle=90;
angleEnum=PREVIEW_ROTATION_ANGLE.ROT_90;
break;
case 1:
	displayAngle=0;
	angleEnum=PREVIEW_ROTATION_ANGLE.ROT_0;
	break;
case 2:
	displayAngle=270;
	angleEnum=PREVIEW_ROTATION_ANGLE.ROT_270;
	break;
	
case 3:	displayAngle=180;
	angleEnum=PREVIEW_ROTATION_ANGLE.ROT_180;
	break; 

}
cameraObj.setDisplayOrientation(displayAngle);
if(_qcSDKEnabled)
{
	if(faceProc==null)
	{
		faceProc=FacialProcessing.getInstance();
		
	}
	Parameters params =cameraObj.getParameters();
	Size previewSize=params.getPreviewSize();
	faceProc.setRecognitionConfidence(75);
	if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE && !switchCamera)
	{
		
		faceProc.setFrame(data, previewSize.width, previewSize.height, true, angleEnum);
		
	}
	else if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE && switchCamera)
	{
		
		faceProc.setFrame(data, previewSize.width, previewSize.height, false, angleEnum);
	}
	else if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT && !switchCamera)
	{
		
		faceProc.setFrame(data, previewSize.width, previewSize.height, true, angleEnum);
		
	}
	else 	{
		
		faceProc.setFrame(data, previewSize.width, previewSize.height, false, angleEnum);
	}
	
}
numFaces=faceProc.getNumFaces();
int SurfaceWidth=mPreview.getWidth();
int SurfaceHeight=mPreview.getHeight();
faceProc.normalizeCoordinates(SurfaceWidth, SurfaceHeight);

if(numFaces>0)
{
	faceArray=faceProc.getFaceData();
for(int i=0;i<numFaces;i++)
{
	Integer t;
	try{
	 t = faceProc.addPerson(i);
	}
	catch(java.lang.IllegalArgumentException i1)
	{
	Toast.makeText(getApplicationContext(), "Cannot Add", Toast.LENGTH_SHORT).show();
	}
	
	
	//tv.setText(t.toString());
}

	//Toast.makeText(getApplicationContext(), "I got faces "+numFaces, Toast.LENGTH_SHORT).show();
	preview.removeView(drawView);
	for(int i=0;i<numFaces;i++)
	{		
		Integer t;
	if((t=faceArray[i].getPersonId())!=-111)
		tv.setText("Got id "+t.toString());
		
		
		} 
		
	drawView=new DrawView(this, faceArray, true);
	preview.addView(drawView);
	}
else
{
	preview.removeView(drawView);
	drawView=new DrawView(this, null, false);
	preview.addView(drawView);
}
}

}
