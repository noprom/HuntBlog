package com.noprom.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.noprom.app.AppConfig;
import com.noprom.app.AppContext;
import com.noprom.app.AppException;
import com.noprom.app.R;
import com.noprom.app.bean.CommentList;
import com.noprom.app.bean.News;
import com.noprom.app.bean.Notice;
import com.noprom.app.common.StringUtils;
import com.noprom.app.common.UIHelper;
import com.noprom.app.widget.BadgeView;


/**
 * 新闻详情
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/2/25.
 */
public class NewsDetail extends ActionBarActivity {

    private final String TAG = "NewsDetail";

    private int newsId;
    private WebView mWebView;
    private Handler mHandler;
    private News newsDetail;

    private TextView mTitle;
    private TextView mAuthor;
    private TextView mPubDate;
    private TextView mCommentCount;

    private BadgeView bv_comment;
    private ImageButton mFavorite;
    private ImageButton mComment;
    private ImageButton mWrite;
    private ImageButton mShare;
    private ImageButton mCommonSwitch;

    private ImageButton mCommentSwitch;
    private EditText mFootEditbox;

    private ViewSwitcher mFooterViewSwitcher;

    private String tempCommentKey = AppConfig.TEMP_COMMENT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        // 允许左上方有一个返回的按钮，并且可以点击
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.initView();
        this.initData();


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
        UIHelper.addWebImageShow(this, mWebView);

        mFooterViewSwitcher = (ViewSwitcher) findViewById(R.id.news_detail_foot_viewswitcher);
        // 底部的图标
        mFavorite = (ImageButton) findViewById(R.id.news_detail_footbar_star);
        mComment = (ImageButton) findViewById(R.id.news_detail_footbar_comment);
        mWrite = (ImageButton) findViewById(R.id.news_detail_footbar_write);
        mShare = (ImageButton) findViewById(R.id.news_detail_footbar_share);
        mCommonSwitch = (ImageButton) findViewById(R.id.news_detail_footbar_common_switch);

        // 评论时的图标
        mCommentSwitch = (ImageButton) findViewById(R.id.news_detail_footbar_comment_switch);
        mFootEditbox = (EditText) findViewById(R.id.news_detail_footbar_editbox);


        // 评论数目
//        bv_comment = new BadgeView(this, mComment);
//        bv_comment.setBackgroundResource(R.drawable.widget_count_bg2);
//        bv_comment.setIncludeFontPadding(false);
//        bv_comment.setGravity(Gravity.CENTER);
//        bv_comment.setTextSize(8f);
//        bv_comment.setTextColor(Color.WHITE);


        // 由主界面切换至评论界面
        mCommonSwitch.setOnClickListener(switchToCommentListener);
        mWrite.setOnClickListener(switchToCommentListener);

        // 由评论界面切换至主界面
        mCommentSwitch.setOnClickListener(switchToCommenListener);

    }




    /**
     * 初始化数据
     */
    private void initData() {

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {

                    // 加载数据
                    mTitle.setText(newsDetail.getTitle());
                    mAuthor.setText(newsDetail.getAuthor());
                    mPubDate.setText(StringUtils.friendly_time(newsDetail.getPubDate()));
                    mCommentCount.setText(String.valueOf(newsDetail.getCommentCount()));

                    // 是否收藏
                    if (newsDetail.getFavorite() == 1) {
                        mFavorite.setBackgroundResource(R.drawable.news_detail_footbar_star_on);
                    } else {
                        mFavorite.setBackgroundResource(R.drawable.news_detail_footbar_star);
                    }

                    // 显示评论数
//                    if(newsDetail.getCommentCount() > 0){
//                        bv_comment.setText(newsDetail.getCommentCount() + "");
//                        bv_comment.show();
//                    }else{
//                        bv_comment.setText("");
//                        bv_comment.hide();
//                    }

                    // 加载内容
                    String body = UIHelper.WEB_STYLE + newsDetail.getBody();

                    // 读取用户设置：是否加载文章图片 -- 默认有wifi下始终加载图片
                    boolean isLoadImage;
                    AppContext ac = (AppContext) getApplication();
                    if (AppContext.NETTYPE_WIFI == ac.getNetworkType()) {
                        isLoadImage = true;
                    } else {
                        isLoadImage = ac.isLoadImage();
                    }

                    if (isLoadImage) {
                        // 过滤掉 img标签的width,height属性
                        body = body.replaceAll(
                                "(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
                        body = body.replaceAll(
                                "(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

                        // 添加点击图片放大支持
                        body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                                "$1$2\" onClick=\"javascript:mWebViewImageListener.onImageClick('$2')\"");

                    } else {
                        // 过滤掉 img标签
                        body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
                    }

                    // 更多关于***软件的信息
                    String softwareName = newsDetail.getSoftwareName();
                    String softwareLink = newsDetail.getSoftwareLink();
                    if (!StringUtils.isEmpty(softwareName)
                            && !StringUtils.isEmpty(softwareLink))
                        body += String
                                .format("<div id='oschina_software' style='margin-top:8px;color:#FF0000;font-weight:bold'>更多关于:&nbsp;<a href='%s'>%s</a>&nbsp;的详细信息</div>",
                                        softwareLink, softwareName);

                    // 相关新闻
                    if (newsDetail.getRelatives().size() > 0) {
                        String strRelative = "";
                        for (News.Relative relative : newsDetail.getRelatives()) {
                            strRelative += String
                                    .format("<a href='%s' style='text-decoration:none'>%s</a><p/>",
                                            relative.url, relative.title);
                        }
                        body += String.format(
                                "<p/><hr/><b>相关资讯</b><div><p/>%s</div>",
                                strRelative);
                    }

                    body += "<div style='margin-bottom: 80px'/>";

                    System.out.println(body);

                    mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
                    mWebView.setWebViewClient(UIHelper.getWebViewClient());

                    // 发送通知广播
                    if (msg.obj != null) {
                        UIHelper.sendBroadCast(NewsDetail.this, (Notice) msg.obj);
                    }
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(NewsDetail.this, R.string.msg_load_is_null);
                } else if (msg.what == -1 && msg.obj != null) {
                    ((AppException) msg.obj).makeToast(NewsDetail.this);
                }
            }
        };

        initData(newsId, false);
    }

    private void initData(final int news_id, final boolean isRefresh) {
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    newsDetail = ((AppContext) getApplication()).getNews(news_id, isRefresh);
                    msg.what = (newsDetail != null && newsDetail.getId() > 0) ? 1 : 0;
                    msg.obj = (newsDetail != null) ? newsDetail.getNotice() : null;// 通知信息
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                mHandler.sendMessage(msg);
            }
        }.start();
    }




    // 主界面切换至评论界面Listener
    private View.OnClickListener switchToCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
            anim.setDuration(500);
            findViewById(R.id.news_detail_footer_common).startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mFooterViewSwitcher.showNext();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    };

    // 由评论界面切换至主界面
    private View.OnClickListener switchToCommenListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
            anim.setDuration(500);
            findViewById(R.id.news_detail_footer_commentlv).startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mFooterViewSwitcher.setDisplayedChild(0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
