package android.carrier.net.elastos.codepumpkin.Bean;

import android.app.Application;

import org.elastos.carrier.Carrier;

public class SysApp extends Application {


    String friendID = "";
    String friendAddr = "";
    Carrier carrierInst = null;

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


}
