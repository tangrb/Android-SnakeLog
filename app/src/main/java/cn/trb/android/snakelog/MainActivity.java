package cn.trb.android.snakelog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnakeLog.setPrintSimpleClassName(true);
        SnakeLog.setPrintLogLevel(true, true, true, true, true);

        try {
            SnakeLog.setSaveLogLevel(true, true, true, true, true);
            String path = getFilesDir().getAbsolutePath() + "/test_log_demo.txt";
            SnakeLog.startSaveLog(path, "UTF-8");
        } catch (Exception e) {
            SnakeLog.e(e);
        }

        SnakeLog.v("demo log");
        SnakeLog.d("demo log");
        SnakeLog.i("demo log");
        SnakeLog.w("demo log");
        SnakeLog.e("demo log");

        SnakeLog.w(new Exception("demo log"));
        SnakeLog.e(new Exception("demo log"));

        SnakeLog.w("demo log", new Exception("demo log"));
        SnakeLog.e("demo log", new Exception("demo log"));
    }

    @Override
    protected void onDestroy() {
        SnakeLog.stopSaveLog();
        super.onDestroy();
    }
}
