package android.carrier.net.elastos.codepumpkin.Bean;

/**
 * 游戏控件
 */
public class GameElement {

    private int id;
    /**
     * 类型 1南瓜 2障碍物
     */
    private int type;
    private float x;
    private float y;

    public GameElement() {
    }

    public GameElement(int id, int type, float x, float y) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "GameElement{" +
                "id=" + id +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
