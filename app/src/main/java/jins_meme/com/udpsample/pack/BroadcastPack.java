package jins_meme.com.udpsample.pack;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;

/**
 * Created by furukawanobuyuki on 2017/02/16.
 */

public class BroadcastPack {
    private static final String TAG = BroadcastPack.class.getName();

    byte status;
    byte p;
    byte c1;
    byte c2;

    private ByteArrayOutputStream baoStream;

    public BroadcastPack(){
        baoStream =  new ByteArrayOutputStream();
    }

    public byte getStatus(){
        return this.status;
    };

    public byte getP(){
        return this.p;
    }

    public byte getC1(){
        return this.c1;
    }

    public byte getC2(){
        return this.c2;
    }

    //バッファを追加する
    public void write(byte[] b) throws IOException {
        baoStream.write(b);
        baoStream.flush();
    }

    public boolean parse() throws ParseException {
        boolean result = false;
        ByteBuffer byteBuffer = ByteBuffer.wrap(baoStream.toByteArray());
        //バイトオーダー リトルエンディアン指定して 数値を読み込む場合
        //byteBuffer.order(ByteOrder.BIG_ENDIAN);
        this.status = (byte) byteBuffer.get();
        this.p = (byte) byteBuffer.get();
        this.c1 = (byte) byteBuffer.get();
        this.c2 = (byte) byteBuffer.get();

        Log.d(TAG,String.format("parse %d %d %d %d",this.status,this.p,this.c1,this.c2));
        return true;
    }

    //現在のバッファを配列として返す
    public byte[] toByteArray(){
        if(baoStream.size() == 0){
            baoStream.write(status);
            baoStream.write(p);
            baoStream.write(c1);
            baoStream.write(c2);
            try {
                parse();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return baoStream.toByteArray();
    }
}
