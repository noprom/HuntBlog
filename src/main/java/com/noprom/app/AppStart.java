package com.noprom.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.noprom.app.common.FileUtils;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2014-2-22.
 */

public class AppStart extends Activity {

    private static final String TAG = "AppStart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = View.inflate(this, R.layout.activity_app_start, null);
        LinearLayout welcome = (LinearLayout) view.findViewById(R.id.app_start_view);
        check(welcome);
        setContentView(view);
    }

    /**
     * 检查是否需要更换图片
     *
     * @param view
     */
    private void check(LinearLayout view) {
        String path = FileUtils.getAppCache(this,"welcomeback");

    }


}
