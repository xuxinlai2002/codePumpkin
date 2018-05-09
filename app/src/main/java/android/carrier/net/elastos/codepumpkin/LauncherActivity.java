package android.carrier.net.elastos.codepumpkin;

import android.carrier.net.elastos.codepumpkin.Bean.Action;
import android.carrier.net.elastos.codepumpkin.Bean.SysApp;
import android.carrier.net.elastos.common.NetOptions;
import android.carrier.net.elastos.common.Synchronizer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.gson.Gson;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.io.File;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener{


    private Button btnStart;
    private Button btnMyInfo;
    private Button btnAddFriend;

    //TODO
    private String TAG="xxl LauncherActivity";
    private String AUTO = "auto-accepted";
    private SysApp application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLandscape();
        setContentView(R.layout.launcher);

        initView();
        testGson();
        initCarrierNet();
    }


    private void initView(){
        this.btnStart = (Button)findViewById(R.id.btn_start);
        this.btnMyInfo = (Button)findViewById(R.id.btn_my_info);
        this.btnAddFriend = (Button)findViewById(R.id.btn_add_friend);
        this.btnStart.setOnClickListener(this);
        this.btnMyInfo.setOnClickListener(this);
        this.btnAddFriend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        try {

            String strID ="iYdaECpcRjrnDrZLyDUk77ByNj5Mvvg9evz7L5rr4ch";
            String strAddr  ="2aKDxyxQX2g5mLeZhiNY8Krhn8WN9HMGTmy7tBGGQUWoFdwDzxJR";
            //以下添加事件
            switch (v.getId()){
                case R.id.btn_start:
                    startActivity(new Intent(LauncherActivity.this,MainActivity.class));
                    break;
                case R.id.btn_my_info:
                    //get user info

                    strAddr = application.getCarrier().getAddress();
                    strID = application.getCarrier().getUserId();
                    Log.i(TAG,"on click btn_my_info :" + strAddr + "," + strID);
                    //TODO
                    //create QRCode


                    break;
                case R.id.btn_add_friend:
                    //TODO
                    //get the QRCode Info

                    Log.i(TAG,"on click btn_add_friend :" + strAddr + "," + strID);
                    application.setFriendID(strID);
                    application.setFriendAddr(strAddr);
                    application.getCarrier().addFriend(strAddr,AUTO);




                    break;
            }

        }catch (ElastosException e) {
            e.printStackTrace();
        }

    }

    private void initLandscape() {
        /* 隐藏标题栏 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /* 隐藏状态栏 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /* 设定屏幕显示为横屏 */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void testGson(){

        Action action = new Action("1",1,1);
        Gson gson = new Gson();
        String json = gson.toJson(action);
        Log.i("gson",json);

        Action action1 = gson.fromJson(json,Action.class);
        Log.i("gson",action1.toString());
    }

    private void initCarrierNet(){

        NetOptions options = new NetOptions(getAppPath());
        CarrierHandler handler = new CarrierHandler();

        application = (SysApp)this.getApplicationContext();

        //1.初始化实例，获得相关信息
        try {
            //1.1获得Carrier的实例
            Carrier carrierInst = Carrier.getInstance(options, handler);
            application.setCarrier(carrierInst);

            //1.2获得Carrier的地址
            String carrierAddr = carrierInst.getAddress();
            Log.i(TAG,"address: " + carrierAddr);

            //1.3获得Carrier的用户ID
            String carrierUserID = carrierInst.getUserId();
            Log.i(TAG,"userID: " + carrierUserID);

            //1.4启动网络
            carrierInst.start(1000);
            handler.synch.await();
            Log.i(TAG,"carrier client is ready now");

        } catch (ElastosException e) {
            e.printStackTrace();
        }

    }


    private String getAppPath() {

        Context context=this;
        File file=context.getFilesDir();
        String path=file.getAbsolutePath();
        return path;
    }

    static class CarrierHandler extends AbstractCarrierHandler {

        Synchronizer synch = new Synchronizer();
        String from;
        ConnectionStatus friendStatus;
        String CALLBACK="xxl ";

        public void onReady(Carrier carrier) {
            synch.wakeup();
        }

        public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {

            Log.i(CALLBACK,"friendid:" + friendId + "connection changed to: " + status);
            from = friendId;
            friendStatus = status;
            if (friendStatus == ConnectionStatus.Connected)
                synch.wakeup();
        }

        //2.2 通过好友验证
        public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello) {
            try {

                Log.i(CALLBACK,"onFriendRequest start  " + userId);
                if (hello.equals("auto-accepted")) {
                    carrier.AcceptFriend(userId);
                }

                Log.i(CALLBACK,"onFriendRequest end " + userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        //3.2 接受好友信息
        public void onFriendMessage(Carrier carrier,String fromId, String message) {

            Log.i(CALLBACK,"onFriendMessage :" + fromId + "connection changed to: " + message);


        }
    }


}
