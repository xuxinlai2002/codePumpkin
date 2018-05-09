package android.carrier.net.elastos.p2pNet;

import android.carrier.net.elastos.codepumpkin.Bean.Action;
import android.carrier.net.elastos.codepumpkin.Bean.SysApp;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

public class CarrierExecutor {

    String TAG = "NGD CarrierExecutor";
    private Context context;
    SysApp application;

    public CarrierExecutor(Context context) {
        this.context = context;
        init();
    }


    private void init() {

        application = (SysApp)this.context.getApplicationContext();
//        try{
////            List<FriendInfo> friendInfoList = application.getCarrier().getFriends();
////            application.setFriendID();
//
//
//        }catch (ElastosException e){
//            e.printStackTrace();
//        }
    }

    public String getUserID() {

        String userID = "";
        try {
            userID = application.getCarrier().getUserId();
        } catch (ElastosException e) {
            e.printStackTrace();
        }
        return userID;
    }

    public String getUserAddr() {

        String getUserAddr = "";
        try {
            getUserAddr = application.getCarrier().getAddress();
        } catch (ElastosException e) {
            e.printStackTrace();
        }
        return getUserAddr;
    }

    public String getFriendID() {

        return application.getFriendID();
    }

    public String getFriendAddr() {

        return application.getFriendAddr();
    }


    public void sendMessage(Action action){

        String strMessaage = this.actionToGString(action);
        try {

            Carrier carrierInst = application.getCarrier();
            Log.i(TAG,"start send message");
            carrierInst.sendFriendMessage(application.getFriendID(), strMessaage);
            Log.i(TAG,"end send message");

        }catch (ElastosException e) {
            e.printStackTrace();
        }
    }



    private String actionToGString(Action action){

        String strActionResult = "";
        Gson gson = new Gson();
        String json = gson.toJson(action);
        Log.i("gson",json);

        Action actionResult = gson.fromJson(json,Action.class);
        strActionResult = actionResult.toString();
        Log.i("gson",strActionResult);

        return strActionResult;
    }



}
