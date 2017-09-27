package com.rair.flyball.base;

import android.app.Application;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by Administrator on 2016/12/3.
 */

public class MyApplication extends Application {

    public static final String APP_ID = "6e40638d72bd2f5236e5c53ecfbf367b";

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Bmob
        Bmob.initialize(getApplicationContext(), APP_ID);
        //创建AppVersion表
        BmobUpdateAgent.initAppVersion();
    }
}
