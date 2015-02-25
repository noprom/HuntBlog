package com.noprom.app.common;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import com.noprom.app.AppManager;
import com.noprom.app.R;

/**
 * 双击退出
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 * Created by noprom on 2015/2/25.
 */
public class DoubleClickExitHelper {

    private final Activity mActivity;

    private boolean isOnkeyBacking;
    private Handler mHandler;
    private Toast mBackToast;

    public DoubleClickExitHelper(Activity activity) {
        mActivity = activity;
        mHandler = new Handler(Looper.myLooper());
    }

    /**
     * Activity onKeyDown事件
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return false;
        }
        if(isOnkeyBacking){
            mHandler.removeCallbacks(onBackTimeRunnable);
            if(mBackToast !=null){
                mBackToast.cancel();
            }
            // 退出程序
            AppManager.getAppManager().AppExit(mActivity);
            return true;
        }else{
            isOnkeyBacking = true;
            if(mBackToast == null){
                mBackToast = Toast.makeText(mActivity, R.string.back_exit_tips,Toast.LENGTH_LONG);
            }
            mBackToast.show();
            mHandler.postDelayed(onBackTimeRunnable,2000);
            return true;
        }
    }

    private Runnable onBackTimeRunnable = new Runnable() {
        @Override
        public void run() {
            isOnkeyBacking = false;
            if(mBackToast != null){
                mBackToast.cancel();
            }
        }
    };
}
