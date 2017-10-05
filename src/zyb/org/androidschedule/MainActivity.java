package zyb.org.androidschedule;

import temp.DataBase;
import temp.MyApplication;
import temp.MyDialog;
import temp.ShareMethod;
import zyb.org.editschedule.SetActivity;
import android.media.AudioManager;          //audio manager, control alarm, music etc.
import android.os.Bundle;                   //transfor data between activities (key-value)
import android.app.Activity;				//interact with users
import android.app.AlertDialog;				//use AlertDialog
import android.app.Service;					//background operation of Android
import android.content.Context;				//
import android.content.DialogInterface;		//interface that defines a dialogtype class that can be shown dismissed, or canceled, and may have buttons that can be clicked
import android.content.Intent;				//communicate or interact with different packages
import android.content.SharedPreferences;	//write or read data from XML
import android.database.Cursor;				//save ask data
import android.support.v4.widget.SimpleCursorAdapter;	//link data with view
import android.util.Log;					//journal of system
import android.view.GestureDetector;		//detect gesture and events
import android.view.Menu;					//interface of menu button on phone
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends Activity {
	 
	public ListView list[] = new ListView[7];
	private TabHost tabs   = null;
	private TextView exitButton = null; 
	private TextView setButton = null;
	public static DataBase db;
	public Cursor[] cursor=new Cursor[7];
	public SimpleCursorAdapter adapter;
	private SharedPreferences pre;

    //define gesture instance
	private GestureDetector detector = null;

    //define min distance between two gestures
	private final int FLIP_DISTANCE = 200;
		
	@Override
	//savedInstanceState save state of instance
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//add this activaity in Myapplication instance
		MyApplication.getInstance().addActivity(this);
		
		db=new DataBase(MainActivity.this);
		pre=getSharedPreferences("firstStart",Context.MODE_PRIVATE);
		/*
		 * if run first, create database
		 */
		if(pre.getBoolean("firstStart", true)){
			SingleInstance.createTable();
			(pre.edit()).putBoolean("firstStart",false).commit();
//			finish();			
		}

		//visit controls in res
		exitButton = (TextView)findViewById(R.id.exitButton);
		setButton = (TextView)findViewById(R.id.setButton);
		list[0] = (ListView)findViewById(R.id.list0);
		list[1] = (ListView)findViewById(R.id.list1);	
		list[2] = (ListView)findViewById(R.id.list2);
		list[3] = (ListView)findViewById(R.id.list3);	
		list[4] = (ListView)findViewById(R.id.list4);	
		list[5] = (ListView)findViewById(R.id.list5);
		list[6] = (ListView)findViewById(R.id.list6);			
		tabs  = (TabHost)findViewById(R.id.tabhost);

	     //create gesture instance
	    detector = new GestureDetector(this, new DetectorGestureListener());
   
		//prepare to use TabSpec
		tabs.setup();
		
		//add 7 optional card at main page
		TabHost.TabSpec  spec = null;
		addCard(spec,"tag1",R.id.list0,"Sun");
		addCard(spec,"tag2",R.id.list1,"Mon");
		addCard(spec,"tag3",R.id.list2,"Tue");
		addCard(spec,"tag4",R.id.list3,"Wed");
		addCard(spec,"tag5",R.id.list4,"Thu");
		addCard(spec,"tag6",R.id.list5,"Fri");
		addCard(spec,"tag7",R.id.list6,"Sat");

		//change color of tabHost
		TabWidget tabWidget = tabs.getTabWidget();
		for(int i=0;i<tabWidget.getChildCount();i++){
			TextView tv = (TextView)tabWidget.getChildAt(i).findViewById(android.R.id.title);
			tv.setTextColor(0xff004499);				
		}
		
		//default day is current day
		tabs.setCurrentTab(ShareMethod.getWeekDay());
		
		//use adapter to add content in tab
		for(int i=0;i<7;i++){
			cursor[i]=MainActivity.db.select(i);		
			list[i].setAdapter(adapter(i));
		}
		
		//define a audio object
		final AudioManager audioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
		//get the default setting of phone alarm, transfor to activity
		 final int orgRingerMode = audioManager.getRingerMode(); 
		 
		//exit button
		exitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//create AlertDialog.builder.
			    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				exit(builder);
			}
		}); 
		
		//setting button
		setButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SetActivity.class);
				//tranfor orgRingMode's data to activity_set
				intent.putExtra("mode_ringer", orgRingerMode);
				startActivity(intent);
			}
		});
		
		for( int day=0;day<7;day++){
			//bind listeners on 7 listviews.  Gesture handle touch actions
			list[day].setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event)   {
					return detector.onTouchEvent(event);
				}
			});

			//bind listeners on items of 7 listviews
			list[day].setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						final int id, long arg3) {
					final int currentDay=tabs.getCurrentTab();
					final int n=id;
				    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				    builder.setIcon(R.drawable.ic_launcher);
					builder.setTitle("choose");
					TextView tv=(TextView)arg1.findViewById(R.id.ltext0);
					Log.i("Test",(tv.getText().toString().equals(""))+"");
					//if item is empty, then create dialog
					if((tv.getText()).toString().equals("")){
						//ͨadd content to dialog from database
						builder.setItems(R.array.edit_options1, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {					
								//jump to edit class if click blank of list
								if(which == 0){					
									new MyDialog(MainActivity.this).add(currentDay,n);
								}
							}
						});
						builder.create().show();
					  }
					//else delete data or change content of dialog
					else{
						builder.setItems(R.array.edit_options2, new DialogInterface.OnClickListener() {
							
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//jump to edit class
								if(which == 0){					
									new MyDialog(MainActivity.this).modify(currentDay,n);
								}
								if(which == 1){
									cursor[currentDay].moveToPosition(n);
									int n1=Integer.parseInt(cursor[currentDay].getString(7));//number of classes
									int n2=Integer.parseInt(cursor[currentDay].getString(8));//number of chosen class
									switch(n2){
										case 0:
											for(int m=0;m<n1;m++){
												MainActivity.db.deleteData(currentDay,n+m+1);
												}
											break;
	
										case 1:
											MainActivity.db.deleteData(currentDay,n);
											for(int m=1;m<n1;m++){
												MainActivity.db.deleteData(currentDay,n+m);
												}
											break;		
										case 2:
											MainActivity.db.deleteData(currentDay,n-1);
											MainActivity.db.deleteData(currentDay,n);
											for(int m=2;m<n1;m++){
												MainActivity.db.deleteData(currentDay,n+m-1);
												}
												break;
										case 3:
											for(int m=n2;m>=0;m--){
												MainActivity.db.deleteData(currentDay,n-m+1);
												}
												break;
										default:
											break;
									}
									cursor[currentDay].requery();
									list[currentDay].invalidate();
								}
							}
						});
						builder.create().show();
					}
				}
			});
		}
		
	}
	//implment interface of GestureDetector.OnGestureListener
	class DetectorGestureListener implements GestureDetector.OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		//respond to flying gesture
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int i = tabs.getCurrentTab();
			//if the distance between first touch point and second touch point over FLIP_DISANCE, then it is left flying.
				if(e1.getX() - e2.getX() > FLIP_DISTANCE){
					if(i<6)
						tabs.setCurrentTab(i+1);
				//	float currentX = e2.getX();
				//	list[i].setRight((int) (inialX - currentX));
					return true;
				}

				//�ڶ��������¼���X����ֵ��ȥ��һ�������¼���X����ֵ����FLIP_DISTANCE��Ҳ�������ƴ������һ���
				else if(e2.getX() - e1.getX() > FLIP_DISTANCE){
					if(i>0)
						tabs.setCurrentTab(i-1);	
					return true;
				}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
	
	}
	
	
	//��дActivity�е�onTouchEvent����������Activity�ϵĴ����¼�����GestureDetector����
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}
	
	//���ò˵���ť
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	//������˵��еġ��˳�����ʱ��������ʾ�Ƿ��˳��ĶԻ���
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//����AlertDialog.Builder���󣬸ö�����AlterDialog�Ĵ�������AlterDialog�������������Ի���
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(item.getItemId() == R.id.menu_exit){
			exit(builder);
			return true;
		}
		if(item.getItemId() == R.id.menu_settings){
			Intent intent = new Intent(MainActivity.this, SetActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	} 
	

	//�� ����:Ϊ���������ѡ�
	public void addCard(TabHost.TabSpec spec,String tag,int id,String name){
		spec = tabs.newTabSpec(tag);
		spec.setContent(id);
		spec.setIndicator(name);
		tabs.addTab(spec);
	}
	//�ӷ��������������Ƿ��˳�����ĶԻ��򣬲�ִ��ִ���Ƿ��˳�����
	public void exit(AlertDialog.Builder builder){
		//Ϊ�����ĶԻ������ñ��������
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("�˳�����");
		builder.setMessage("ȷ��Ҫ�˳�����γ̱���");
		//������ߵİ�ťΪ��ȷ��������������󶨼�������������˳�
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//�˳�Ӧ�ó��򣬼����ٵ����е�activity
				MyApplication.getInstance().exitApp();
			}
		});
		//�����ұߵİ�ťΪ��ȡ��������������󶨼��������������Ȼͣ���ڵ�ǰ����
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
								
			}
		});
		//��������ʾ�����ĶԻ���
		builder.create().show();
	}
	/*
	 * Ϊÿһ��list�ṩ����������
	 */
	@SuppressWarnings("deprecation")
	public SimpleCursorAdapter adapter(int i){
		return new SimpleCursorAdapter(this, R.layout.list_v2,cursor[i],new String[]{"_id","classes","location",
		"teacher","zhoushu"},new int[]{R.id.number,R.id.ltext0,R.id.ltext1,R.id.ltext6,R.id.ltext7} );
	}
	
	/*
	 * ��һ������ʱ�������ݿ��
	 */
	static class SingleInstance{
		static SingleInstance si;
		private SingleInstance(){
			for(int i=0;i<7;i++){
				db.createTable(i);
			}
		}
		static SingleInstance createTable(){
			if(si==null)
				return si=new SingleInstance();
			return null;
		}
	}
}
