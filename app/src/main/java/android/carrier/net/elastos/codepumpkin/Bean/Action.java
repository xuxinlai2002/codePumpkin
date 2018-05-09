package android.carrier.net.elastos.codepumpkin.Bean;


/**
 * 行动事件
 */
public class Action {
    /**
     * 类型
     * 1、移动 2、旋转
     */
    private int type;

    /***
     * 数据
     * 若 type = 1   value为步数
     * 若 type = 2   value为角度
     */
    private double value;

    /**用户id*/
    private String userId;



    public Action() {
    }


    public Action(int type, float value) {
        this.type = type;
        this.value = value;
    }

    public Action(String userId,int type, float value) {
        this.userId = userId;
        this.type = type;
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }




    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }
}
