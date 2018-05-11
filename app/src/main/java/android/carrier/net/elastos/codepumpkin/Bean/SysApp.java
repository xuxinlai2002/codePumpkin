package android.carrier.net.elastos.codepumpkin.Bean;

import android.app.Application;

import org.elastos.carrier.Carrier;

public class SysApp extends Application {


    String friendID = "";
    String friendAddr = "";
    Carrier carrierInst = null;
    //我的用户类型  0 主玩家（左侧）  1 次玩家（右侧）
    int myGameUserType = 0;

    boolean isFirst = true;

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }

    public String getFriendAddr() {
        return friendAddr;
    }

    public void setFriendAddr(String getFriendAddr) {
        this.friendAddr = getFriendAddr;
    }


    public Carrier getCarrier() {
        return carrierInst;
    }

    public void setCarrier(Carrier inst) {
        carrierInst = inst;
    }

    public int getMyGameUserType() {
        return myGameUserType;
    }

    public void setMyGameUserType(int myGameUserType) {
        this.myGameUserType = myGameUserType;
    }
}
