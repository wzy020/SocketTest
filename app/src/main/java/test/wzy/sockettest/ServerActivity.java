package test.wzy.sockettest;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends Activity {

    private String mServerIP;
    private static final int SERVERPORT = 51706;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        Intent intent = getIntent();
        mServerIP = intent.getStringExtra("ip_addr");

        TextView ipTextVIew = (TextView) findViewById(R.id.tv_server_ip);
        ipTextVIew.setText(mServerIP);

        mText = (TextView)findViewById(R.id.tv_server_text);
    }





    @Override
    protected void onResume() {
        super.onResume();

        Thread desktopServerThread = new Thread(new Server());
        desktopServerThread.start();
    }


    public class Server implements Runnable {
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(SERVERPORT);
                while (true) {
                    Socket client = serverSocket.accept(); // 等待连接
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        final String str = in.readLine(); // 读取客户端的信息
                        if (str != null ) {
                            mText.post(new Runnable() {
                                @Override
                                public void run() {
                                    mText.setText(str);
                                }
                            });

                            // 反馈信息给客户端
                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
                            out.println("You sent to server message is:" + str);
                            out.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        client.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
