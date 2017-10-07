package temp;

/**
 * Created by yiqian on 2017/10/6.
 */

        import java.util.LinkedList;
        import java.util.List;

        import android.app.Activity;
        import android.app.Application;

//MyApplication class save all activities, and close all activities when exit
public class MyApplication extends Application {

    //define activities container
    private List<Activity> activityList = new LinkedList<Activity>();
    private static MyApplication instance;

    private MyApplication(){}


    //get MyApplication instance
    public static MyApplication getInstance(){
        if(instance == null)
            instance = new MyApplication();
        return instance;
    }


    //add activities to container
    public void addActivity(Activity activity){
        activityList.add(activity);
    }


    //visit all instances
    public void exitApp(){
        for(Activity activity : activityList){
            if(activity != null)
                activity.finish();
        }
        System.exit(0);
    }


    //clean cache
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}

