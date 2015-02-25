package com.noprom.app;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Hashtable;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 * Created by noprom on 2015/2/22.
 */
public class AppContext extends Application {

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static final int PAGE_SIZE = 20;//默认分页大小
    private static final int CACHE_TIME = 60*60000;//缓存失效时间

    private boolean login = false;	//登录状态
    private int loginUid = 0;	//登录用户的id
    private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();

    private String saveImagePath;//保存图片路径

    // TODO add unLoginHandler

    @Override
    public void onCreate(){
        super.onCreate();
        // 注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 设置保存图片的路径
//        saveImagePath =
    }

    /**
     * 获取App安装包信息
     * @return
     */
    public PackageInfo getPackageInfo(){
        PackageInfo info = null;
        try{
            info = getPackageManager().getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if(info == null) info = new PackageInfo();
        return info;
    }

}
