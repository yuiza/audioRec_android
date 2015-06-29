package com.example.audiorec;

import java.io.IOException;

import android.media.*;
import android.os.*;
import android.app.Activity;
import android.util.*;
import android.view.*;
import android.widget.*;



public class MainActivity extends Activity {
	//録音に必要な物用意
	final static int SAMPLING_RATE = 44100;
	AudioRecord audioRec = null;
	boolean bIsRecording = false;
	int bufsize;
	static int flag = 0;
	static int play = 0;

	MediaRecorder mRecorder;
	MediaPlayer mPlayer;
	String mFilename;

	//
	Handler stopHandler = new Handler();
	Handler playHandler = new Handler();
	Handler recHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bufsize=AudioRecord.getMinBufferSize(SAMPLING_RATE,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
		mFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/rec.3gp";

		Button btnStartRec = (Button) findViewById(R.id.button1);
		Button btnStopRec = (Button) findViewById(R.id.button2);
		Button btnPlayRec = (Button)findViewById(R.id.button3);


		btnStartRec.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(MainActivity.flag == 1){
					Toast.makeText(MainActivity.this, "今忙しい", Toast.LENGTH_SHORT).show();
				}else{
					//スレッドにて録音
					new Thread(new Runnable(){
						public void run(){
							recHandler.post(new Runnable() {
								public void run() {
									try {
										mRecorder = new MediaRecorder();
										mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
										mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
										mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
										mRecorder.setOutputFile(mFilename);
										mRecorder.prepare();
										mRecorder.start();
										MainActivity.flag = 1;
										Toast.makeText(MainActivity.this, "録音なう", Toast.LENGTH_SHORT).show();
									} catch (IllegalStateException e) {
										Log.e("TAG", e.toString());
										Toast.makeText(MainActivity.this, "例外ですね:IllegalStateIOException", Toast.LENGTH_SHORT).show();
									} catch (IOException e) {
										Log.e("TAG", e.toString());
										Toast.makeText(MainActivity.this, "例外が...:IOException", Toast.LENGTH_SHORT	).show();
									}
								}
							});
						}
					}).start();

				}
			}
		});


		btnStopRec.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//スレッドにてストップ制御
				new Thread(new Runnable(){
					public void run(){
						stopHandler.post(new Runnable() {
							public void run() {
								// TODO 自動生成されたメソッド・スタブ
								if(MainActivity.flag == 1){
									mRecorder.stop();
									mRecorder.reset();
									mRecorder.release();
									Toast.makeText(MainActivity.this, "録音終わったぉ", Toast.LENGTH_SHORT).show();
									MainActivity.flag = 0;
									mRecorder = null;
								}else{
									Toast.makeText(MainActivity.this, "なんで押したんだぉ？", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}).start();
			}
		});


		btnPlayRec.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				if(MainActivity.play == 1){
					Toast.makeText(MainActivity.this, "今忙しい", Toast.LENGTH_SHORT).show();
				}else{
					//スレッドにて再生
					new Thread(new Runnable(){
						public void run(){
							playHandler.post(new Runnable() {
								public void run() {
									try {
										mPlayer = new MediaPlayer();
										mPlayer.setDataSource(mFilename);
										mPlayer.setVolume(100, 100);
										mPlayer.prepare();
										mPlayer.start();
										MainActivity.play = 1;
										Toast.makeText(MainActivity.this, "再生中", Toast.LENGTH_SHORT	).show();
									} catch (IllegalArgumentException e){
										Log.e("TAG", e.toString());
										Toast.makeText(MainActivity.this, "例外ですね:IllegalArgumentIOException", Toast.LENGTH_SHORT).show();
									} catch (SecurityException e){
										Log.e("TAG", e.toString());
										Toast.makeText(MainActivity.this, "例外:SecurityException", Toast.LENGTH_SHORT).show();
									} catch (IllegalStateException e) {
										Log.e("TAG", e.toString());
										Toast.makeText(MainActivity.this, "例外ですね:IllegalStateIOException", Toast.LENGTH_SHORT).show();
									} catch (IOException e) {
										Log.e("TAG", e.toString());
										Toast.makeText(MainActivity.this, "例外が...:IOException", Toast.LENGTH_SHORT	).show();
									}
								}
							});
						}
					}).start();
				}
				mPlayer = null;
				MainActivity.play = 0;
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
		mRecorder.stop();
		mRecorder.reset();
		//再生中ならそっちストップ
		if(mPlayer.isPlaying()) {
			mPlayer.stop();
			mPlayer = null;
		}
		mRecorder.release();
		mRecorder = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
