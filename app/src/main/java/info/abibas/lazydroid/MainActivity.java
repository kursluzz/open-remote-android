package info.abibas.lazydroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //constants
    private static final String DIALOG_TITLE = "Lazy Droid";

    //class variables
    private TextView Text;

    private boolean keyboardOpened = false;
    private ActTable actTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actTable = new ActTable();
        Text = (TextView) findViewById(R.id.text);
        ImageButton VolumeUp = (ImageButton)findViewById(R.id.volume_up);
        ImageButton VolumeDown = (ImageButton)findViewById(R.id.volume_down);
        ImageButton Shutdown = (ImageButton)findViewById(R.id.shutdown);
        ImageView LeftClick = (ImageView)findViewById(R.id.left_click);
        ImageView RightClick = (ImageView)findViewById(R.id.right_click);
        ImageButton Keyboard = (ImageButton)findViewById(R.id.keyboard);
        ImageView Pad = (ImageView)findViewById(R.id.pad);


        //volume up
        VolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAct("press", "volume_up");
            }
        });

        //volume down
        VolumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAct("press", "volume_down");
            }
        });

        //shutdown
        Shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptShutdown();
            }
        });

        //left click
        LeftClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAct("mouse", "left_click");
            }
        });

        //right click
        RightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAct("mouse", "right_click");
            }
        });

        //Keyboard click
        Keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!keyboardOpened) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
                keyboardOpened = true;
            } else {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                keyboardOpened = false;
            }
            }
        });

        //pad touch
        Pad.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        Log.d("MotionEvent", "ACTION_MOVE");
                        sendMouseMove(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("MotionEvent", "ACTION_DOWN");
                        MousePosition.x = (int) event.getX();
                        MousePosition.y = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("MotionEvent", "ACTION_UP");
                        sendMouseMove(event.getX(), event.getY());
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyAction = event.getAction();
        if(keyAction == KeyEvent.ACTION_UP)
        {
            int keyCode = event.getKeyCode();
            String act = actTable.getAct(keyCode);
            if(!act.isEmpty()){
                //key without a char like enter, space, backspace
                sendAct("press", act);
                return false;
            } else {
                //key has a char
                int keyUnicodeCode = event.getUnicodeChar(event.getMetaState());
                if (keyUnicodeCode != 0 && event.getKeyCode() != KeyEvent.KEYCODE_SHIFT_LEFT ) {
                    //not just shift
                    char keyChar = (char) keyUnicodeCode;
                    sendAct("press", String.valueOf(keyChar));
                    return false;
                }
            }
        }
        else if (keyAction == KeyEvent.ACTION_MULTIPLE) {
            out(event.getCharacters());
            sendAct("str", event.getCharacters());
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    private void sendAct(String act, String val) {
        if (!notifyWifiState()) {
            return;
        }
        String[] jsonKeys = {"act", "val"};
        String[] jsonVals = {act, val};
        String jsonStr = jsonEncode(jsonKeys, jsonVals);
        new TcpClient().execute(jsonStr);
    }

    private void sendAct(String act, String val, String jsonData) {
        if (!notifyWifiState()) {
            return;
        }
        String packetStr = "{\"act\":\"" + act + "\",\"val\":\"" + val + "\",\"data\":" + jsonData + "}";
        new TcpClient().execute(packetStr);
    }

    //todo: make it generic
    private void promptShutdown() {
        if (!notifyWifiState()) {
            return;
        }
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        sendAct("system", "shutdown");
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Shutdown the computer?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    private void alert(String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //do some thing here which you need
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void alert(String msg) {
        alert(msg, DIALOG_TITLE);
    }

    private void dialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Deleting a Contact No")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //do some thing here which you need
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean wifiState() {
        WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    private boolean notifyWifiState() {
        //init checks
        if (!wifiState()) {
            toastAlert("Please check if the wifi is on");
            return false;
        }
        return true;
    }

    private void toastAlert(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    public void out(String str) {
        Text.setText(str);
    }

    private String jsonEncode(String[] keys, double[] vals) {
        String result;
        JSONObject data = new JSONObject();
        for (int i=0; i<keys.length; i++) {
            try {
                data.put(keys[i], vals[i]);
            } catch (JSONException e) {
                Log.d("JSONException", "jsonEncode()");
            }
        }
        result = data.toString();
        return result;
    }

    private String jsonEncode(String[] keys, int[] vals) {
        String result;
        JSONObject data = new JSONObject();
        for (int i=0; i<keys.length; i++) {
            try {
                data.put(keys[i], vals[i]);
            } catch (JSONException e) {
                Log.d("JSONException", "jsonEncode()");
            }
        }
        result = data.toString();
        return result;
    }

    private String jsonEncode(String[] keys, String[] vals) {
        String result;
        JSONObject data = new JSONObject();
        for (int i=0; i<keys.length; i++) {
            try {
                data.put(keys[i], vals[i]);
            } catch (JSONException e) {
                Log.d("JSONException", "jsonEncode()");
            }
        }
        result = data.toString();
        return result;
    }

    private String jsonEncode(String key, String val) {
        String[] jsonKey = {key};
        String[] jsonVal = {val};
        return jsonEncode(jsonKey, jsonVal);
    }

    private void sendMouseMove(float fltNewX, float fltNewY) {
        int newX = (int)fltNewX;
        int newY = (int)fltNewY;
        int oldX = MousePosition.x;
        int oldY = MousePosition.y;
        int moveX = newX - oldX;
        int moveY = newY - oldY;
        MousePosition.x = newX;
        MousePosition.y = newY;
        String[] jsonKeys = {"x", "y"};
        int[] jsonVals = {moveX, moveY};
        String jsonData = jsonEncode(jsonKeys, jsonVals);
        sendAct("mouse", "mouse_move", jsonData);

    }

}