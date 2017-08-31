package com.heweater;

import android.content.Context;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017/8/30.
 */


/**
 * Created by Administrator on 2017/8/16.
 */

public class MyApplication extends LitePalApplication {
    private static Context context;
    private static MyApplication instance;
    @Override
    public void onCreate(){
        context = getApplicationContext();
        LitePal.initialize(context);
        instance = this;
    }
    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getContext(){
        return context;
    }
}
