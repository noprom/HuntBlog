package com.noprom.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.noprom.app.AppContext;
import com.noprom.app.AppException;
import com.noprom.app.R;
import com.noprom.app.adapter.ListViewNewsAdapter;
import com.noprom.app.bean.News;
import com.noprom.app.bean.NewsList;
import com.noprom.app.bean.Notice;
import com.noprom.app.common.StringUtils;
import com.noprom.app.common.UIHelper;
import com.noprom.app.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 综合Tab 新闻资讯Fragment
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2014-2-25.
 */
public class NewsFragment extends Fragment {
    private final String TAG = "NewsFragment";

    private ListViewNewsAdapter lvNewsAdapter;
    private List<News> lvNewsData = new ArrayList<News>();
    private Handler lvNewsHandler;


    private PullToRefreshListView lvNews;
    private View lvNews_footer;
    private TextView lvNews_foot_more;
    private ProgressBar lvNews_foot_progress;
    private LinearLayout lvNews_root;

    private int lvNewsSumData;
    private int curNewsCatalog = NewsList.CATALOG_ALL;
    private AppContext appContext;// 全局Context


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        lvNews_root = (LinearLayout) inflater.inflate(R.layout.fragment_news, container, false);

        lvNewsAdapter = new ListViewNewsAdapter(getActivity(), lvNewsData,
                R.layout.news_listitem);
        lvNews_footer = inflater.inflate(R.layout.listview_footer,
                null);
        lvNews_foot_more = (TextView) lvNews_footer
                .findViewById(R.id.listview_foot_more);
        lvNews_foot_progress = (ProgressBar) lvNews_footer
                .findViewById(R.id.listview_foot_progress);
        lvNews = (PullToRefreshListView) lvNews_root.findViewById(R.id.listview_news);
        lvNews.addFooterView(lvNews_footer);// 添加底部视图 必须在setAdapter前
        lvNews.setAdapter(lvNewsAdapter);
        // 每一项的点击事件
        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvNews_footer)
                    return;

                News news = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    news = (News) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.news_listitem_title);
                    news = (News) tv.getTag();
                }
                if (news == null)
                    return;

                // 跳转到新闻详情
                UIHelper.showNewsRedirect(view.getContext(), news);
            }
        });
        lvNews.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvNews.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvNewsData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvNews_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvNews.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvNews_foot_more.setText(R.string.load_ing);
                    lvNews_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
                    loadLvNewsData(curNewsCatalog, pageIndex, lvNewsHandler,
                            UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvNews.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                loadLvNewsData(curNewsCatalog, 0, lvNewsHandler,
                        UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });

        appContext = (AppContext) getActivity().getApplication();
        this.initNewsListData();

        return lvNews_root;
    }

    /**
     * 初始化新闻资讯ListView数据
     */
    private void initNewsListData() {
        // 初始化Handler
        lvNewsHandler = this.getLvHandler(lvNews, lvNewsAdapter,
                lvNews_foot_more, lvNews_foot_progress, AppContext.PAGE_SIZE);
        // 加载资讯数据
        if (lvNewsData.isEmpty()) {
            loadLvNewsData(curNewsCatalog, 0, lvNewsHandler,
                    UIHelper.LISTVIEW_ACTION_INIT);
        }
    }

    /**
     * 获取listview的初始化Handler
     *
     * @param lv
     * @param adapter
     * @return
     */
    private Handler getLvHandler(final PullToRefreshListView lv,
                                 final BaseAdapter adapter, final TextView more,
                                 final ProgressBar progress, final int pageSize) {
        return new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what >= 0) {
                    // listview数据处理
                    Notice notice = handleLvData(msg.what, msg.obj, msg.arg2,
                            msg.arg1);

                    if (msg.what < pageSize) {
                        lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
                        adapter.notifyDataSetChanged();
                        more.setText(R.string.load_full);
                    } else if (msg.what == pageSize) {
                        lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
                        adapter.notifyDataSetChanged();
                        more.setText(R.string.load_more);

                        // 特殊处理-热门动弹不能翻页
//                        if (lv == lvTweet) {
//                            TweetList tlist = (TweetList) msg.obj;
//                            if (lvTweetData.size() == tlist.getTweetCount()) {
//                                lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
//                                more.setText(R.string.load_full);
//                            }
//                        }
                    }
                    // 发送通知广播
                    if (notice != null) {
                        UIHelper.sendBroadCast(lv.getContext(), notice);
                    }
                    // 是否清除通知信息
//                    if (isClearNotice) {
//                        ClearNotice(curClearNoticeType);
//                        isClearNotice = false;// 重置
//                        curClearNoticeType = 0;
//                    }
                } else if (msg.what == -1) {
                    // 有异常--显示加载出错 & 弹出错误消息
                    lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
                    more.setText(R.string.load_error);
//                    ((AppException) msg.obj).makeToast(MainActivity.this);
                }
                if (adapter.getCount() == 0) {
                    lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
                    more.setText(R.string.load_empty);
                }
                progress.setVisibility(ProgressBar.GONE);
