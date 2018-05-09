package android.carrier.net.elastos.codepumpkin.util;

import android.carrier.net.elastos.codepumpkin.Bean.GameUser;
import android.carrier.net.elastos.codepumpkin.common.GameCommon;
import android.util.Log;

import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.nodes.CCTextureNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import java.util.Random;

public class SpriteUtil {

    public static CGSize boxSize;


    public static Random rand = new Random();


    public static CGPoint cratePoint(double x, double y) {
        CGPoint p = new CGPoint();
        p.set((float) x, (float) y);
        return p;
    }

    /***
     * 创建步数的字符
     * @param text
     * @param point
     * @param value
     * @return
     */
    public static CCLabel createStepText(String text, CGPoint point, float value) {

        CCLabel label = CCLabel.labelWithString(text, "font/consola.ttf", GameCommon.DEFAULT_FONT_SIZE);
        label.setColor(ccColor3B.ccBLACK);
        label.setPosition(point);
        label.setUserData(value);
        return label;
    }

    /***
     * 创建玩家
     * @param
     * @param
     * @return
     */
    public static CCSprite createGameUser(int nType) {
        String picPath = "";
        float nS;
        if(nType == 0){
            nS = 0 - GameCommon.DEFAULT_SIZE ;
            picPath = "dragon-head.png";
        }else{
            nS = GameCommon.DEFAULT_SIZE;
            picPath = "dragon-head2.png";
        }
        CCSprite s = new CCSprite(picPath);
        s.setPosition(cratePoint(boxSize.getWidth() / 2 + nS, s.getContentSize().getHeight()));
        //s.setAnchorPoint(CGPoint.getZero());
        s.setScale(1.2);
        return s;

    }

    public static CCSprite createPumpkin() {
        CCSprite s = new CCSprite("pumpkin.png");
        s.setUserData(1);    // 1 存在  0已经吃掉
        //s.setAnchorPoint(CGPoint.getZero());
        return s;
    }

    public static CCSprite createBush() {
        CCSprite s = new CCSprite("bush.png");
        //s.setAnchorPoint(CGPoint.getZero());
        return s;
    }

    public static CGPoint randomPosition() {
        return cratePoint(rand.nextInt((int) (boxSize.getWidth() - 200) + 100),
                rand.nextInt((int) (boxSize.getHeight() - 300)) + 200);
//        CGPoint p;
//        while (true) {
//            p = cratePoint( rand.nextInt((int)(boxSize.getWidth()-view.getContentSize().getWidth())) + 100,
//                    rand.nextInt((int)(boxSize.getHeight()-view.getContentSize().getHeight())) + 200);
//            view.setPosition(p);
//            // cc.log("random:"+JSON.stringify(p));
//            if (checkPosition(view)) {
//                break;
//            }
//        }
//        return p;
    }




    /**
     * 判断点面是否重合
     */
    public static boolean isContainsPointByView(CCSprite view, CGPoint p) {
        Log.i("point", view.getBoundingBox().toString());
        Log.i("point", p.toString());
        return view.getBoundingBox().contains(p.x, p.y);
    }

    /**
     * 单位换算
     */
    public static CGPoint converPx(float x, float y) {
        return cratePoint(GameCommon.gameWidth / GameCommon.deviceWidth * x, GameCommon.gameHeight / GameCommon.deviceHeight * y);
    }

    ;

    /**
     * 判断面面是否重合
     */
    public static boolean isContainsRect(CCSprite view1, CCSprite view2) {
        //view1.getBoundingBox()
//        bool Rect::intersectsRect(const Rect& rect) const
////        {
////            return !(     getMaxX() < rect.getMinX() ||
////                    rect.getMaxX() <      getMinX() ||
////                    getMaxY() < rect.getMinY() ||
////                    rect.getMaxY() <      getMinY());
////        }
//        view1.getBoundingBox().contains()
        // view1.getBoundingBox().origin.x+view1.getBoundingBox().size.width < view2.getBoundingBox().origin.x
        Log.i("rect",view1.getBoundingBox().toString()+","+view2.getBoundingBox().toString());
        return CGRect.intersects(view1.getBoundingBox(), view2.getBoundingBox());
       // return CGRect.containsRect(view1.getBoundingBox(), view2.getBoundingBox());
    }
}
