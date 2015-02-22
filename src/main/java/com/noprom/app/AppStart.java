package com.noprom.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.noprom.app.common.FileUtils;
import com.noprom.app.common.StringUtils;

import java.io.File;
import java.util.List;

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

        // 渐变展示启动页
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,1.0f);
        alphaAnimation.setDuration(2000);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        // 兼容低版本的cookie（1.5版本以下，包括1.5.0,1.5.1）
        // TODO
    }

    private void redirectTo() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 检查是否需要更换图片
     *
     * @param view
     */
    private void check(LinearLayout view) {
        String path = FileUtils.getAppCache(this, "welcomeback");
        List<File> files = FileUtils.listPathFiles(path);
        if (!files.isEmpty()) {
            File f = files.get(0);
            long time[] = getTime(f.getName());
            long today = StringUtils.getToday();
            if (today >= time[0] && today <= time[1]) {
                view.setBackgroundDrawable(Drawable.createFromPath(f.getAbsolutePath()));
            }
        }
    }


    /**
     * 分析显示的时间
     *
     * @param time
     * @return
     */
    private long[] getTime(String time) {
        long res[] = new long[2];
        try {
            time = time.substring(0, time.indexOf("."));
            String t[] = time.split("-");
            res[0] = Long.parseLong(t[0]);
            if (t.length >= 2) {
                res[1] = Long.parseLong(t[1]);
            } else {
                res[1] = Long.parseLong(t[0]);
            }
        } catch (Exception e) {

        }
        return res;
    }
}
