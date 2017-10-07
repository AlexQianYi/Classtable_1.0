package org.editschedule;

/**
 * Created by yiqian on 2017/10/6.
 */

        import org.androidschedule.R;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Service;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.SharedPreferences;
        import android.content.DialogInterface.OnClickListener;
        import android.os.Bundle;
        import android.os.Vibrator;

public class RemindActivity extends Activity
{
    private Vibrator vibrator;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences pre = getSharedPreferences("time", Context.MODE_MULTI_PROCESS);
        int advance_time = pre.getInt("time_choice", 30);

        //ªÒ»°œµÕ≥µƒvibrator∑˛ŒÒ£¨≤¢…Ë÷√ ÷ª˙’Ò∂Ø2√Î
        vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        // ¥¥Ω®“ª∏ˆ∂‘ª∞øÚ
        final AlertDialog.Builder builder= new AlertDialog.Builder(RemindActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Œ¬‹∞Ã· æ");
        builder.setMessage("ÕØ–¨£¨ªπ”–" + advance_time + "∑÷÷”æÕ“™…œøŒ¡À≈∂£°");
        builder.setPositiveButton(
                "»∑∂®" ,
                new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog , int which)
                    {
                        // Ω· ¯∏√Activity
                        RemindActivity.this.finish();
                    }
                }
        )
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Ω· ¯∏√Activity
        RemindActivity.this.finish();
    }


}

