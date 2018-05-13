package android.carrier.net.elastos.codepumpkin.layer;

import android.carrier.net.elastos.codepumpkin.Bean.Action;
import android.carrier.net.elastos.codepumpkin.Bean.GameElement;
import android.carrier.net.elastos.codepumpkin.Bean.GameUser;
import android.carrier.net.elastos.codepumpkin.MainActivity;
import android.carrier.net.elastos.codepumpkin.common.GameCommon;
import android.carrier.net.elastos.codepumpkin.ui.WaitDialog;
import android.carrier.net.elastos.codepumpkin.util.SpriteUtil;
import android.carrier.net.elastos.p2pNet.CarrierExecutor;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

//implements CCTableViewDataSource, CCTableViewDelegate
public class GameCCLayer extends CCLayer {

    private Context context;

    private CCLabel spriteToast;
    private CCSprite spriteBg;
    private CCSprite spriteGrad;
    private CCSprite spriteStep;
    // private CCSprite spriteTurn;
    private CCSprite spriteLeft;
    private CCSprite spriteRight;
    private CCSprite spriteReset;
//    private CCTableView tableActions;

    private CCSpawn actionTurnLeft;
    private CCSpawn actionTurnRight;
    private CCSpawn actionStep1;
    private CCSpawn actionStep2;
    private CCSpawn actionStepBox;

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

    private Gson gson;

    private WaitDialog waitDialog;

    public GameCCLayer(Context context) {
        this.context = context;
        carrierExecutorInst = new CarrierExecutor(this.context);
        currentUser = carrierExecutorInst.getUserID();

        if (carrierExecutorInst.getMyGameUserType() == 1 && !checkNull(carrierExecutorInst.getFriendID())) {
            carrierExecutorInst.sendMessage(sendRunGameToString());  //给对方发送启动游戏事件
        }
        gson = new Gson();
        waitDialog = new WaitDialog(context);
        isTouchEnabled_ = true;
        SpriteUtil.boxSize = this.getContentSize();
        init();
    }


    private void init() {

        initBg();

        initCtrlView();
        initUserView();



        if (carrierExecutorInst.getMyGameUserType() == 0) {
            initElement();
        } else {
            waitDialog.show("等待地图");
        }

        playUserAnimation();

        createStepPrompt();

        //add globel info
        //schedule("actionLoop", 0.1f);
        //this.context
        // scheduleUpdate();
    }

//    public void update(float dt){
//        actionLoop();
//    }


//    private void playAudio(){
//        SoundEngine engine = new SoundEngine();
//        engine.playEffect(context, R.raw.good_zh);
//
//    }

    /**
     * 重新播放事件
     */
    public void rePlay() {
        // 回到开始、轮询事件
        actionIndex = 0;
        loopWait = false;
        isRePlay = true;
        ((MainActivity) context).runOnUiThread(new Thread() {
            @Override
            public void run() {
                ((MainActivity) context).dialog.showDialog(actionList);
            }
        });
        initUserView();

        createStepPrompt();
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
        //移动按钮
        if (SpriteUtil.isContainsPointByView(spriteStep, p)) {
            if (spriteStep1.getOpacity() == 0) {
                spriteStep1.runAction(actionStep1);
                spriteStep2.runAction(actionStep2);
                setDisplayStep(true);
            } else {
                spriteStep1.runAction(actionStep1.reverse());
                spriteStep2.runAction(actionStep2.reverse());
                setDisplayStep(false);
            }
            return false;
        }

//        if (SpriteUtil.isContainsPointByView(spriteRight, p)) {
//            //spriteLeft.setVisible(true);
//            if (spriteLeft.getOpacity() == 0) {
//                spriteLeft.runAction(actionTurnLeft);
//                spriteRight.runAction(actionTurnRight);
//            } else {
//                spriteLeft.runAction(actionTurnLeft.reverse());
//                spriteRight.runAction(actionTurnRight.reverse());
//            }
//            return false;
//        }


        //左转
        if (SpriteUtil.isContainsPointByView(spriteLeft, p)) {
            //spriteLeft.runAction(actionTurnLeft.reverse());
            //spriteRight.runAction(actionTurnRight.reverse());
            actionHandler(new Action(currentUser, GameCommon.ACTION_ROTA, -90));
            return false;
        }

        //右转
        if (SpriteUtil.isContainsPointByView(spriteRight, p)) {
            //spriteLeft.runAction(actionTurnLeft.reverse());
            //spriteRight.runAction(actionTurnRight.reverse());
            //actionHandler(new Action(currentUser, GameCommon.ACTION_ROTA, 90));
            actionHandler(new Action(currentUser, GameCommon.ACTION_ROTA, 90));
            return false;
        }

        // -60
        if (SpriteUtil.isContainsPointByView(spriteStep1, p)) {
            spriteStep1.runAction(actionStep1.reverse());
            spriteStep2.runAction(actionStep2.reverse());
            actionHandler(new Action(currentUser, GameCommon.ACTION_MOVE, 0 - GameCommon.DEFAULT_SIZE));
            return false;
        }

        // +60
        if (SpriteUtil.isContainsPointByView(spriteStep2, p)) {
            spriteStep1.runAction(actionStep1.reverse());
            spriteStep2.runAction(actionStep2.reverse());
            actionHandler(new Action(currentUser, GameCommon.ACTION_MOVE, GameCommon.DEFAULT_SIZE));
            return false;
        }

        //步数提示
        for (int i = 0; i < stepPromptList.size(); i++) {
            if (SpriteUtil.isContainsPointByView(stepPromptList.get(i), p)) {
                actionHandler(new Action(currentUser, GameCommon.ACTION_MOVE, (float) (stepPromptList.get(i).getUserData())));
                setDisplayStep(false);
            }
        }

        //重放
        if (SpriteUtil.isContainsPointByView(spriteReset, p)) {
            rePlay();
        }


        return super.ccTouchesBegan(event);
    }