//                mHeadProgress.setVisibility(ProgressBar.GONE);
                if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
                    lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)
                            + new Date().toLocaleString());
                    lv.setSelection(0);
                } else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
                    lv.onRefreshComplete();
                    lv.setSelection(0);
                }
            }
        };
    }

    /**
     * 线程加载新闻数据
     *
     * @param catalog
     *            分类
     * @param pageIndex
     *            当前页数
     * @param handler
     *            处理器
     * @param action
     *            动作标识
     */
    private void loadLvNewsData(final int catalog, final int pageIndex,
                                final Handler handler, final int action) {
//        mHeadProgress.setVisibility(ProgressBar.VISIBLE);
        new Thread() {
            public void run() {
                Message msg = new Message();
                boolean isRefresh = false;
                if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                        || action == UIHelper.LISTVIEW_ACTION_SCROLL)
                    isRefresh = true;
                try {
                    NewsList list = appContext.getNewsList(catalog, pageIndex,
                            isRefresh);

                    msg.what = list.getPageSize();
                    msg.obj = list;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
                if (curNewsCatalog == catalog)
                    handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * listview数据处理
     *
     * @param what       数量
     * @param obj        数据
     * @param objtype    数据类型
     * @param actiontype 操作类型
     * @return notice    通知信息
     */
    private Notice handleLvData(int what, Object obj, int objtype,
                                int actiontype) {
        Notice notice = null;
        switch (actiontype) {
            case UIHelper.LISTVIEW_ACTION_INIT:
            case UIHelper.LISTVIEW_ACTION_REFRESH:
            case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
                int newdata = 0;// 新加载数据-只有刷新动作才会使用到
                switch (objtype) {
                    case UIHelper.LISTVIEW_DATATYPE_NEWS:
                        NewsList nlist = (NewsList) obj;
                        notice = nlist.getNotice();
                        lvNewsSumData = what;
                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            if (lvNewsData.size() > 0) {
                                for (News news1 : nlist.getNewslist()) {
                                    boolean b = false;
                                    for (News news2 : lvNewsData) {
                                        if (news1.getId() == news2.getId()) {
                                            b = true;
                                            break;
                                        }
                                    }
                                    if (!b)
                                        newdata++;
                                }
                            } else {
                                newdata = what;
                            }
                        }
                        lvNewsData.clear();// 先清除原有数据
                        lvNewsData.addAll(nlist.getNewslist());
                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_BLOG:
//                        BlogList blist = (BlogList) obj;
//                        notice = blist.getNotice();
//                        lvBlogSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvBlogData.size() > 0) {
//                                for (Blog blog1 : blist.getBloglist()) {
//                                    boolean b = false;
//                                    for (Blog blog2 : lvBlogData) {
//                                        if (blog1.getId() == blog2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvBlogData.clear();// 先清除原有数据
//                        lvBlogData.addAll(blist.getBloglist());
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_POST:
//                        PostList plist = (PostList) obj;
//                        notice = plist.getNotice();
//                        lvQuestionSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvQuestionData.size() > 0) {
//                                for (Post post1 : plist.getPostlist()) {
//                                    boolean b = false;
//                                    for (Post post2 : lvQuestionData) {
//                                        if (post1.getId() == post2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvQuestionData.clear();// 先清除原有数据
//                        lvQuestionData.addAll(plist.getPostlist());
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_TWEET:
//                        TweetList tlist = (TweetList) obj;
//                        notice = tlist.getNotice();
//                        lvTweetSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvTweetData.size() > 0) {
//                                for (Tweet tweet1 : tlist.getTweetlist()) {
//                                    boolean b = false;
//                                    for (Tweet tweet2 : lvTweetData) {
//                                        if (tweet1.getId() == tweet2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvTweetData.clear();// 先清除原有数据
//                        lvTweetData.addAll(tlist.getTweetlist());
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
//                        ActiveList alist = (ActiveList) obj;
//                        notice = alist.getNotice();
//                        lvActiveSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvActiveData.size() > 0) {
//                                for (Active active1 : alist.getActivelist()) {
//                                    boolean b = false;
//                                    for (Active active2 : lvActiveData) {
//                                        if (active1.getId() == active2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvActiveData.clear();// 先清除原有数据
//                        lvActiveData.addAll(alist.getActivelist());
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
//                        MessageList mlist = (MessageList) obj;
//                        notice = mlist.getNotice();
//                        lvMsgSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvMsgData.size() > 0) {
//                                for (Messages msg1 : mlist.getMessagelist()) {
//                                    boolean b = false;
//                                    for (Messages msg2 : lvMsgData) {
//                                        if (msg1.getId() == msg2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvMsgData.clear();// 先清除原有数据
//                        lvMsgData.addAll(mlist.getMessagelist());
//                        break;
                }
                if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                    // 提示新加载数据
//                    if (newdata > 0) {
//                        NewDataToast
//                                .makeText(
//                                        this,
//                                        getString(R.string.new_data_toast_message,
//                                                newdata), appContext.isAppSound())
//                                .show();
//                    } else {
//                        NewDataToast.makeText(this,
//                                getString(R.string.new_data_toast_none), false)
//                                .show();
//                    }
                }
                break;
            case UIHelper.LISTVIEW_ACTION_SCROLL:
                switch (objtype) {
                    case UIHelper.LISTVIEW_DATATYPE_NEWS:
                        NewsList list = (NewsList) obj;
                        notice = list.getNotice();
                        lvNewsSumData += what;
                        if (lvNewsData.size() > 0) {
                            for (News news1 : list.getNewslist()) {
                                boolean b = false;
                                for (News news2 : lvNewsData) {
                                    if (news1.getId() == news2.getId()) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (!b)
                                    lvNewsData.add(news1);
                            }
                        } else {
                            lvNewsData.addAll(list.getNewslist());
                        }
                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_BLOG:
//                        BlogList blist = (BlogList) obj;
//                        notice = blist.getNotice();
//                        lvBlogSumData += what;
//                        if (lvBlogData.size() > 0) {
//                            for (Blog blog1 : blist.getBloglist()) {
//                                boolean b = false;
//                                for (Blog blog2 : lvBlogData) {
//                                    if (blog1.getId() == blog2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvBlogData.add(blog1);
//                            }
//                        } else {
//                            lvBlogData.addAll(blist.getBloglist());
//                        }
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_POST:
//                        PostList plist = (PostList) obj;
//                        notice = plist.getNotice();
//                        lvQuestionSumData += what;
//                        if (lvQuestionData.size() > 0) {
//                            for (Post post1 : plist.getPostlist()) {
//                                boolean b = false;
//                                for (Post post2 : lvQuestionData) {
//                                    if (post1.getId() == post2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvQuestionData.add(post1);
//                            }
//                        } else {
//                            lvQuestionData.addAll(plist.getPostlist());
//                        }
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_TWEET:
//                        TweetList tlist = (TweetList) obj;
//                        notice = tlist.getNotice();
//                        lvTweetSumData += what;
//                        if (lvTweetData.size() > 0) {
//                            for (Tweet tweet1 : tlist.getTweetlist()) {
//                                boolean b = false;
//                                for (Tweet tweet2 : lvTweetData) {
//                                    if (tweet1.getId() == tweet2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvTweetData.add(tweet1);
//                            }
//                        } else {
//                            lvTweetData.addAll(tlist.getTweetlist());
//                        }
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
//                        ActiveList alist = (ActiveList) obj;
//                        notice = alist.getNotice();
//                        lvActiveSumData += what;
//                        if (lvActiveData.size() > 0) {
//                            for (Active active1 : alist.getActivelist()) {
//                                boolean b = false;
//                                for (Active active2 : lvActiveData) {
//                                    if (active1.getId() == active2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvActiveData.add(active1);
//                            }
//                        } else {
//                            lvActiveData.addAll(alist.getActivelist());
//                        }
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
//                        MessageList mlist = (MessageList) obj;
//                        notice = mlist.getNotice();
//                        lvMsgSumData += what;
//                        if (lvMsgData.size() > 0) {
//                            for (Messages msg1 : mlist.getMessagelist()) {
//                                boolean b = false;
//                                for (Messages msg2 : lvMsgData) {
//                                    if (msg1.getId() == msg2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvMsgData.add(msg1);
//                            }
//                        } else {
//                            lvMsgData.addAll(mlist.getMessagelist());
//                        }
//                        break;
                }
                break;
        }
        return notice;
    }

}
