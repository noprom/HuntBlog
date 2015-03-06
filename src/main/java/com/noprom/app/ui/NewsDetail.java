package com.noprom.app.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.noprom.app.R;


/**
 * 新闻详情
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 * Created by noprom on 2015/2/25.
 */
public class NewsDetail extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // 允许左上方有一个返回的按钮，并且可以点击
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
