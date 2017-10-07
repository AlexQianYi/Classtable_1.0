package org.version;

/**
 * Created by yiqian on 2017/10/6.
 */


        import temp.MyApplication;
        import zyb.org.androidschedule.R;
        import android.app.Activity;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.TextView;

public class VersionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        //Ω´∏√activityº”»ÎµΩMyApplication∂‘œÛ µ¿˝»›∆˜÷–
        MyApplication.getInstance().addActivity(this);

        TextView backButton = (TextView)findViewById(R.id.backtoSetButton);
        //Œ™∑µªÿ∞¥≈•∞Û∂®º‡Ã˝∆˜
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
//				Intent intent = new Intent(AboutUs.this,MainActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//À¢–¬
//				startActivity(intent);
            }
        });
    }
}

