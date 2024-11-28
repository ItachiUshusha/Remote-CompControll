package com.example.compcontroll;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    String ip;
    int port = 10000;
    String msg;
    TextView text;
    Button but;
    Button power;
    Socket s = null;
    List<String> hosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.textView);

        but = findViewById(R.id.button);

        power = findViewById(R.id.powerOff);

        but.setOnClickListener(view -> onClick());
        power.setOnClickListener(view -> onPower());

    }

    protected void onClick(){
        //Enter your ip
        ip="192.168.0";
        msg = "Hello from android";
        new InetScanner().execute();
    }

    protected void onPower(){
        msg = "Power off";
        new Chat().execute();
    }

    class InetScanner extends AsyncTask<Void, String, Void>
    {
        @Override
        protected Void doInBackground(Void... params){
            runOnUiThread(() -> text.setText("Дождитесь окончания сканирования..."));
            try {
                int timeout = 5000;
                String host = "";
                for(int i = 100; i < 115; i++){
                    host = ip + "." + i;
                    InetAddress inetAddress = InetAddress.getByName(host);
                    if(inetAddress.isReachable(timeout)){
                        hosts.add(host);
                    }
                }
                send(hosts);
            } catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }



        protected void send(List<String> ipAddresses){
            for (String ip : ipAddresses) {
                try {
                    s = new Socket(ip, port);
                    new Chat().execute();
                    return;
                } catch (IOException ex) {

                }
            }
        }
    }

    class Chat extends AsyncTask<Void, String, Void>{
        @Override
        protected Void doInBackground(Void... params){
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String message;


            message = msg;

            out.println(message);
            out.flush();

            String serverResponse = null;
            try {
                serverResponse = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            publishProgress(serverResponse);
            return null;
        }
        @Override
        protected void onProgressUpdate(String ... values){
            super.onProgressUpdate(values);
            String serverResponse = values[0];
            text.setText(serverResponse);
        }
    }
}

