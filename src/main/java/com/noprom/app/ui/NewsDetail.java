package com.noprom.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.noprom.app.AppConfig;
import com.noprom.app.R;
import com.noprom.app.bean.CommentList;
import com.noprom.app.bean.News;


/**
 * 新闻详情
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/2/25.
 */
public class NewsDetail extends ActionBarActivity {

    private int newsId;
    private WebView mWebView;
    private Handler mHandler;
    private News newsDetail;

    private TextView mTitle;
    private TextView mAuthor;
    private TextView mPubDate;
    private TextView mCommentCount;



    private String tempCommentKey = AppConfig.TEMP_COMMENT;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        // 允许左上方有一个返回的按钮，并且可以点击
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.initView();


    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        newsId = getIntent().getIntExtra("news_id", 0);
        if (newsId > 0) {
            tempCommentKey = AppConfig.TEMP_COMMENT + "_" + CommentList.CATALOG_NEWS + "_" + newsId;
        }

        mTitle = (TextView) findViewById(R.id.news_detail_title);
        mAuthor = (TextView) findViewById(R.id.news_detail_author);
        mPubDate = (TextView) findViewById(R.id.news_detail_date);
        mCommentCount = (TextView) findViewById(R.id.news_detail_commentcount);

        mWebView = (WebView) findViewById(R.id.news_detail_webview);
        // 允许缩放
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDefaultFontSize(15);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
