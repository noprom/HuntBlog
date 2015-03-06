package com.noprom.app.ui;

import android.app.Activity;
import android.view.View;

/**
 * 应用程序Activity的基类
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/3/6.
 */
public class BaseActivity extends Activity{
    // 是否允许全屏
    private boolean allowFullScreen = true;

    // 是否允许销毁
    private boolean allowDestroy = true;
    private View view;


}
