package info.abibas.lazydroid;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MousePosition {

    private static int intervalMs = 100;

    public static boolean allowSend = false;
    public static int x = 0;
    public static int y = 0;
    public static int stepX = 150;
    public static int stepY = 100;


    private static Timer timer;


    public static void setInterval() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run()
            {
                allowSend = true;

            }
        };
        timer.schedule(task, intervalMs ,intervalMs);
    }

    public static void clearInterval() {
        timer.cancel();
        timer.purge();
    }
}
