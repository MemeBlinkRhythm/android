package jins_meme.com.udpsample.pack;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;

/**
 * Created by furukawanobuyuki on 2017/02/04.
 */

public class CmdPack {
    private static final String TAG = CmdPack.class.getName();

    private byte id;

    private byte blink;

    private byte sec;

    private ByteArrayOutputStream baoStream;

    public CmdPack(){
        baoStream =  new ByteArrayOutputStream();
    };


    public byte getId(){
        return this.id;
    }
    public void setId(byte id){
        this.id = id;
    }

    public byte getBlink(){
        return this.blink;
    }
    public void setBlink(byte blink){
        this.blink = blink;
    }

    public byte getSec(){
        return this.sec;
    }
    public void setSec(byte sec){
        this.sec = sec;
    }

    //バッファを追加する
    public void write(byte[] b) throws IOException {
        baoStream.write(b);
        baoStream.flush();
    }

    private boolean parse() throws ParseException {
        boolean result = false;
        ByteBuffer byteBuffer = ByteBuffer.wrap(baoStream.toByteArray());
        //バイトオーダー リトルエンディアン指定して 数値を読み込む場合
        //byteBuffer.order(ByteOrder.BIG_ENDIAN);
        this.id = (byte) byteBuffer.get();
        this.blink = (byte) byteBuffer.get();
        this.sec = (byte) byteBuffer.get();

        Log.d(TAG,String.format("parse %d %d %d",this.id,this.blink,this.sec));
        return true;
    }

    //現在のバッファを配列として返す
    public byte[] toByteArray(){
        if(baoStream.size() == 0){
            baoStream.write(id);
            baoStream.write(blink);
            baoStream.write(sec);
            try {
                parse();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return baoStream.toByteArray();
    }
}
