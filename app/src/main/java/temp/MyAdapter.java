package temp;

/**
 * Created by yiqian on 2017/10/6.
 */

        import org.MainActivity;
        import org.androidschedule.R;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.support.v4.widget.SimpleCursorAdapter;
        import android.widget.Adapter;
public class MyAdapter {

    private Context context;
    private MainActivity main;
    private Cursor[] cursor=new Cursor[7];
    private SimpleCursorAdapter[] adapter;

    private SharedPreferences preferences;

    public MyAdapter(Context context){
        this.context=context;
        main=(MainActivity) context;
    }
    public void test(){



    }

}

