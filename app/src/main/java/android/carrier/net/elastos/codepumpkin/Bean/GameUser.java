package android.carrier.net.elastos.codepumpkin.Bean;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import java.util.List;

/**用户玩家*/
public class GameUser {
    /**用户id*/
    private String id;
    /**行动事件*/
    private List<Action> actionList;
    /**开始的位置*/
    private CGPoint startPosition;
    /**当前的方向*/
    private int direction;
    /**吃南瓜的数量*/
    private int pumpkinCount;

    private CCSprite sprite;

    public GameUser() {
    }

    public GameUser(String id, List<Action> actionList, CGPoint startPosition, int direction) {
        this.id = id;
        this.actionList = actionList;
        this.startPosition = startPosition;
        this.direction = direction;
    }

    public CCSprite getSprite() {
        return sprite;
    }

    public void setSprite(CCSprite sprite) {
        this.sprite = sprite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<Action> getActionList() {
        return actionList;
    }

    public void setActionList(List<Action> actionList) {
        this.actionList = actionList;
    }

    public CGPoint getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(CGPoint startPosition) {
        this.startPosition = startPosition;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getPumpkinCount() {
        return pumpkinCount;
    }

    public void setPumpkinCount(int pumpkinCount) {
        this.pumpkinCount = pumpkinCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameUser gameUser = (GameUser) o;

        if (id != gameUser.id) return false;
        if (direction != gameUser.direction) return false;
        if (actionList != null ? !actionList.equals(gameUser.actionList) : gameUser.actionList != null)
            return false;
        return startPosition != null ? startPosition.equals(gameUser.startPosition) : gameUser.startPosition == null;
    }

//    @Override
//    public int hashCode() {
//        int result = id;
//        result = 31 * result + (actionList != null ? actionList.hashCode() : 0);
//        result = 31 * result + (startPosition != null ? startPosition.hashCode() : 0);
//        result = 31 * result + direction;
//        return result;
//    }

    @Override
    public String toString() {
        return "GameUser{" +
                "id=" + id +
                ", actionList=" + actionList +
                ", startPosition=" + startPosition +
                ", direction=" + direction +
                '}';
    }
}


