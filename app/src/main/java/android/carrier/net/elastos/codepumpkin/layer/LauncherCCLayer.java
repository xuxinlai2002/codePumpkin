package android.carrier.net.elastos.codepumpkin.layer;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

/***
 * 启动页
 */
public class LauncherCCLayer extends CCLayer {

    private CCSprite spriteBg = null;
    private CCSprite spriteLogo = null;
    private CCSprite spriteTitle = null;

    public LauncherCCLayer() {
        init();
    }

    private void init() {

        spriteBg = CCSprite.sprite("bg.png");
        // CCSprite游戏精灵类，需要加载一张图片代表游戏精灵

        spriteBg.setAnchorPoint(CGPoint.getZero());
        // 设置精灵的锚点
        // 锚点是设置在屏幕上显示的位置，原点为自身左下角为准，锚点的值乘以被设置锚点的元素宽或高，为移动的距离

        spriteBg.setPosition(CGPoint.getZero());
        // 设置精灵的坐标，以屏幕的左下角为原点，向右，向上为正方向,属于OpenGL坐标系

        this.addChild(spriteBg, 0, 0);
        // this指代当前对象，即MyCCLayer
        // 给当前图层添加一个子元素
        // 参数1：子元素对象，参数2：子元素重要性，参数3：子元素的标签(可以通过标签取出该元素)

        spriteLogo = CCSprite.sprite("logo.png");
        spriteTitle = CCSprite.sprite("title.png");
        //logoSprite.setAnchorPoint(CGPoint.getZero());
        //titleSprite.setAnchorPoint(CGPoint.getZero());


        //sprite_helloword.setAnchorPoint(CGPoint.getZero());
        spriteLogo.setPosition(
                (float)((this.getContentSize().width) / 2),
                (float)((this.getContentSize().height) * 0.5f));
        spriteTitle.setPosition(
                (float)((this.getContentSize().width) / 2),
                (float)((this.getContentSize().height) * 0.1f));

        spriteLogo.setScale(0.7f);
        spriteTitle.setScale(0.6f);

        this.addChild(spriteLogo, 1, 1);
        this.addChild(spriteTitle, 1, 2);

    }

}