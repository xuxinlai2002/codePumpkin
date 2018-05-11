package android.carrier.net.elastos.codepumpkin;

import android.carrier.net.elastos.codepumpkin.Bean.Action;
import android.carrier.net.elastos.codepumpkin.common.GameCommon;
import android.carrier.net.elastos.codepumpkin.layer.GameCCLayer;
import android.carrier.net.elastos.codepumpkin.layer.LauncherCCLayer;
import android.carrier.net.elastos.codepumpkin.ui.StepDialog;
import android.carrier.net.elastos.codepumpkin.ui.adapter.DialogAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private CCScene launcherScene;
    private CCScene gameScene;

    private ActionReceiver actionReceiver;

    private GameCCLayer gameCCLayer;

    private Gson gson;

    public StepDialog dialog;
    public DialogAdapter adapter;

    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        initLandscape();
        getDisplayInfo();
        initCocos();


        launcherScene = CCScene.node();  //启动页面场景
        gameScene = CCScene.node();
        launcherScene.addChild(new LauncherCCLayer()); //将MyCCLayer层加到场景里
        gameCCLayer = new GameCCLayer(this);
        gameScene.addChild(gameCCLayer); //将MyCCLayer层加到场景里
        CCDirector.sharedDirector().runWithScene(gameScene);// 运行场景

        gson = new Gson();
        createAndBindBroadcast();

        initDialog();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.good_zh);
        mediaPlayer.setLooping(false);//设置为循环播放
        //    mediaPlayer.prepareAsync();
    }


    /**播放音乐*/
    public void playAudio(){
       // mediaPlayer.reset();
        mediaPlayer.start();
    }

    private void initDialog() {
        adapter =  new DialogAdapter(this, null);
        dialog = new StepDialog(this,adapter);

    }


    private void test() {
        Intent intent = new Intent();
        intent.setAction(GameCommon.ACTION_MESSAGE);
        Action a = new Action("CicPM5B3Hfr4GdqqDy3zYkqR59gE1G9WJYt2Bygy9iZd", 1, 60);
        intent.putExtra(GameCommon.ACTION_VALUE, gson.toJson(a));
        this.sendBroadcast(intent);
    }


    private void createAndBindBroadcast() {
        actionReceiver = new ActionReceiver();
        // 1. 实例化BroadcastReceiver子类 &  IntentFilter
        IntentFilter intentFilter = new IntentFilter();

        // 2. 设置接收广播的类型
        intentFilter.addAction(GameCommon.ACTION_MESSAGE);
        registerReceiver(actionReceiver, intentFilter);
    }

    private void getDisplayInfo() {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        GameCommon.deviceWidth = wm.getDefaultDisplay().getWidth();
        GameCommon.deviceHeight = wm.getDefaultDisplay().getHeight();
        Log.i("device", GameCommon.deviceWidth + "," + GameCommon.deviceHeight);
    }


    private void initCocos() {
        CCGLSurfaceView ccglSurfaceView = new CCGLSurfaceView(this);//cocos2d提供的SurfaceView

        setContentView(ccglSurfaceView);//设置Activity显示的view

        CCDirector.sharedDirector().attachInView(ccglSurfaceView);
        // CCDirector 导演，负责管理和切换场景，负责初始化OPENGL的各项参数
        // CCDirector 采用单例模式，通过sharedDirector()方法获取其唯一的实例
        // attachInView 与OpenGL的SurfaceView进行连接，意思是将surfaceView交给cocos2d来管理

        CCDirector.sharedDirector().setDisplayFPS(true);
        // 显示fps，需要添加fps_images.png到assets中，否则会报nullpointer，在CCDirector源码中可以看出

        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);// 设置帧率 60帧每秒，人的肉眼识别帧率为60，所以设置为60最为合理

        CCDirector.sharedDirector().setDeviceOrientation(
                CCDirector.kCCDeviceOrientationPortrait);// 设置为横屏显示

        CCDirector.sharedDirector().setScreenSize(GameCommon.gameWidth, GameCommon.gameHeight);
        // 设置横屏后的分辨率,其实是将屏幕横竖分成800和480个小块
    }

    private void initLandscape() {
        /* 隐藏标题栏 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /* 隐藏状态栏 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /* 设定屏幕显示为横屏 */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        CCDirector.sharedDirector().resume();
        //恢复游戏运行
        // cocos2d提供3个生命周期方法，对应android的三个生命周期
    }

    @Override
    protected void onPause() {
        super.onPause();
        CCDirector.sharedDirector().onPause();
        //暂停，游戏切出时候调用
    }

    @Override
    protected void onDestroy() {
        CCDirector.sharedDirector().end();
        unregisterReceiver(actionReceiver);
        if (mediaPlayer != null) {
            mediaPlayer.stop();

            mediaPlayer.reset();

            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();

        // 结束，游戏退出时调用
    }

    class ActionReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(GameCommon.ACTION_VALUE);
            Log.i("info", message);

            try {
                JsonReader reader = new JsonReader(new StringReader(message));
                reader.setLenient(true);
                Action action = gson.fromJson(reader, Action.class);
                gameCCLayer.actionHandler(action);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


}
