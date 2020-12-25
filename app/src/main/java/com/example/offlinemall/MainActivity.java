package com.example.offlinemall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.offlinemall.utils.SerialPortUtils;

import java.io.FileDescriptor;

import android_serialport_api.SerialPort;

public class MainActivity extends AppCompatActivity {

    /**
     * 注意：在高版本运行时，需要重新用ndk编译一遍，生成新的so文件替换，以免crash
     */


    private final String TAG = "MainActivity";

    private Button button_open;
    private Button button_close;
    private EditText editText_send;
    private Button button_send;
    private TextView textView_status;
    private Button button_status;
    private Spinner spinner_one;

    private SerialPortUtils serialPortUtils = new SerialPortUtils();
    private SerialPort serialPort;

    private Handler handler;
    private byte[] mBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(); //创建主线程的handler  用于更新UI

        button_open = (Button)findViewById(R.id.button_open);
        button_close = (Button)findViewById(R.id.button_close);
        button_send = (Button)findViewById(R.id.button_send);
        editText_send = (EditText)findViewById(R.id.editText_send);
        textView_status = (TextView)findViewById(R.id.textView_status);
        button_status = (Button)findViewById(R.id.button_status);
        spinner_one = (Spinner)findViewById(R.id.spinner_one);

        editText_send.setText("S3");

        button_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //serialPortUtils = new SerialPortUtils();
                serialPort = serialPortUtils.openSerialPort();
                if (serialPort == null){
                    Toast.makeText(MainActivity.this,"串口打开失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                textView_status.setText("串口已打开");
                Toast.makeText(MainActivity.this,"串口已打开",Toast.LENGTH_SHORT).show();

            }
        });
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serialPortUtils.closeSerialPort();
                textView_status.setText("串口已关闭");
                Toast.makeText(MainActivity.this,"串口关闭成功",Toast.LENGTH_SHORT).show();
            }
        });
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serialPortUtils.sendSerialPort(editText_send.getText().toString());
                textView_status.setText("串口发送指令：" + serialPortUtils.data_);
            }
        });
        button_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileDescriptor fileDescriptor = serialPort.mFd;
                String result = fileDescriptor.toString();
                textView_status.setText(result);
            }
        });
        //串口数据监听事件
        serialPortUtils.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mBuffer = buffer;
                handler.post(runnable);
            }

        });


    }

    //开线程更新UI
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                String reviceContent = new String(mBuffer, "GB2312");
                textView_status.setText("数据内容:"+ reviceContent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

}
