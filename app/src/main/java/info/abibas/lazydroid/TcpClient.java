package info.abibas.lazydroid;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient extends AsyncTask <String, String, String>{

    private String ip = "192.168.0.101";
    private int port = 2208;
    private Socket socket;
    private OutputStream out;
    private PrintWriter output;

    @Override
    protected String doInBackground(String... str) {
        Log.d("general", str[0]);
        Log.d("general", "AsyncTask::doInBackground");
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        output = new PrintWriter(out);

        send(str[0]);

        try {
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void send(String message) {
        Log.d("general", "Sending Data to PC");
        Log.d("general", "socket.isConnected()" + socket.isConnected());
        Log.d("general", "socket.isClosed()" + socket.isClosed());
        Log.d("general", "socket.isBound()" + socket.isBound());
        output.println(message);
        output.flush();
        output.close();
        Log.d("general", "Data sent to PC");
    }
    public void disconnect() throws IOException {
        socket.close();
        Log.d("general", "Socket closed");
    }
}
