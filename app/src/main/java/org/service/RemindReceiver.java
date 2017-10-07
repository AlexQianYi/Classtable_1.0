package org.service;

/**
 * Created by yiqian on 2017/10/6.
 */


        import java.util.Calendar;
        import java.util.Date;
        import temp.DataBase;
        import temp.ShareMethod;
        import org.editschedule.RemindActivity;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;

public class RemindReceiver extends BroadcastReceiver {

    //∂®“Â±£¥Ê√ø’≈±Ì ˝æ›µƒcursorºØ∫œ
    Cursor[] cursor = new Cursor[7];
    //±£¥Ê ±º‰£¨temp[day][row][hm]±Ì æµ⁄day+1∏ˆtab—°œÓø®÷–µƒµ⁄row+1∏ˆ––÷–”√ªß ‰»Îµƒµ⁄“ª∏ˆ£®º¥øŒ≥Ãø™ º£© ±º‰≤∑÷Œ™ ±∫Õ∑÷
    //hmŒ™0 ±±Ì æ ±£¨1±Ì æ∑÷£¨2 ±¥˙±Ì ±∫Õ∑÷µƒ◊È∫œ£¨º¥Œ¥≤∑÷«∞µƒ◊÷∑˚¥Æ
    String[][][] temp = new String[7][12][3];
    //Ω´temp ˝◊È÷–µƒ◊÷∑˚¥Æ◊™ªØŒ™œ‡”¶µƒ’˝ ˝£¨’‚¿Ô»•µÙ¡À ±∫Õ∑÷µƒ◊È∫œ
    int[][][] start_time = new int[7][12][2];
    private int advance_time;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        //»°µ√ ˝æ›ø‚
        DataBase db = new DataBase(arg0);
        //»°≥ˆ ˝æ›ø‚÷–√ø»’µƒ ˝æ›£¨±£¥Ê‘⁄cursor ˝◊È÷–
        for(int i=0;i<7;i++){
            cursor[i]=db.select(i);
        }
        //¥” ˝æ›ø‚»°≥ˆ”√ªß ‰»Îµƒ…œøŒµƒ ±∫Õ∑÷£¨”√¿¥…Ë÷√øŒ«∞Ã·–—
        for(int day=0;day<7;day++){
            for(int row=0;row<12;row++){
                cursor[day].moveToPosition(row);
                temp[day][row][2] = cursor[day].getString(5);
                if(!temp[day][row][2].equals("")){
                    temp[day][row][2] = temp[day][row][2].substring(temp[day][row][2].indexOf(":")+2);
                    temp[day][row][0] = temp[day][row][2].substring(0, temp[day][row][2].indexOf(":"));
                    temp[day][row][1] = temp[day][row][2].substring(temp[day][row][2].indexOf(":")+1);
                }
                else{
                    temp[day][row][0] = temp[day][row][1] = "0";
                }
                for(int hm=0;hm<2;hm++){
                    start_time[day][row][hm] = Integer.parseInt(temp[day][row][hm]);
                }
            }
        }

        //¥”∏√context÷–∆Ù∂ØÃ·–—µƒactivity£¨∏˘æ›SDKŒƒµµµƒÀµ√˜£¨–Ë“™º”…œaddFlags()“ªæ‰
        Intent remind_intent = new Intent(arg0, RemindActivity.class);
        remind_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //ªÒ»°Ã·«∞Ã·–—µƒ ±º‰÷µ,»Áπ˚√ª”–ªÒ»°µΩ‘Ú»°ƒ¨»œ÷µ30∑÷÷”
//		int advance_time = arg1.getIntExtra("anvance_remindtime", 20);
        //’‚¿Ôƒ£ Ω“ª∂®“™…Ë÷√Œ™MODE_MULTI_PROCESS£¨∑Ò‘Úº¥ πœ‡”¶µƒxmlŒƒº˛÷– ˝æ›”–∏¸–¬£¨RemindReceiver÷–“≤≤ªƒ‹ªÒ»°∏¸–¬∫Ûµƒ ˝æ›£¨∂¯ «“ª÷±ªÒ»°…œ¥Œµƒ ˝æ›£¨ ≥˝∑««Âø’ª∫¥Ê
        SharedPreferences pre = arg0.getSharedPreferences("time", Context.MODE_MULTI_PROCESS);
        advance_time = pre.getInt("time_choice", 30);
        int currentday = ShareMethod.getWeekDay();
//		System.out.println(advance_time);

        Calendar c = Calendar.getInstance();
        //ªÒ»°µ±«∞µƒ ±∫Õ∑÷
        int current_hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int current_minute = c.get(Calendar.MINUTE);

        //∂®“Â“ª∏ˆ±Í÷æŒª£¨”√¿¥≈≈≥˝µÙ÷ÿ∏¥µƒÃ·–—
        boolean flag = true;
        //—≠ª∑≈–∂œµ±ÃÏµƒøŒ«∞Ã·–—
        for(int i=0;i<12;i++){
            if(!(start_time[currentday][i][0]==0 && start_time[currentday][i][1]==0)){
                //Ω´calendarµƒ ±∫Õ∑÷…Ë÷√Œ™Ã·–— ±∫Úµƒ ±∫Õ∑÷
                c.set(Calendar.HOUR_OF_DAY, start_time[currentday][i][0]);
                c.set(Calendar.MINUTE, start_time[currentday][i][1]);
                long remind_time = c.getTimeInMillis()-advance_time*60*1000;
                Date date=new Date(remind_time);
                c.setTime(date);

                //ªÒ»°…Ë÷√µƒÃ·–—µƒ ±∫Õ∑÷
                int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                //»Áπ˚µΩ¡À…Ë∂®µƒÃ·–— ±º‰£¨æÕ∆Ù∂ØÃ·–—µƒactivity
                if(hourOfDay==current_hourOfDay && minute==current_minute){
                    if(flag){
                        arg0.startActivity(remind_intent);
//						System.out.println("time remind" + i);
                        flag = false;
                    }
                }else{
                    flag = true;
                }
            }
        }

    }

}

