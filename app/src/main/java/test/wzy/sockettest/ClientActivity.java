package test.wzy.sockettest;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientActivity extends Activity {


    private EditText mInput;
    private Button mSend, mSet;
    private TextView mIPTv, mText;
    private String mServerIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mIPTv = (TextView) findViewById(R.id.tv_ip);
        mText = (TextView)findViewById(R.id.tv_client_text);
        mInput = (EditText)findViewById(R.id.et_client_input);
        mSend = (Button)findViewById(R.id.btn_client_send);
        mSet = (Button)findViewById(R.id.set_ip);

        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServerIP = mInput.getText().toString();
                mInput.setText("");
                mIPTv.setText("server IP: " + mServerIP);
                mSend.setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);
            }
        });


        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run(){
                        sendText();
                    }
                }.start();
            }
        });
    }

    private void sendText(){
        Socket socket = null;
        try {
            InetAddress serverAddr = InetAddress.getByName(mServerIP);

            socket = new Socket(serverAddr, 51706); // 创建连接

            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);

            // 把用户输入的内容发送给服务端
            String text = mInput.getText().toString();
            mInput.post(new Runnable() {
                @Override
                public void run() {
                    mInput.setText("");
                }
            });
            out.println(text);
            out.flush();


            // 接收服务端反馈
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final String msg = in.readLine();

            mText.post(new Runnable() {
                @Override
                public void run() {
                    if(mText.getLineCount() == 10)
                        mText.setText("");
                    mText.append(msg);
                    mText.append("\n");
                }
            });

        } catch(UnknownHostException e) {
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

}
