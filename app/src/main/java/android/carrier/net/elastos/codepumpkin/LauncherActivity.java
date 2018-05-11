package android.carrier.net.elastos.codepumpkin;


import android.carrier.net.elastos.codepumpkin.Bean.SysApp;
import android.carrier.net.elastos.codepumpkin.common.GameCommon;
import android.carrier.net.elastos.codepumpkin.ui.QcCodeDialog;
import android.carrier.net.elastos.codepumpkin.util.ToastUtil;
import android.carrier.net.elastos.common.NetOptions;
import android.carrier.net.elastos.common.Synchronizer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.io.File;
import java.util.List;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import com.google.zxing.WriterException;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yzq.zxinglibrary.encode.CodeCreator;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener{

    private String friendUserId = "";
    private String friendUserAddress = " ";
   // private String friendUserId = "9U6E5ZkvtW8pVpo8iSDDyoZ4BccFTRKwRUVbLqYrXY6T";
  //  private String friendUserAddress = " KcPRVCGkWdt49w9bpJFRyXySnCxNvDAibyn23rau42fVqNehc4c4";

    private QcCodeDialog dialog;

    private ImageButton btnStart;
    private ImageButton btnMyInfo;
    private ImageButton btnAddFriend;

    //TODO
    private String TAG="C LauncherActivity";
    private String AUTO = "auto-accepted";
    private SysApp application;
  //  private ImageView contentIvWithLogo;

    //
    private int REQUEST_CODE_SCAN = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLandscape();
        setContentView(R.layout.launcher);

        initView();
        //testGson();
        initCarrierNet();

        try {
            application.setFriendID(friendUserId);
            application.setFriendAddr(friendUserAddress);
//            if(!application.getCarrier().getUserId().equals("DATYhHwvqN64ZQHeDkegT8cPn9Qw8wxjLC8LqXhMXfWG")){  //小手机
//                friendUserId = "DATYhHwvqN64ZQHeDkegT8cPn9Qw8wxjLC8LqXhMXfWG";
//                friendUserAddress = "TjZ7kKMfxxPd7wTBBYra7uTUw4pTEFoQ67TXKCgSKDcFLSCH3WTQ";
//
//            }
            List<FriendInfo> list = application.getCarrier().getFriends();
            Log.i(TAG,list.toString());
        } catch (ElastosException e) {
            e.printStackTrace();
        }



    }




    private void initView(){
        this.btnStart = (ImageButton) findViewById(R.id.btn_start);
        this.btnMyInfo = (ImageButton)findViewById(R.id.btn_my_info);
        this.btnAddFriend = (ImageButton)findViewById(R.id.btn_add_friend);
        this.btnStart.setOnClickListener(this);
        this.btnMyInfo.setOnClickListener(this);
        this.btnAddFriend.setOnClickListener(this);
        dialog = new QcCodeDialog(this);
    }

    @Override
    public void onClick(View v) {

        Bitmap bitmap = null;
        try {

            String strID ="";
            String strAddr  ="";
            //以下添加事件
            switch (v.getId()){
                case R.id.btn_start:
                    startActivity(new Intent(LauncherActivity.this,MainActivity.class));
                    break;
                case R.id.btn_my_info:
                    //get user info
                    strAddr = application.getCarrier().getAddress();
                    strID = application.getCarrier().getUserId();

                    String contentEtString = strAddr + "," + strID;
                    Log.i(TAG,"on click btn_my_info :" + strAddr.length() + "," + strID.length());

                    bitmap = null;
                    try {
                        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                        bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, logo);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {

                        //TODO need to add in a dialog???
                      //  contentIvWithLogo.setImageBitmap(bitmap);
                        dialog.showDialog(bitmap);

                        application.setMyGameUserType(0);
                        ToastUtil.showLong(this,"我的二维码");

                    }

                    break;
                case R.id.btn_add_friend:

                    AndPermission.with(this)
                            .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                    Intent intent = new Intent(LauncherActivity.this, CaptureActivity.class);

                                    /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                                     * 也可以不传这个参数
                                     * 不传的话  默认都为默认不震动  其他都为true
                                     * */

                                    ZxingConfig config = new ZxingConfig();
                                    config.setPlayBeep(true);
                                    config.setShake(true);
                                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);

                                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                                }
                            })
                            .onDenied(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                    Uri packageURI = Uri.parse("package:" + getPackageName());
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    startActivity(intent);

                                    Toast.makeText(LauncherActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                                }
                            }).start();

                    break;
            }

        }catch (ElastosException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.i(TAG,"on click onActivityResult start ");
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String[] content = data.getStringExtra(Constant.CODED_CONTENT).split(",");
                Log.i(TAG,"onActivityResult 扫描结果为 :" + content);

                //purse content 中间 52位为逗号
                //friendUserAddress = content.substring(0,51);
                //friendUserId = content.substring(53);
                friendUserAddress = content[0];
                friendUserId = content[1];

                Log.i(TAG,"friendAddress :" + friendUserAddress);
                Log.i(TAG,"friendUserId :" + friendUserId);

                application.setFriendID(friendUserId);
                application.setFriendAddr(friendUserAddress);

                application.setMyGameUserType(1);

                try {
                    String UserID = application.getCarrier().getUserId();
                    if(application.getCarrier().isFriend(friendUserId)){
                        application.getCarrier().removeFriend(friendUserId);
                    }
                    if(!Carrier.isValidAddress(friendUserAddress)){
                        ToastUtil.showLong(LauncherActivity.this,"不正确的地址,请扫描正确的二维码");
                    }else{
                        application.getCarrier().addFriend(friendUserAddress,UserID);
                        ToastUtil.showLong(LauncherActivity.this,"添加好友成功");
                    }

                } catch (ElastosException e) {
                    e.printStackTrace();
                }
            }
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



