package com.example.facialprocessingsample;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfacePreview  extends SurfaceView implements SurfaceHolder.Callback{
private SurfaceHolder mHolder;
private Camera mCamera;
Context mContext;

	public CameraSurfacePreview(Context context,Camera camera) {
		super(context);
		mCamera =camera;
		mContext=context;
		mHolder=getHolder();
		mHolder.addCallback(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try
		{
			
			
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		}
		catch(IOException e)
		{
			Log.d("TAG", "Error setting camera preview"+e.getMessage());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

}
