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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.kalman.*;
public class MainActivity extends Activity implements SensorEventListener, OnClickListener {
	private SensorManager sensorManager;
	private TextView showTextView;
	private Sensor gyroscopeSensor;
	private static final float NS2S = 1.0f / 1000000000.0f;
	private float timestamp;
	private float angle[] = new float[3];
	private ProgressBar pb;
	private int status=0;
	private Button btn_start;
	private Button btn_stop;
	private TextView tv_angle;
	private Ex ex;
	float angley=0;
	float anglez=0;
	float anglex=0;
	boolean flag=false;
	double rAnglex=0;
	double rAngley=0;
	double rAnglez=0;
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
		pb=(ProgressBar)findViewById(R.id.progressBar_record_progress);
		pb.setVisibility(View.VISIBLE);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//---------------->拍摄按钮的接口	
		btn_start=(Button)findViewById(R.id.button_start);
		btn_start.setOnClickListener(this);
		btn_stop=(Button)findViewById(R.id.button_stop);
		btn_stop.setOnClickListener(this);
		tv_angle=(TextView)findViewById(R.id.textview_angle);
		tv_angle.setText("角度显示");
		ex=new Ex();
		ex.init();
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					if(flag){
						ex.inputData(angley);
						rAngley=ex.filter();
		//				Log.i("估测数据y",Double.toString(rAngley));

					}
				}
				
			}
			
		}).start();
	}
	public void init(){
		status=0;
		angle[0]=0;
		angle[1]=0;
		angle[2]=0;
		pb.setProgress(status);
		tv_angle.setText("角度显示");
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			if (timestamp != 0) {
				final float dT = (event.timestamp - timestamp) * NS2S;
				angle[0] += event.values[0] * dT;
				angle[1] += event.values[1] * dT;
				angle[2] += event.values[2] * dT;
				anglex = (float) Math.toDegrees(angle[0]);
				angley= (float) Math.toDegrees(angle[1]);
				anglez = (float) Math.toDegrees(angle[2]);
				flag=true;
				tv_angle.setText("x  "+rAnglex+"\n"+"y  "+rAngley+"\n"+"z  "+rAnglez+"\n");
				// Log.d("角度","anglex------------>" + anglex);
			//	Log.d("角度", "angley------------>" + angley);
				
				if(status<100){
					status=Math.abs((int)(((float)rAngley/360)*100));
			//		Log.d("进度",Integer.toString(status));
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
		flag=false;
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.button_start:
			init();
			sensorManager.registerListener(this, gyroscopeSensor,
					SensorManager.SENSOR_DELAY_GAME);
			break;
		case R.id.button_stop:
			sensorManager.unregisterListener(this);
			flag=false;
			init();
			break;
			
		}

	}
	public void kalman(){
		
	}
}