//    private void testGson(){

//        Action action = new Action("1",1,1);
//        Gson gson = new Gson();
//        String json = gson.toJson(action);
//        Log.i("gson",json);
//
//        Action action1 = gson.fromJson(json,Action.class);
//        Log.i("gson",action1.toString());
//    }

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

     class CarrierHandler extends AbstractCarrierHandler {

        Synchronizer synch = new Synchronizer();
        String from;
        ConnectionStatus friendStatus;
        String CALLBACK="xxl ";


        public void onReady(Carrier carrier) {
            Log.i(TAG,"onReady"+System.currentTimeMillis());
            synch.wakeup();
        }

        public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {

            Log.i(CALLBACK,"friendid:" + friendId + "connection changed to: " + status);
            from = friendId;
            friendStatus = status;
            if (friendStatus == ConnectionStatus.Connected){
                application.setFriendID(friendId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showLong(LauncherActivity.this,"添加成功");
                        if(dialog.isShowing()){
                            dialog.cancel();
                        }
                    }
                });

                synch.wakeup();

            }
        }

         @Override
         public void onFriendAdded(Carrier carrier, FriendInfo info) {
             Log.i(TAG,"onFriendAdded"+System.currentTimeMillis());
             super.onFriendAdded(carrier, info);
         }

         //2.2 通过好友验证
        public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String userAddr) {
            try {

//                if (hello.equals("auto-accepted")) {
//                    carrier.AcceptFriend(userId);
//                }
                Log.i(TAG,"收到好友请求"+userId);
                carrier.AcceptFriend(userId);
                application.setFriendID(userId);
                application.setFriendAddr(userAddr);
                ToastUtil.showLong(LauncherActivity.this,"添加成功");
                dialog.cancel();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        //3.2 接受好友信息
        public void onFriendMessage(Carrier carrier,String fromId, String message) {

            Log.i(CALLBACK,"address:" + fromId + "connection changed to: " + message);
            Intent intent = new Intent();
            intent.setAction(GameCommon.ACTION_MESSAGE);
            intent.putExtra(GameCommon.ACTION_VALUE,message);
            LauncherActivity.this.sendBroadcast(intent);

        }
    }


}
