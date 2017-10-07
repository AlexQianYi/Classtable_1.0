package org.service;

/**
 * Created by yiqian on 2017/10/6.
 */

        import java.util.Timer;
        import java.util.TimerTask;
        import temp.DataBase;
        import temp.ShareMethod;
        import android.app.Service;
        import android.content.Intent;
        import android.database.Cursor;
        import android.media.AudioManager;
        import android.os.IBinder;

public class SetQuietService extends Service {

    //save time
    String[][][] temp = new String[7][12][2];

    //get database define cursor
    DataBase db = new DataBase(SetQuietService.this);
    Cursor[] cursor = new Cursor[7];

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //����һ����ȡϵͳ��Ƶ�������Ķ���
        final AudioManager audioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
        //��ȡ�ֻ�֮ǰ���úõ�����ģʽ
        final int orgRingerMode = audioManager.getRingerMode();

        //ÿ��һ���Ӵ����ݿ���ȡ�Դ����ݣ����һ�ε�ǰ��ʱ�䣬�������û���������¿�ʱ��Ƚϣ������ȣ���ִ����Ӧ�ľ������߻ָ���������
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                //ȡ�����ݿ���ÿ�յ����ݣ�������cursor������
                for(int i=0;i<7;i++){
                    cursor[i]=db.select(i);
                }

                //�����ݿ�ȡ���û�������Ͽκ��¿�ʱ�䣬���������Ͽ��Զ�����
                for(int day=0;day<7;day++){
                    for(int row=0;row<12;row++){
                        cursor[day].moveToPosition(row);
                        for(int time=0;time<2;time++){
                            temp[day][row][time] = cursor[day].getString(time+5);
                        }
                        if(!temp[day][row][0].equals(""))
                            temp[day][row][0] = temp[day][row][0].substring(temp[day][row][0].indexOf(":")+2);
                    }
                }

                //��ȡ��ǰ�������ڼ�
                int currentDay = ShareMethod.getWeekDay();
                for(int j=0;j<12;j++){
                    //��ȡ�ֻ���ǰ������ģʽ
                    int currentRingerMode = audioManager.getRingerMode();
                    if(temp[currentDay][j][0].equals(ShareMethod.getTime()) && currentRingerMode!=AudioManager.RINGER_MODE_VIBRATE){
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//						System.out.println("class is on");
                    }
                    if(temp[currentDay][j][1].equals(ShareMethod.getTime()) && currentRingerMode==AudioManager.RINGER_MODE_VIBRATE){
                        audioManager.setRingerMode(orgRingerMode);
//						System.out.println("class is over");
                    }
                }

            }
        }, 0, 60000);
        return super.onStartCommand(intent, flags, startId);
    }

}

