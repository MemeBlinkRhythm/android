package jins_meme.com.udpsample;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.Date;

import jins_meme.com.udpsample.pack.BroadcastPack;
import jins_meme.com.udpsample.pack.CmdPack;
import jins_meme.com.udpsample.util.UDPObjectTransfer;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private Thread receiveThread;

    private Handler handler = new Handler();

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        Button btnParent = (Button) findViewById(R.id.btnParent);
        btnParent.setOnClickListener(clickBtnParent);

        Button btnChild1 = (Button) findViewById(R.id.btnChild1);
        btnChild1.setOnClickListener(clickBtnChild1);

        Button btnChild2 = (Button) findViewById(R.id.btnChild2);
        btnChild2.setOnClickListener(clickBtnChild2);

        //受信処理を開始する
        this.receiveThread = new Thread(receiveThreadWork);
        this.receiveThread.start();
    }

    //送信処理
    private void send(final String address,final int port,final CmdPack pack){
        //送信処理
        AsyncTask<Void,Void,Void> sendAsyncTask = new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                Log.d(TAG,"send");
                try {
                    UDPObjectTransfer.send(pack.toByteArray(), address, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

            }
        };
        sendAsyncTask.execute();
    }

    private View.OnClickListener clickBtnParent = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.d(TAG, "clickBtnParent");

            int s = (int)((new Date()).getTime() %10);
            CmdPack pack = new CmdPack();
            pack.setId((byte) 0); //親
            pack.setBlink((byte) 1);
            pack.setSec((byte)s);

            send("192.168.10.1",2390,pack);
        }
    };

    private View.OnClickListener clickBtnChild1 = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.d(TAG, "clickBtnChild1");

            int s = (int)((new Date()).getTime() %10);
            CmdPack pack = new CmdPack();
            pack.setId((byte) 1); //子1
            pack.setBlink((byte) 1);
            pack.setSec((byte)s);

            send("192.168.10.1",2390,pack);
        }
    };

    private View.OnClickListener clickBtnChild2 = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.d(TAG, "clickBtnChild2");

            int s = (int)((new Date()).getTime() %10);
            CmdPack pack = new CmdPack();
            pack.setId((byte) 2); //子2
            pack.setBlink((byte) 1);
            pack.setSec((byte)s);

            send("192.168.10.1",2390,pack);
        }
    };

    //受信処理
    private Runnable receiveThreadWork = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"receive");
            int port = 2390;      // 送信側と揃える
            int bufferSize = 1024; // 適当なサイズで

            while (true) {
                byte[] byteArry = null; // 受信するまで待機
                try {
                    byteArry = UDPObjectTransfer.receive(port, bufferSize);

                    BroadcastPack pack = new BroadcastPack();
                    pack.write(byteArry);
                    pack.parse();

                    //ここで 判定
                    if(pack.getStatus() == 1){
                        //開始処理を書く
                        Log.d(TAG, "start");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("start");
                            }
                        });
                    }
                    else if(pack.getStatus() == 2){
                        Log.d(TAG, "end");

                        Log.d(TAG, "1" + pack.getP()); // 親 の回数
                        Log.d(TAG, "2" + pack.getC1()); // 子1 の回数
                        Log.d(TAG, "2" + pack.getC2()); // 子2 の回数

                        //TODO 自分の 番号に従った画面を表示する
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("stop");
                            }
                        });
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
