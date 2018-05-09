
# cocos  Pumkin 调用接口文档



#### > 统一结构


```
{

    data：{}             // 每个接口传输的实际数据
    code: 1              //状态码
    msg：xxx             //信息
    version：1          //数据格式版本号 1.0
    timestamp:xxx       //时间戳
    fromUserId:         //发送者的用户id
    toUserId:           //接收者的用户id
}


```

## 链上通信


#### 1、添加好友


##### 请求数据：

| 字段名 | 内容 | 类型 | 非空 | 备注 |
| ------ | ---- | ---- | ---- |---- |
|     userId   |      |   char   |  *    | 用户id    |



##### 响应数据：


```
data:{
    userId:  
    userName:
    address:
    //////用户的相关信息
}


```


---



#### 2. 加入到游戏

##### 请求数据：

| 字段名 | 内容 | 类型 | 非空 | 备注 |
| ------ | ---- | ---- | ---- |---- |
|     userId   |      |   char   |  *    | 对方的用户id    |
|     gameId   |      |   int   |      | 游戏房间的id，备用字段，若不传则加入当前的游戏    |

##### 响应数据：

```
请求方：返回游戏的信息
data：{
    pumpkinList:[   //南瓜
        {
            x:15,
            y:15,
            width:50,
            height:50,
            zIndex:66,
            scale:1,
            id,
        },
    ],
    bushList:[  //障碍物
        {
            x:15,
            y:15,
            width:50,
            height:50,
            zIndex:66,
            scale:1,
            id
        },
    ],  
    accountList[    //用户
        {
            x:15,
            y:15,
            width:50,
            height:50,
            zIndex:66,
            scale:1,
            id,
            
        },
    ]
}

---

被请求方

data:{
    accountList[    //用户
        {
            x:15,
            y:15,
            width:50,
            height:50,
            zIndex:66,
            scale:1,
            id,
            
        }
    ]
}



```



---



#### 3. 请求动作

##### 请求数据：


```
data:{
    type:1              //动作类型：  1 移动   2旋转
    value:10            //数据   若type = 1传输移动的步数，若type=2 传输方向 1上、2右、3下、4左、
    time:xxx            //当前时间戳
    userId:xxx          //事件发生玩家的id
    direction:1         //用户的方向  1上、2右、3下、4左、
    actionIndex：1      //在事件队列中的位置
}


```


##### 响应数据：




```
data{
    
}


```

---


#### 3. 游戏结束

##### 请求数据：


```
data:{
    type:1              // 结束的情况  1 获胜  2 遇到障碍物 3网络断开...
    action:{}           // 结束之前最后一次的事件
    
}


```


##### 响应数据：




```
data{
    
}


```

