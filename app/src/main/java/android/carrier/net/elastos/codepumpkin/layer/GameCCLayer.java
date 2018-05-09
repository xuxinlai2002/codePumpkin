package android.carrier.net.elastos.codepumpkin.layer;

import android.carrier.net.elastos.codepumpkin.Bean.Action;
import android.carrier.net.elastos.codepumpkin.Bean.GameUser;
import android.carrier.net.elastos.codepumpkin.Bean.SysApp;
import android.carrier.net.elastos.codepumpkin.MainActivity;
import android.carrier.net.elastos.codepumpkin.common.GameCommon;
import android.carrier.net.elastos.codepumpkin.util.SpriteUtil;
import android.carrier.net.elastos.codepumpkin.util.ToastUtil;
import android.carrier.net.elastos.p2pNet.CarrierExecutor;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.elastos.carrier.Carrier;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

public class GameCCLayer extends CCLayer {

    private Context context;

    private CCSprite spriteBg;
    private CCSprite spriteGrad;
    private CCSprite spriteStep;
    private CCSprite spriteTurn;
    private CCSprite spriteLeft;
    private CCSprite spriteRight;
    private CCSprite spriteStepBox;

    private CCLabel spriteStep1;
    private CCLabel spriteStep2;


    private List<CCSprite> bushList = new ArrayList<CCSprite>();
    private List<CCSprite> pumpkinList = new ArrayList<>();
    private List<GameUser> gameUserList = new ArrayList<GameUser>();
    private List<CCLabel> stepPromptList = new ArrayList<CCLabel>();

    private List<Action> actionList = new ArrayList<>();
    private int actionIndex = 0;
    private String currentUser = "";


    private CarrierExecutor carrierExecutorInst = null;

    /**
     * 現在是否有任務正在進行
     */
    private boolean loopWait = false;
    /**
     * 是否为重播状态
     */
    private boolean isRePlay = false;

    public GameCCLayer(Context context) {
        this.context = context;
        init();
    }


    private void init() {
        isTouchEnabled_ = true;
        SpriteUtil.boxSize = this.getContentSize();
        initBg();

        carrierExecutorInst = new CarrierExecutor(this.context);
        currentUser = carrierExecutorInst.getUserID();

        initCtrlView();
        initView();


        //add globel info
        //schedule("actionLoop", 0.1f);
        //this.context
        // scheduleUpdate();
    }

//    public void update(float dt){
//        actionLoop();
//    }

    /**
     * 重新播放事件
     */
    public void rePlay() {
        // 回到开始、轮询事件
        actionIndex = 0;
        loopWait = false;
        isRePlay = true;
        this.initView();
        actionLoop();
    }

    /**
     * 触摸事件
     */
    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        CGPoint p = CCDirector.sharedDirector().convertToGL(SpriteUtil.cratePoint(event.getX(), event.getY()));
        //CGPoint p = SpriteUtil.converPx(event.getX(),event.getY());

        Log.i("action", p.toString());

        //左转
        if (SpriteUtil.isContainsPointByView(spriteLeft, p)) {
            actionHandler(new Action(currentUser, GameCommon.ACTION_ROTA, -90));
            return false;
        }

        //右转
        if (SpriteUtil.isContainsPointByView(spriteRight, p)) {
            //actionHandler(new Action(currentUser, GameCommon.ACTION_ROTA, 90));
            actionHandler(new Action(currentUser, GameCommon.ACTION_ROTA, 90));
            return false;
        }

        // -60
        if (SpriteUtil.isContainsPointByView(spriteStep1, p)) {
            actionHandler(new Action(currentUser, GameCommon.ACTION_MOVE, 0 - GameCommon.DEFAULT_SIZE));
            return false;
        }

        // +60
        if (SpriteUtil.isContainsPointByView(spriteStep2, p)) {
            actionHandler(new Action(currentUser, GameCommon.ACTION_MOVE, GameCommon.DEFAULT_SIZE));
            return false;
        }

        //步数提示
        for (int i = 0; i < stepPromptList.size(); i++) {
            if (SpriteUtil.isContainsPointByView(stepPromptList.get(i), p)) {
                actionHandler(new Action(currentUser, GameCommon.ACTION_MOVE, (float) (stepPromptList.get(i).getUserData())));

            }
        }

