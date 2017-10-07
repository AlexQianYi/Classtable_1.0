package temp;

/**
 * Created by yiqian on 2017/10/6.
 */

        import java.util.Calendar;
        import java.util.Date;

public class ShareMethod {

    //get what day of the week it is
    //return type 0:Sunday, 1:Monday, 2:Tuesday, 3:Wednesday, 4:Thursday, 5:Friday. 6:Saturday
    public static int getWeekDay(){
        Calendar calendar=Calendar.getInstance();
        Date date=new Date(System.currentTimeMillis());
        calendar.setTime(date);
        int weekDay=calendar.get(Calendar.DAY_OF_WEEK)-1;
        return weekDay;
    }

    //get current time
    //return type: 00:00
    public static String getTime(){
        Calendar c=Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        //get standard type time
        //add 0 if only one digital
        StringBuffer s_hour = new StringBuffer();
        StringBuffer s_minute = new StringBuffer();
        s_hour.append(hourOfDay);
        s_minute.append(minute);
        if(hourOfDay<10){
            s_hour.insert(0,"0");
        }
        if(minute<10){
            s_minute.insert(0,"0");
        }
        return s_hour.toString() + ":" + s_minute.toString();
    }

}

