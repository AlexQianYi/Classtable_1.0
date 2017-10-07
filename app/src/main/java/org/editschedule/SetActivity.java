package org.editschedule;

/**
 * Created by yiqian on 2017/10/6.
 */

        import temp.MyApplication;
        import org.about.AboutUsActivity;
        import org.androidschedule.R;
        import org.service.RemindReceiver;
        import org.version.VersionActivity;
        import android.app.Activity;
        import android.app.AlarmManager;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.app.PendingIntent;
        import android.app.Service;
        import android.app.AlertDialog.Builder;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.media.AudioManager;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.CompoundButton;
        import android.widget.CompoundButton.OnCheckedChangeListener;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.Toast;

public class SetActivity extends Activity {

    //declare a SharedPreferences object to save switch items
    private SharedPreferences preferences = null;
    //write data to preferences by editor
    private SharedPreferences.Editor editor = null;

    //declare a SharedPreferences object to save time_choice
    private SharedPreferences pre = null;
    //pre_editor write data to pre
    private SharedPreferences.Editor pre_editor = null;

    //alarmManager: provide alarm service before class
    private AlarmManager alarmManager = null;
    //pi: point alarmManager function
    private PendingIntent pi = null;
    private Intent alarm_receiver = null;

    //alarm time dialog's id
    final int SINGLE_DIALOG = 0x113;
    //define time choice
    private int time_choice = 0;

    private Switch switch_quietButton;
    private Switch switch_remindButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        //add this activity into MyApplication container
        MyApplication.getInstance().addActivity(this);

        //declare a object to get audio service
        final AudioManager audioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);

        //get original ringer from MainActivity
        Intent intent = getIntent();
        final int orgRingerMode = intent.getIntExtra("mode_ringer", AudioManager.RINGER_MODE_NORMAL);

        //get system alarm service
        alarmManager = (AlarmManager)getSystemService(Service.ALARM_SERVICE);

        //ָalarm items
        alarm_receiver = new Intent(SetActivity.this,RemindReceiver.class);
        pi = PendingIntent.getBroadcast(SetActivity.this, 0, alarm_receiver, 0);

        //take out items
        TextView backButton = (TextView)findViewById(R.id.backtoMainButton);
        switch_quietButton = (Switch)findViewById(R.id.switch_quiet);
        switch_remindButton = (Switch)findViewById(R.id.switch_remind);

        //MODE_MULTI_PROCESS
        this.pre = SetActivity.this.getSharedPreferences("time", Context.MODE_MULTI_PROCESS);
        this.pre_editor = pre.edit();

        //ָSharePreferences data can use cross progesses
        this.preferences = SetActivity.this.getSharedPreferences("switch", Context.MODE_MULTI_PROCESS);
        this.editor = preferences.edit();


        //when create this activity, get data of switch_quitButton and switch_remindButton
        Boolean quiet_status = preferences.getBoolean("switch_quiet", false);
        Boolean remind_status = preferences.getBoolean("switch_remind", false);
        switch_quietButton.setChecked(quiet_status);
        switch_remindButton.setChecked(remind_status);

        //bandle listener on backButton
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
//				Intent intent = new Intent(Set.this,MainActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//ˢ��
//				startActivity(intent);
            }
        });

        //bandle listener on switch_quietButton
        switch_quietButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //start quiet service
                Intent intent = new Intent();
                intent.setAction("zyb.org.service.QUIET_SERVICE");

                if(isChecked){
                    if(startService(intent) != null)
                        Toast.makeText(SetActivity.this, "The device is muted", 3000).show();
                    else{
                        Toast.makeText(SetActivity.this, "muted failed, please try again", 3000).show();
                        switch_quietButton.setChecked(false);
                    }
                }
                else{
                    if(stopService(intent))
                        Toast.makeText(SetActivity.this, "Back to the original ring model", 3000).show();
                    else{
                        Toast.makeText(SetActivity.this, "Close muted failed, please try again", 3000).show();
                        switch_quietButton.setChecked(true);
                    }
                    audioManager.setRingerMode(orgRingerMode);
                }
                //save switch information in this perferences
                SetActivity.this.editor.putBoolean("switch_quiet", isChecked);
                editor.commit();
            }
        });

        //bandle listener on remindButton
        switch_remindButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showDialog(SINGLE_DIALOG);
                }
                else{
                    alarmManager.cancel(pi);
                }
                //save switch information in this perferences
                SetActivity.this.editor.putBoolean("switch_remind", isChecked);
                editor.commit();
            }
        });

    }

    @Override
    //showDialog() will use this return dialog
    protected Dialog onCreateDialog(int id, Bundle args) {

        //different types of dialog
        if(id == SINGLE_DIALOG){
            Builder b = new AlertDialog.Builder(this);
            //heading of dialog
            b.setTitle("Choose Time Before Class to Remind");
            //list of dialog
            b.setSingleChoiceItems(R.array.set_remind, -1, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog,
                                    int which){
                    switch (which){
                        case 0:
                            time_choice = 5;
                            break;
                        case 1:
                            time_choice = 10;
                            break;
                        case 2:
                            time_choice = 20;
                            break;
                        case 3:
                            time_choice = 30;
                            break;
                        case 4:
                            time_choice = 40;
                            break;
                        case 5:
                            time_choice = 50;
                            break;
                        case 6:
                            time_choice = 60;
                            break;
                    }
                }
            });
            //Confirm button
            b.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
//					System.out.println("SetActivity:" + time_choice);
                    if(time_choice == 0){
                        Toast.makeText(SetActivity.this, "Choose Time Before Class to Remind", 3000).show();
                        switch_remindButton.setChecked(false);
                    }else{
                        SetActivity.this.pre_editor.putInt("time_choice", time_choice);
                        pre_editor.commit();
                        //run pi for every 1 minute: broadcast
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pi);
                        Toast.makeText(SetActivity.this, "setting succeed, system will remind you " + time_choice + "before class", Toast.LENGTH_LONG).show();
                    }
                }
            });
            //Cancel button
            b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch_remindButton.setChecked(false);
                }
            });
            //create dialog
            return b.create();
        }
        else
            return null;
    }



    //About us dialog
    public void click_us(View v){
        Intent intent = new Intent(SetActivity.this, AboutUsActivity.class);
        startActivity(intent);
    }
    //support version
    public void click_version(View v){
        Intent intent = new Intent(SetActivity.this, VersionActivity.class);
        startActivity(intent);
    }
    public void click_revision(View v){
        Log.i("MyDebug", "revision");
    }
}