        //重放
        if (SpriteUtil.isContainsPointByView(spriteGrad, p)) {
            rePlay();
        }

        return super.ccTouchesBegan(event);
    }

    /**
     * 事件处理
     */
    public void actionHandler(Action action) {
        //添加到任务队列
        actionList.add(action);
        if (!loopWait) {       // 如果没有任务则执行loop
            actionLoop();
        }
    }

    public void actionLoop() {
        Log.i("loop", "-----------------------------------" + actionIndex);
        if (actionIndex < actionList.size()) {
            Action action = actionList.get(actionIndex);
            GameUser user = gameUserList.get(this.getUserIndex(action.getUserId()));

            loopWait = true;        // 开启等待

            switch (action.getType()) {
                case GameCommon.ACTION_MOVE:
                    user.getSprite().runAction(CCSequence.actions(
                            createMoveAction(user, action.getValue()), CCCallFunc.actionWithTarget(GameCCLayer.this, "actionCall")
                    ));
                    break;
                case GameCommon.ACTION_ROTA:
                    user.getSprite().runAction(CCSequence.actions(
                            createRotaAction(user, (float)( action.getValue())), CCCallFunc.actionWithTarget(GameCCLayer.this, "actionCall")
                    ));
                    break;
            }


            if(!isRePlay && !action.getUserId().equals(carrierExecutorInst.getFriendID())){
                carrierExecutorInst.sendMessage(action);
            }

        }


    }



    private int getUserIndex(String userId){

        int nRet = 0;
        if(!userId.equals(gameUserList.get(1).getId())) {
            nRet = 1;
        }
//        } if(!userId.equals(carrierExecutorInst.getUserID())){
//            nRet = 1;
//        }

        return nRet;
    }
    /**
     * 回调
     */
    public void actionCall() {
        Log.i("call", actionIndex + "");
        actionIndex++;
        updateView();
        loopWait = false; //关闭等待
        actionLoop();

    }

    /**
     * 刷新
     */
    public void updateView() {


        //   schedule();
        int nUserSize = gameUserList.size();
        //遍历吃南瓜
        for (int j = 0; j < pumpkinList.size(); j++) {

            for(int i = 0 ;i < nUserSize ;i ++){
                if (SpriteUtil.isContainsRect(gameUserList.get(i).getSprite(), pumpkinList.get(j))) {
                    pumpkinList.get(j).setUserData(0);      //吃掉
                    pumpkinList.get(j).setOpacity(0);
                    gameUserList.get(i).setPumpkinCount(gameUserList.get(i).getPumpkinCount() + 1);
                    if (gameUserList.get(i).getPumpkinCount() > GameCommon.PUMPKIN_COUNT / 2) {
                        ToastUtil.showLong(context, "游戏胜利");
                    } else {
                        ToastUtil.showLong(context, "吃掉南瓜");
                    }
                }
            }
        }
        //遍历障碍物
        for (int j = 0; j < bushList.size(); j++) {

            for(int i = 0 ;i < nUserSize ;i ++) {
                if (SpriteUtil.isContainsRect(gameUserList.get(i).getSprite(), bushList.get(j))) {
                    ToastUtil.showLong(context, "游戏结束");

                }
            }
        }

        createStepPrompt();
    }


    /***
     * 创建移动事件
     * @param gameUser
     * @param step
     * @return
     */
    public CCMoveBy createMoveAction(GameUser gameUser, double step) {
        CCMoveBy moveAction = null;
        float duration = (Math.abs((float) step) / GameCommon.DEFAULT_SIZE) * GameCommon.DEFAULT_TIME;
        switch (gameUser.getDirection()) {        //方向  上、右、下、左
            case GameCommon.DIRECTION_UP:
            default:
                moveAction = CCMoveBy.action(duration, SpriteUtil.cratePoint(0, step));
                break;
            case GameCommon.DIRECTION_RIGHT:
                moveAction = CCMoveBy.action(duration, SpriteUtil.cratePoint(step, 0));
                break;
            case GameCommon.DIRECTION_DOWN:
                moveAction = CCMoveBy.action(duration, SpriteUtil.cratePoint(0, 0 - step));
                break;
            case GameCommon.DIRECTION_LEFT:
                moveAction = CCMoveBy.action(duration, SpriteUtil.cratePoint(0 - step, 0));
                break;

        }

        return moveAction;
    }

    /***
     * 创建旋转事件
     * @param gameUser
     * @param
     * @return
     */
    public CCRotateBy createRotaAction(GameUser gameUser, float value) {
        if (value < 0) {        // 左转
            if (gameUser.getDirection() > 1) {
                gameUser.setDirection(gameUser.getDirection() - 1);
            } else {
                gameUser.setDirection(4);

            }
        } else {
            if (gameUser.getDirection() < 4) {
                gameUser.setDirection(gameUser.getDirection() + 1);
            } else {
                gameUser.setDirection(1);
            }
        }

        return CCRotateBy.action(GameCommon.DEFAULT_TIME, value);

//        if (type == 1) {
//            // if (acc.direction > 1) {
//            //     acc.direction--;
//            // } else {
//            //     acc.direction = 4;
//            // }
//
//            //  acc.setRotation(-90);
//        } else {
//            // if (acc.direction < 4) {
//            //     acc.direction++;
//            // } else {
//            //     acc.direction = 1;
//            // }
//            //acc.setRotation(90);
//            return CCRotateBy.action(GameCommon.DEFAULT_TIME,value);
//
//        }


    }


    /**
     * 初始化背景
     */
    private void initBg() {
        spriteBg = CCSprite.sprite("grass-big.jpg");
        spriteBg.setAnchorPoint(CGPoint.getZero());
        spriteBg.setPosition(CGPoint.getZero());
        this.addChild(spriteBg, 0, 0);

        spriteGrad = CCSprite.sprite("dragon-body.png");
        spriteGrad.setPosition((float) (spriteGrad.getContentSize().getWidth() * 0.2)
                , (float) (spriteGrad.getContentSize().getHeight() * 0.2));
        spriteGrad.setScale(0.4);
        this.addChild(spriteGrad, 20);
    }

    /**
     * 初始化控制控件
     */
    private void initCtrlView() {
        spriteStep = new CCSprite("icon-step.png");
        spriteTurn = new CCSprite("icon-turn.png");
        spriteLeft = new CCSprite("icon-left.png");
        spriteRight = new CCSprite("icon-right.png");

        spriteStep.setPosition(SpriteUtil.cratePoint(this.getContentSize().getWidth() * 0.6, spriteStep.getContentSize().getHeight() / 2 + 10));
        spriteTurn.setPosition(SpriteUtil.cratePoint(this.getContentSize().getWidth() * 0.8, spriteTurn.getContentSize().getHeight() / 2 + 10));
        spriteLeft.setPosition(SpriteUtil.cratePoint(this.getContentSize().getWidth() * 0.8 - 40, spriteTurn.getContentSize().getHeight() + 40));
        spriteRight.setPosition(SpriteUtil.cratePoint(this.getContentSize().getWidth() * 0.8 + 40, spriteTurn.getContentSize().getHeight() + 40));


        this.addChild(spriteStep, 10);
        this.addChild(spriteTurn, 10);
        this.addChild(spriteLeft, 10);
        this.addChild(spriteRight, 10);

        this.spriteStep1 = SpriteUtil.createStepText("-60",
                SpriteUtil.cratePoint(this.getContentSize().getWidth() * 0.6 - 30, spriteStep.getContentSize().getHeight() + 40)
                , -60);
        this.spriteStep2 = SpriteUtil.createStepText("+60",
                SpriteUtil.cratePoint(this.getContentSize().getWidth() * 0.6 + 30, spriteStep.getContentSize().getHeight() + 40)
                , 60);

        this.addChild(this.spriteStep1, 20);
        this.addChild(this.spriteStep2, 20);
    }

    /**
     * 初始化其他视图控件
     */
    private void initView() {

        if (isRePlay) {
            for (int i = 0; i < gameUserList.size(); i++) {
                GameUser item = gameUserList.get(i);
                item.setDirection(1);
                item.setPumpkinCount(0);
                item.getSprite().setPosition(gameUserList.get(i).getStartPosition());
                item.getSprite().setRotation(0);
            }
            for (int i = 0; i < GameCommon.PUMPKIN_COUNT; i++) {
                pumpkinList.get(i).setOpacity(255);
            }
        } else {
            //添加自身玩家
            GameUser myUser = new GameUser();

            myUser.setId(carrierExecutorInst.getUserID());
            myUser.setDirection(1);
            myUser.setSprite(SpriteUtil.createGameUser(0));
            myUser.setStartPosition(myUser.getSprite().getPosition());
            gameUserList.add(myUser);
            this.addChild(myUser.getSprite(), 5);
            // 添加好友玩家
            GameUser friendUser = new GameUser();
            friendUser.setId(carrierExecutorInst.getFriendID());
            friendUser.setDirection(1);
            friendUser.setSprite(SpriteUtil.createGameUser(1));
            friendUser.setStartPosition(friendUser.getSprite().getPosition());
            gameUserList.add(friendUser);
            this.addChild(friendUser.getSprite(), 5);


            // 添加南瓜和障碍物
            for (int i = 0; i < GameCommon.PUMPKIN_COUNT; i++) {
                pumpkinList.add(randomPositionBySprite(SpriteUtil.createPumpkin()));
                this.addChild(pumpkinList.get(i), 10);
            }

            for (int i = 0; i < GameCommon.BUSH_COUNT; i++) {
                bushList.add(randomPositionBySprite(SpriteUtil.createBush()));
                this.addChild(bushList.get(i), 10);
            }
        }

        createStepPrompt();


    }


    /***
     * 遍历南瓜，创建步数提示
     */
    private void createStepPrompt() {
        for (int i = 0; i < stepPromptList.size(); i++) {
            this.removeChild(stepPromptList.get(i), true);
        }
        stepPromptList.clear();

        CGPoint p = gameUserList.get(0).getSprite().getPosition();

        for (int i = 0; i < pumpkinList.size(); i++) {
            if ((int) (pumpkinList.get(i).getUserData()) == 1) {
                CGRect b = pumpkinList.get(i).getBoundingBox();
                int t = 0;
                if (gameUserList.get(0).getDirection() % 2 != 0) {  //x轴
                    if (p.x > b.origin.x && p.x < b.origin.x + b.size.width) {   // x轴重合

                        t = (int) (b.origin.y - p.y + 10);
                        stepPromptList.add(SpriteUtil.createStepText((t > 0 ? "+" + t : "" + t), createStepPromptPosition(), t));
                        this.addChild(stepPromptList.get(stepPromptList.size() - 1), 20);
                    }
                } else {                                                      //y轴
                    if (p.y > b.origin.y && p.y < b.origin.y + b.size.height) {   // x轴重合
                        t = (int) (b.origin.x - p.x + 10);
                        stepPromptList.add(SpriteUtil.createStepText((t > 0 ? "+" + t : "" + t), createStepPromptPosition(), t));
                        this.addChild(stepPromptList.get(stepPromptList.size() - 1), 20);
                    }
                }

            }
        }

        Log.e("step", stepPromptList.toString());

    }

    /**
     * 创建步数提示的位置
     */
    private CGPoint createStepPromptPosition() {          //为步数提示栏确认位置
        if (stepPromptList.size() > 0) {
            CGPoint item = stepPromptList.get(stepPromptList.size() - 1).getPosition();
            return SpriteUtil.cratePoint(item.x + GameCommon.DEFAULT_FONT_SIZE * 3, item.y);
        } else {
            return SpriteUtil.cratePoint(SpriteUtil.boxSize.width * 0.6 - 30, 130);
        }
    }

    /**
     * 检查位置是否已经有控件存在
     */
    private boolean checkPosition(CCSprite cc) {
        for (int i = 0; i < gameUserList.size(); i++) {
            if (SpriteUtil.isContainsRect(gameUserList.get(i).getSprite(), cc)) {
                return false;
            }
        }
        for (int i = 0; i < pumpkinList.size(); i++) {
            if (SpriteUtil.isContainsRect(pumpkinList.get(i), cc)) {
                return false;
            }
        }
        for (int i = 0; i < bushList.size(); i++) {
            if (SpriteUtil.isContainsRect(bushList.get(i), cc)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 随机位置
     */
    private CCSprite randomPositionBySprite(CCSprite view) {
        CGPoint p;
        while (true) {
            p = SpriteUtil.randomPosition();
            view.setPosition(p);
            // cc.log("random:"+JSON.stringify(p));
            if (checkPosition(view)) {
                break;
            }
        }
        return view;
    }


}