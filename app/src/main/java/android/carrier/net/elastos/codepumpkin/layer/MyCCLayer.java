package android.carrier.net.elastos.codepumpkin.layer;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

public class MyCCLayer extends CCLayer {
    private CCSprite sprite_game_bg = null;// 游戏背景精灵
    private CCSprite sprite_helloword = null;// helloworld精灵

    public MyCCLayer() {
        init();
    }

    private void init() {
        sprite_game_bg = CCSprite.sprite("bg.png");
        // CCSprite游戏精灵类，需要加载一张图片代表游戏精灵

        sprite_game_bg.setAnchorPoint(CGPoint.getZero());
        // 设置精灵的锚点
        // 锚点是设置在屏幕上显示的位置，原点为自身左下角为准，锚点的值乘以被设置锚点的元素宽或高，为移动的距离

        sprite_game_bg.setPosition(CGPoint.getZero());
        // 设置精灵的坐标，以屏幕的左下角为原点，向右，向上为正方向,属于OpenGL坐标系

        this.addChild(sprite_game_bg, 0, 0);
        // this指代当前对象，即MyCCLayer
        // 给当前图层添加一个子元素
        // 参数1：子元素对象，参数2：子元素重要性，参数3：子元素的标签(可以通过标签取出该元素)

        sprite_helloword = CCSprite.sprite("helloworld.png");
        sprite_helloword.setAnchorPoint(CGPoint.getZero());
        sprite_helloword.setPosition(
                (this.getContentSize().width - sprite_helloword
                        .getContentSize().width) / 2,
                (this.getContentSize().height - sprite_helloword
                        .getContentSize().height) / 2);
        // 让其显示最屏幕正中间
        this.addChild(sprite_helloword, 1, 1);
        // sprite_helloword精灵的重要性为1，而sprite_game_bg的重要性为0
        // 所以sprite_helloword会显示在sprite_game_bg的上方
        // 换言之就是重要性越高，越优先显示在上层
    }

}