    private void setDisplayStep(boolean v) {
        for (int i = 0; i < stepPromptList.size(); i++) {
            stepPromptList.get(i).setOpacity(v ? 255 : 0);
        }
    }


    /**
     * 暴露给外部的数据处理
     */
    public void dataHandler(String data) {
        try {
            if (data != null && !"".equals(data)) {
                JSONObject js = new JSONObject(data);
                JsonReader reader = new JsonReader(new StringReader(js.getString(GameCommon.MESSAGE_KEY_DATA)));
                reader.setLenient(true);
                switch (js.getString(GameCommon.MESSAGE_KEY_TYPE)) {
                    case GameCommon.MESSAGE_ACTION:     //动作
                        actionHandler((Action) (gson.fromJson(reader, Action.class)));
                        break;
                    case GameCommon.MESSAGE_MAP:        //地图
                        mapElementHandler(
                                (List<GameElement>) (gson.fromJson(
                                        reader,
                                        new TypeToken<List<GameElement>>() {
                                        }.getType())));
                        break;
                    case GameCommon.MESSAGE_RUNGAME:    //启动游戏

                        break;
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                            createRotaAction(user, (action.getValue())), CCCallFunc.actionWithTarget(GameCCLayer.this, "actionCall")
                    ));
                    break;
            }

            if (isRePlay) {
                ((MainActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) context).adapter.setCurrentIndex(actionIndex);
                        ((MainActivity) context).dialog.lvAction.setSelection(actionIndex);
                        //((MainActivity)context).adapter.notifyDataSetChanged();
                        //  ListView lvAction = ((MainActivity)context).dialog.lvAction;
                        //  lvAction.performItemClick(lvAction.getChildAt(0),0,lvAction.getItemIdAtPosition(0));
                    }
                });

            }

            if (!checkNull(carrierExecutorInst.getFriendID()) &&
                    !isRePlay &&
                    !action.getUserId().equals(carrierExecutorInst.getFriendID())) {
                carrierExecutorInst.sendMessage(actionToString(action));
            }

        }


    }


    private boolean checkNull(String text) {
        return text == null || "".equals(text);
    }


    private int getUserIndex(String userId) {

        int nRet = 0;
        if (!userId.equals(gameUserList.get(0).getId())) {
            nRet = 1;
        }
//        } if(!userId.equals(carrierExecutorInst.getUserID())){
//            nRet = 1;
//        }

        return nRet;
    }

    private int getMyUserIndex() {
        return carrierExecutorInst.getMyGameUserType();
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

            if (pumpkinList.get(j).getOpacity() != 0) {           //隐藏 被吃掉

                for (int i = 0; i < nUserSize; i++) {
                    if (SpriteUtil.isContainsRect(gameUserList.get(i).getSprite(), pumpkinList.get(j))) {
                        //播放音效
                        ((MainActivity) context).playAudio();
                        pumpkinList.get(j).setUserData(0);      //吃掉
                        pumpkinList.get(j).setOpacity(0);
                        gameUserList.get(i).setPumpkinCount(gameUserList.get(i).getPumpkinCount() + 1);
                        if (gameUserList.get(i).getPumpkinCount() > GameCommon.PUMPKIN_COUNT / 2) {
                            //if(carrierExecutorInst.getUserID().equals(gameUserList.get(i).getId())){
                            if (i == getMyUserIndex()) {
                                toast("游戏胜利");

                            } else {
                                toast("对方取得了胜利");

                            }

                        } else {
                            toast("吃掉南瓜");
                        }
                    }
                }
            }

        }
        //遍历障碍物
        for (int j = 0; j < bushList.size(); j++) {

            for (int i = 0; i < nUserSize; i++) {
                if (SpriteUtil.isContainsRect(gameUserList.get(i).getSprite(), bushList.get(j))) {
                    toast("游戏结束");
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
        float duration = (Math.abs((float) step) / GameCommon.DEFAULT_SIZE) * (isRePlay ? GameCommon.DEFAULT_TIME * 3 : GameCommon.DEFAULT_TIME);
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

        return CCRotateBy.action((isRePlay ? GameCommon.DEFAULT_TIME * 3 : GameCommon.DEFAULT_TIME), value);

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


        spriteGrad = CCSprite.sprite("dragon-body.png");
        spriteGrad.setPosition((float) (spriteGrad.getContentSize().getWidth() * 0.2)
                , (float) (spriteGrad.getContentSize().getHeight() * 0.2));
        spriteGrad.setScale(0.4);


        this.addChild(spriteBg, 0, 0);

        this.addChild(spriteGrad, 20);


        spriteToast = SpriteUtil.createToast();
        this.addChild(spriteToast, 100);
    }

    private void playUserAnimation() {
        CCSpriteFrameCache c1 = CCSpriteFrameCache.initSpriteFrameCache();
        CCSpriteFrameCache c2 = CCSpriteFrameCache.initSpriteFrameCache();
        c1.addSpriteFrames("grad-1.plist");
        c2.addSpriteFrames("grad-2.plist");
        //c.addSpriteFrames("")
        ArrayList<CCSpriteFrame> frames1 = new ArrayList<>();
        ArrayList<CCSpriteFrame> frames2 = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            frames1.add(c1.getSpriteFrame((i) + ".png"));
            frames2.add(c2.getSpriteFrame((i) + ".png"));
        }
        gameUserList.get(0).getSprite().runAction(CCRepeatForever.actionWithAction(CCAnimate.action(CCAnimation.animationWithFrames(frames1, GameCommon.DEFAULT_TIME * 3))));
        gameUserList.get(1).getSprite().runAction(CCRepeatForever.actionWithAction(CCAnimate.action(CCAnimation.animationWithFrames(frames2, GameCommon.DEFAULT_TIME * 3))));

//        CCSpriteFrameCache.initSpriteFrameCache().getSpriteFrame()
//        CCAnimation animation = CCAnimation.animation("");
//        for (int i =)
//        animation.addFrame();
    }

    private void toast(String text) {
        spriteToast.setString(text);
        spriteToast.runAction(CCSequence.actions(CCFadeIn.action(2), CCCallFunc.actionWithTarget(GameCCLayer.this, "toastCall")
        ));
    }

    public void toastCall() {
        spriteToast.setOpacity(0);
    }

    /**
     * 初始化控制控件
     */
    private void initCtrlView() {
        spriteStep = new CCSprite("icon-step.png");
        //spriteTurn = new CCSprite("icon-turn.png");
        spriteLeft = new CCSprite("icon-left.png");
        spriteRight = new CCSprite("icon-right.png");
        spriteReset = new CCSprite("icon-reset.png");

        // spriteTurn.setPosition(SpriteUtil.cratePoint(this.getContentSize().getWidth() - GameCommon.DEFAULT_SIZE, spriteTurn.getContentSize().getHeight() / 2 + 10));
        spriteLeft.setPosition(SpriteUtil.cratePoint(this.getContentSize().getWidth() - GameCommon.DEFAULT_SIZE * 3 - 20, spriteRight.getContentSize().getHeight() / 2 + 10));
        spriteRight.setPosition(SpriteUtil.cratePoint(this.getContentSize().getWidth() - GameCommon.DEFAULT_SIZE - 20, spriteRight.getContentSize().getHeight() / 2 + 10));
        spriteStep.setPosition(SpriteUtil.cratePoint(this.getContentSize().getWidth() - GameCommon.DEFAULT_SIZE * 2 - 20, spriteStep.getContentSize().getHeight() / 2 + 10));
        spriteReset.setPosition(SpriteUtil.cratePoint(GameCommon.DEFAULT_SIZE * 2, spriteReset.getContentSize().getHeight() / 2 + 10));


        //创建动画和透明度
        // spriteLeft.setPosition(spriteTurn.getPosition());
        // spriteLeft.setOpacity(0);
        // spriteRight.setPosition(spriteTurn.getPosition());
        // spriteRight.setOpacity(0);
        actionTurnLeft = SpriteUtil.createShowIconAction(SpriteUtil.cratePoint(0, GameCommon.DEFAULT_SIZE));
        actionTurnRight = SpriteUtil.createShowIconAction(SpriteUtil.cratePoint(0, GameCommon.DEFAULT_SIZE * 2));


        this.addChild(spriteStep, 10);
        //this.addChild(spriteTurn, 10);
        this.addChild(spriteLeft, 10);
        this.addChild(spriteRight, 10);
        this.addChild(spriteReset, 10);

        spriteStep1 = SpriteUtil.createStepText("-60",
                spriteStep.getPosition()
                , -60);
        spriteStep2 = SpriteUtil.createStepText("+60",
                spriteStep.getPosition()
                , 60);
        spriteStep1.setOpacity(0);
        spriteStep2.setOpacity(0);
        //spriteStepBox.setOpacity(0);
        actionStep1 = SpriteUtil.createShowIconAction(SpriteUtil.cratePoint(0, 50));
        actionStep2 = SpriteUtil.createShowIconAction(SpriteUtil.cratePoint(0, 50 + GameCommon.DEFAULT_FONT_SIZE));
        actionStepBox = SpriteUtil.createShowIconAction(SpriteUtil.cratePoint(0, GameCommon.DEFAULT_FONT_SIZE * 2 + 10));
        this.addChild(this.spriteStep1, 20);
        this.addChild(this.spriteStep2, 20);


    }

//    private void initStepTableView() {
//        for (int i = 0; i < 3; i++) {
//            testList.add("888" + i);
//        }
//        tableActions = CCTableView.view(this,getContentSize());
////        tableActions.direction = 1;
//        tableActions.tDelegate = this;
//        tableActions.setPosition(SpriteUtil.cratePoint(getContentSize().width / 2, getContentSize().height / 2));
//        // tableActions.
//        // tableActions.
//        this.addChild(tableActions, 101);
//
//        tableActions.reloadData();
//    }
//
//    @Override
//    public void tableCellTouched(CCTableView ccTableView, CCTableViewCell ccTableViewCell) {
//        Log.i("table", "滑动啦");
//        Log.i("table", ccTableView.getPosition().toString());
//        Log.i("table", ccTableView.getContentSize().toString());
//
//    }
//
//    @Override
//    public CGSize cellSizeForTable(CCTableView ccTableView) {
//        return CGSize.make(100,100);
//    }
//
//
//    //构建列表子项
//    @Override
//    public CCTableViewCell tableCellAtIndex(CCTableView ccTableView, int i) {
//        Log.i("table", i + "加载了");
//
//        CCTableViewCell cc = new CCTableViewCell();
//        CCLabel label = CCLabel.labelWithString("111", "font/consola.ttf", GameCommon.DEFAULT_FONT_SIZE);
//        label.setColor(ccColor3B.ccBLACK);
//        label.setPosition(CGPoint.zero());
//        cc.addChild(label, 50);
//        return cc;
//    }
//
//    @Override
//    public int numberOfCellsInTableView(CCTableView ccTableView) {
//        return testList.size();
//    }


//    private void loadAnimation(){
//        CCSpriteFrameCache c = CCSpriteFrameCache.sharedSpriteFrameCache();
//        c.addSpriteFrames("grad-2.plist");
//        c.addSpriteFrames("")
//        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
//        for(int i=1;i<=8;i++){
//            frames.add(c.getSpriteFrame((i)+".png"));
//       }
//       CCAnimation a = CCAnimation.animationWithFrames(frames,1);
//        spriteGrad.runAction(CCAnimate.action(a));
//        CCAnimation a = CCAnimation.animationWithFrames();
//    }

    /**
     * 初始化其他视图控件
     */
    private void initUserView() {

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

            //添加玩家1
            GameUser user1 = new GameUser();
            user1.setDirection(1);
            // 添加玩家2
            GameUser user2 = new GameUser();
            user2.setDirection(1);

            if (carrierExecutorInst.getMyGameUserType() == 0) {
                user1.setId(carrierExecutorInst.getUserID());
                user2.setId(carrierExecutorInst.getFriendID());

            } else {
                user1.setId(carrierExecutorInst.getFriendID());
                user2.setId(carrierExecutorInst.getUserID());
            }

            user1.setSprite(SpriteUtil.createGameUser(0));
            user2.setSprite(SpriteUtil.createGameUser(1));
            user1.setStartPosition(user1.getSprite().getPosition());
            user2.setStartPosition(user2.getSprite().getPosition());

            gameUserList.add(user1);
            gameUserList.add(user2);

            this.addChild(user1.getSprite(), 5);
            this.addChild(user2.getSprite(), 5);

//            //添加自身玩家
//            GameUser myUser = new GameUser();
//
//          //  myUser.setId(carrierExecutorInst.getUserID());
//            myUser.setId("DATYhHwvqN64ZQHeDkegT8cPn9Qw8wxjLC8LqXhMXfWG");
//            myUser.setDirection(1);
//            myUser.setSprite(SpriteUtil.createGameUser(0));
//            myUser.setStartPosition(myUser.getSprite().getPosition());
//            gameUserList.add(myUser);
//            this.addChild(myUser.getSprite(), 5);
//            // 添加好友玩家
//            GameUser friendUser = new GameUser();
//           // friendUser.setId(carrierExecutorInst.getFriendID());
//            friendUser.setId("9U6E5ZkvtW8pVpo8iSDDyoZ4BccFTRKwRUVbLqYrXY6T");
//            friendUser.setDirection(1);
//            friendUser.setSprite(SpriteUtil.createGameUser(1));
//            friendUser.setStartPosition(friendUser.getSprite().getPosition());
//            gameUserList.add(friendUser);
//            this.addChild(friendUser.getSprite(), 5);

        }

    }

    private void initElement() {

//        pumpkinList.add(SpriteUtil.createPumpkinByPoint(CGPoint.make(480, 310)));
//        pumpkinList.add(SpriteUtil.createPumpkinByPoint(CGPoint.make(320, 270)));
//        pumpkinList.add(SpriteUtil.createPumpkinByPoint(CGPoint.make(530, 270)));
//        pumpkinList.add(SpriteUtil.createPumpkinByPoint(CGPoint.make(210, 250)));
//        pumpkinList.add(SpriteUtil.createPumpkinByPoint(CGPoint.make(160, 360)));
//
//        bushList.add(SpriteUtil.createBushByPoint(CGPoint.make(630, 250)));
//        bushList.add(SpriteUtil.createBushByPoint(CGPoint.make(192, 170)));
//        bushList.add(SpriteUtil.createBushByPoint(CGPoint.make(100, 290)));
//
//        for (int i = 0; i < pumpkinList.size(); i++) {
//            this.addChild(pumpkinList.get(i), 10);
//        }
//        for (int i = 0; i < bushList.size(); i++) {
//            this.addChild(bushList.get(i), 10);
//        }


        // 添加南瓜和障碍物
        for (int i = 0; i < GameCommon.PUMPKIN_COUNT; i++) {
            pumpkinList.add(randomPositionBySprite(SpriteUtil.createPumpkin()));
            this.addChild(pumpkinList.get(i), 10);
        }

        for (int i = 0; i < GameCommon.BUSH_COUNT; i++) {
            bushList.add(randomPositionBySprite(SpriteUtil.createBush()));
            this.addChild(bushList.get(i), 10);
        }

        if (carrierExecutorInst.getMyGameUserType() == 0 && !checkNull(carrierExecutorInst.getFriendID())) {
            carrierExecutorInst.sendMessage(mapElementToString());
        }

    }


    /***
     * 遍历南瓜，创建步数提示
     */
    private void createStepPrompt() {
        // spriteStepBox.removeAllChildren(true);
        for (int i = 0; i < stepPromptList.size(); i++) {
            this.removeChild(stepPromptList.get(i), true);
        }
        stepPromptList.clear();
        GameUser user = gameUserList.get(getMyUserIndex());
        CGPoint p = user.getSprite().getPosition();

        Log.i("stepuser", getUserIndex(currentUser) + "");

        for (int i = 0; i < pumpkinList.size(); i++) {
            if ((int) (pumpkinList.get(i).getUserData()) == 1) {
                CGRect b = pumpkinList.get(i).getBoundingBox();
                int t = 0;
                if (user.getDirection() % 2 != 0) {  //x轴
                    if (p.x > b.origin.x && p.x < b.origin.x + b.size.width) {   // x轴重合

                        if (user.getDirection() == 1) {
                            t = (int) (b.origin.y - p.y + 10);
                        } else {
                            t = (int) (p.y - b.origin.y + 10);

                        }
                        stepPromptList.add(SpriteUtil.createStepText((t > 0 ? "+" + t : "" + t), createStepPromptPosition(), t));

                        this.addChild(stepPromptList.get(stepPromptList.size() - 1), 20);
                    }
                } else {                                                      //y轴
                    if (p.y > b.origin.y && p.y < b.origin.y + b.size.height) {   // y轴重合
                        if (user.getDirection() == 2) {
                            t = (int) (b.origin.x - p.x + 10);
                        } else {
                            t = (int) (p.x - b.origin.x + 10);
                        }
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
//        return SpriteUtil.cratePoint(GameCommon.DEFAULT_SIZE/2, stepPromptList.size() * GameCommon.DEFAULT_FONT_HEIGHT);

        if (stepPromptList.size() > 0) {
            CGPoint item = stepPromptList.get(stepPromptList.size() - 1).getPosition();
            return SpriteUtil.cratePoint(spriteStep.getPosition().x, item.y + GameCommon.DEFAULT_FONT_HEIGHT);
        } else {
            return SpriteUtil.cratePoint(spriteStep.getPosition().x, 140);
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


    /***
     * 处理地图数据到字符串
     * @return
     */
    public String mapElementToString() {
        JSONObject js = new JSONObject();
        try {
            js.put(GameCommon.MESSAGE_KEY_TYPE, GameCommon.MESSAGE_MAP);
            List<GameElement> gameElementList = new ArrayList<>();
            for (int i = 0; i < pumpkinList.size(); i++) {
                gameElementList.add(new GameElement(i, 1, pumpkinList.get(i).getPosition().x, pumpkinList.get(i).getPosition().y));
            }
            for (int i = 0; i < bushList.size(); i++) {
                gameElementList.add(new GameElement(i + 5, 2, bushList.get(i).getPosition().x, bushList.get(i).getPosition().y));
            }
            js.put(GameCommon.MESSAGE_KEY_DATA, gson.toJson(gameElementList));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return js.toString();

    }

    /***
     * 地图控件处理
     */
    public void mapElementHandler(final List<GameElement> list) {
        pumpkinList.clear();
        bushList.clear();
        // 添加南瓜和障碍物
        for (int i = 0; i < list.size(); i++) {
            CCSprite cc = null;
            if (list.get(i).getType() == 1) {
                cc = SpriteUtil.createPumpkin();
                pumpkinList.add(cc);
            } else {
                cc = SpriteUtil.createBush();
                bushList.add(cc);
            }
            cc.setPosition(list.get(i).getX(), list.get(i).getY());
            GameCCLayer.this.addChild(cc);
        }

        waitDialog.dismiss();
        createStepPrompt();


    }

    /**
     * 启动游戏事件字符串
     */
    public String sendRunGameToString() {
        JSONObject js = new JSONObject();
        try {
            js.put(GameCommon.MESSAGE_KEY_TYPE, GameCommon.MESSAGE_RUNGAME);
            js.put(GameCommon.MESSAGE_KEY_DATA, carrierExecutorInst.getUserID() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js.toString();
    }

    /**
     * 动作事件到字符串
     */
    public String actionToString(Action action) {
        JSONObject js = new JSONObject();
        try {
            js.put(GameCommon.MESSAGE_KEY_TYPE, GameCommon.MESSAGE_ACTION);
            js.put(GameCommon.MESSAGE_KEY_DATA, gson.toJson(action));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return js.toString();
    }
}