package com.noprom.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.noprom.app.api.ApiClient;
import com.noprom.app.bean.BlogList;
import com.noprom.app.bean.News;
import com.noprom.app.bean.NewsList;
import com.noprom.app.bean.Notice;
import com.noprom.app.bean.User;
import com.noprom.app.common.CyptoUtils;
import com.noprom.app.common.StringUtils;
import com.noprom.app.common.UIHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/2/22.
 */
public class AppContext extends Application {

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static final int PAGE_SIZE = 20;//默认分页大小
    private static final int CACHE_TIME = 60 * 60000;//缓存失效时间
    private static final String TAG = "AppContext";


    private boolean login = false;    //登录状态
    private int loginUid = 0;    //登录用户的id
    private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();

    private String saveImagePath;//保存图片路径

    private Handler unLoginHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
                UIHelper.showLoginDialog(AppContext.this);
            }
        }
    };

    @Override
    public void onCreate() {
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
        saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
        if (StringUtils.isEmpty(saveImagePath)) {
            setProperty(AppConfig.SAVE_IMAGE_PATH, AppConfig.DEFAULT_SAVE_IMAGE_PATH);
            saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
        }
    }

    /**
     * 用户是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return login;
    }

    /**
     * 获取登陆信息
     * @return
     */
    public User getLoginInfo(){
        User lu = new User();
        lu.setUid(StringUtils.toInt(getProperty("user.uid"),0));
        lu.setName(getProperty("user.name"));
        lu.setFace(getProperty("user.face"));
        lu.setAccount(getProperty("user.account"));
        lu.setPwd(CyptoUtils.decode("oschinaApp", getProperty("user.pwd")));
        lu.setLocation(getProperty("user.location"));
        lu.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
        lu.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
        lu.setScore(StringUtils.toInt(getProperty("user.score"), 0));
        lu.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
        return lu;
    }

    /**
     * 用户注销
     */
    public void Logout() {
        ApiClient.cleanCookie();
        this.cleanCookie();
        this.login = false;
        this.loginUid = 0;
    }

    /**
     * 清除保存的缓存
     */
    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }

    /**
     * 检测当前系统声音是否为正常模式
     *
     * @return
     */
    public boolean isAudioNormal() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * 是否发出提示音
     *
     * @return
     */
    public boolean isVoice() {
        String perf_voice = getProperty(AppConfig.CONF_VOICE);
        //默认是开启提示声音
        if (StringUtils.isEmpty(perf_voice))
            return true;
        else
            return StringUtils.toBool(perf_voice);
    }

    /**
     * 应用程序是否发出声音
     *
     * @return
     */
    public boolean isAppSound() {
        return isAudioNormal() && isVoice();
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI 2：WAP 3：NET
     */
    public int getNetworkType() {
        int netType = 0;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return netType;
        }
        int nType = ni.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = ni.getExtraInfo();
            if (!StringUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 新闻列表
     *
     * @param catalog   分类
     * @param pageIndex 当前页数
     * @param isRefresh 是否刷新
     * @return 新闻实体
     * @throws AppException 异常
     */
    public NewsList getNewsList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
        NewsList list = null;
        String key = "newslist_" + catalog + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getNewsList(this, catalog, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (NewsList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (NewsList) readObject(key);
            if (list == null)
                list = new NewsList();
        }
        return list;
    }


    /**
     * 新闻详情
     *
     * @param news_id
     * @param isRefresh
     * @return
     * @throws AppException
     */
    public News getNews(int news_id, boolean isRefresh) throws AppException {
        News news = null;
        String key = "news_" + news_id;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                news = ApiClient.getNewsDetail(this, news_id);
                if (news != null) {
                    Notice notice = news.getNotice();
                    news.setNotice(null);
                    news.setCacheKey(key);
                    saveObject(news, key);
                    news.setNotice(notice);
                }
            } catch (AppException e) {
                news = (News) readObject(key);
                if (news == null)
                    throw e;
            }
        } else {
            news = (News) readObject(key);
            if (news == null)
                news = new News();
        }
        return news;
    }


    /**
     * 博客列表
     *
     * @param type      推荐：recommend 最新：latest
     * @param pageIndex
     * @return
     * @throws AppException
     */
    public BlogList getBlogList(String type, int pageIndex, boolean isRefresh) throws AppException {
        BlogList list = null;
        String key = "bloglist_" + type + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getBlogList(this, type, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (BlogList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (BlogList) readObject(key);
            if (list == null)
                list = new BlogList();
        }
        return list;
    }

    /**
     * 未登录或修改密码后的处理
     */
    public Handler getUnLoginHandler() {
        return this.unLoginHandler;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }


    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 是否加载显示文章图片
     * @return
     */
    public boolean isLoadImage()
    {
        String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
        //默认是加载的
        if(StringUtils.isEmpty(perf_loadimage))
            return true;
        else
            return StringUtils.toBool(perf_loadimage);
    }

    /**
     * 获取内存中保存图片的路径
     *
     * @return
     */
    public String getSaveImagePath() {
        return saveImagePath;
    }

    /**
     * 设置内存中保存图片的路径
     *
     * @return
     */
    public void setSaveImagePath(String saveImagePath) {
        this.saveImagePath = saveImagePath;
    }

    /**
     * 判断缓存数据是否可读
     *
     * @param cachefile
     * @return
     */
    private boolean isReadDataCache(String cachefile) {
        return readObject(cachefile) != null;
    }

    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @return
     */
    private boolean isExistDataCache(String cachefile) {
        boolean exist = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     * @throws java.io.IOException
     */
    public boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(file, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     */
    public Serializable readObject(String file) {
        if (!isExistDataCache(file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            //反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

}
