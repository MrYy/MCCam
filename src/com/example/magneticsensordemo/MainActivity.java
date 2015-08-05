package com.example.magneticsensordemo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager sensorManager;
	private TextView showTextView;
	private Sensor gyroscopeSensor;
	private static final float NS2S = 1.0f / 1000000000.0f;
	private float timestamp;
	private float angle[] = new float[3];
	private ProgressBar pb;
	private int status=0;
	float angley=0;
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(msg.what==1){
				pb.setProgress(status);
			}
			if(msg.what==2){
				Toast.makeText(MainActivity.this,"拍摄完成",Toast.LENGTH_SHORT).show();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showTextView = (TextView) findViewById(R.id.showTextView);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		pb=(ProgressBar)findViewById(R.id.progressBar_record_progress);
//---------------->拍摄按钮的接口	
		sensorManager.registerListener(this, gyroscopeSensor,
				SensorManager.SENSOR_DELAY_GAME);
		init();
		
		new Thread(new Runnable(){

			@Override
			public void run() {

				
			}
			
		}).start();

	}
	public void init(){
		status=0;
		angley=0;
		pb.setProgress(status);
		pb.setVisibility(View.VISIBLE);
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			if (timestamp != 0) {
				final float dT = (event.timestamp - timestamp) * NS2S;
				angle[0] += event.values[0] * dT;
				angle[1] += event.values[1] * dT;
				angle[2] += event.values[2] * dT;
				float anglex = (float) Math.toDegrees(angle[0]);
				angley= (float) Math.toDegrees(angle[1]);
				float anglez = (float) Math.toDegrees(angle[2]);
				// Log.d("角度","anglex------------>" + anglex);
				Log.d("角度", "angley------------>" + angley);
				if(status<100){
					status=Math.abs((int)(((float)angley/360)*100));
					Log.d("进度",Integer.toString(status));
					handler.sendEmptyMessage(1);
				}else{
					sensorManager.unregisterListener(this);
					handler.sendEmptyMessage(2);
				}
				// Log.d("角度","anglez------------>" + anglez);
			}
			// Log.d("TAMESTAMP",Long.toString(event.timestamp));
			timestamp = event.timestamp;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensorManager.unregisterListener(this);
	}
}
