package com.noprom.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.noprom.app.AppContext;
import com.noprom.app.AppException;
import com.noprom.app.R;
import com.noprom.app.adapter.ListViewNewsAdapter;
import com.noprom.app.bean.News;
import com.noprom.app.bean.NewsList;
import com.noprom.app.bean.Notice;
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

    private View lvNews_footer;
    private TextView lvNews_foot_more;
    private ProgressBar lvNews_foot_progress;
    private ListView lvNews_root;

    private PullToRefreshListView lvNews;
    private ListViewNewsAdapter lvNewsAdapter;
    private List<News> lvNewsData = new ArrayList<News>();
    private int lvNewsSumData;
    private int curNewsCatalog = NewsList.CATALOG_ALL;

    private Handler lvNewsHandler;
    private boolean isClearNotice = false;
    private AppContext appContext;  // 全局Context

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
<<<<<<< HEAD:src/main/java/com/noprom/app/fragment/MainNewsFragment.java
        lvNews_root = (ListView) inflater.inflate(R.layout.fragment_main_news, container, false);
//        lvNewsAdapter = new ListViewNewsAdapter(getActivity(), lvNewsData, R.layout.news_listitem);
//        lvNews_footer = inflater.inflate(R.layout.listview_footer, null);
//        lvNews_foot_more = (TextView) lvNews_footer.findViewById(R.id.listview_foot_more);
//        lvNews_foot_progress = (ProgressBar) lvNews_footer.findViewById(R.id.listview_foot_progress);
//        lvNews = (PullToRefreshListView) lvNews_root.findViewById(R.id.listview_news);
//        lvNews.addFooterView(lvNews_footer);    // 添加底部视图必须在setAdapter之间
//        lvNews.setAdapter(lvNewsAdapter);
//
//        // 设置每个选项的点击事件
//        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // 点击头部、底部栏目无效
//                if (position == 0 || view == lvNews_footer)
//                    return;
//
//                News news = null;
//                if (view instanceof TextView) {
//                    news = (News) view.getTag();
//                } else {
//                    TextView tv = (TextView) view.findViewById(R.id.news_listitem_title);
//                    news = (News) tv.getTag();
//                }
//                if (news == null)
//                    return;
//
//                // TODO 跳转到新闻详情页面
//
//            }
//        });
//
//        // TODO 设置下滑滚动事件
//
//
//        // 加载ListView数据
//        this.initNewsListData();

        return lvNews_root;
    }


    /**
     * 加载ListView数据
     */
    private void initNewsListData() {
        // 初始化handler
        lvNewsHandler = this.getLvHandler(lvNews, lvNewsAdapter, lvNews_foot_more, lvNews_foot_progress, AppContext.PAGE_SIZE);

        // 加载资讯数据
        if (lvNewsData.isEmpty()) {
            loadLvNewsData(curNewsCatalog, 0, lvNewsHandler, UIHelper.LISTVIEW_ACTION_INIT);
        }
=======
        return inflater.inflate(R.layout.fragment_news, container, false);
>>>>>>> parent of c2da3ab... 【新增】实体基类：实现序列化:src/main/java/com/noprom/app/fragment/NewsFragment.java
    }

    /**
     * 线程加载新闻数据
     *
     * @param catalog   分类
     * @param pageIndex 当前页数
     * @param handler   处理器
     * @param action    动作标识
     */
    private void loadLvNewsData(final int catalog, final int pageIndex, final Handler handler, final int action) {
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                boolean isRefresh = false;
                if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL){
                    isRefresh = true;
                }
                try{
                    NewsList list = appContext.getNewsList(catalog,pageIndex,isRefresh);
                    msg.what = list.getPageSize();
                    msg.obj = list;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
                if(curNewsCatalog == catalog)
                    handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 获取listview的初始化Handler
     *
     * @param lv
     * @param adapter
     * @param more
     * @param progress
     * @param pageSize
     * @return
     */
    private Handler getLvHandler(final PullToRefreshListView lv,
                                 final BaseAdapter adapter, final TextView more,
                                 final ProgressBar progress, final int pageSize) {

        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what >= 0) {
                    // listview数据处理
                    Notice notice = handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
                    if (msg.what < pageSize) {
                        lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
                        adapter.notifyDataSetChanged();
                        more.setText(R.string.load_full);
                    } else if (msg.what == pageSize) {
                        lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
                        adapter.notifyDataSetChanged();
                        more.setText(R.string.load_more);
                        // TODO 特殊处理-热门动弹不能翻页
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

                    // TODO 是否清除通知信息
//                    if (isClearNotice) {
//                        ClearNotice(curClearNoticeType);
//                        isClearNotice = false;// 重置
//                        curClearNoticeType = 0;
//                    }
                } else if (msg.what == -1) {
                    // 有异常--显示加载出错 & 弹出错误消息
                    lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
                    more.setText(R.string.load_error);
                    ((AppException) msg.obj).makeToast(getActivity());
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
     * listview数据处理
     *
     * @param what
     * @param obj
     * @param objtype
     * @param actiontype
     * @return
     */
    private Notice handleLvData(int what, Object obj, int objtype, int actiontype) {
        Notice notice = null;
        switch (actiontype) {
            case UIHelper.LISTVIEW_ACTION_INIT:
            case UIHelper.LISTVIEW_ACTION_REFRESH:
            case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
                int newdata = 0;    // 新加载数据，只有刷新动作才会使用到
                switch (objtype) {
                    // 新闻数据
                    case UIHelper.LISTVIEW_DATATYPE_NEWS:
                        NewsList nlist = (NewsList) obj;
                        notice = nlist.getNotice();
                        lvNewsSumData = what;
                        // 处理新增数据
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
                        lvNewsData.clear(); // 先清除原来的数据
                        lvNewsData.addAll(nlist.getNewslist());
                        break;
                    // TODO
                }
//                if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                    // 提示新加载数据
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
//                }
                break;
        }
        return notice;
    }


}
