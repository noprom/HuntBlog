package com.noprom.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.noprom.app.R;
import com.noprom.app.adapter.ListViewNewsAdapter;
import com.noprom.app.bean.News;
import com.noprom.app.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 综合Tab 新闻资讯Fragment
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2014-2-25.
 */
public class NewsFragment extends Fragment {

    private ListViewNewsAdapter lvNewsAdapter;
    private List<News> lvNewsData = new ArrayList<News>();

    private PullToRefreshListView lvNews;
    private View lvNews_footer;
    private TextView lvNews_foot_more;
    private ProgressBar lvNews_foot_progress;
    private LinearLayout lvNews_root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lvNews_root = (LinearLayout) inflater.inflate(R.layout.fragment_news,container,false);

        lvNewsAdapter = new ListViewNewsAdapter(getActivity(), lvNewsData,
                R.layout.news_listitem);
//        lvNews_footer = inflater.inflate(R.layout.listview_footer,
//                null);
//        lvNews_foot_more = (TextView) lvNews_footer
//                .findViewById(R.id.listview_foot_more);
//        lvNews_foot_progress = (ProgressBar) lvNews_footer
//                .findViewById(R.id.listview_foot_progress);
//        lvNews = (PullToRefreshListView) findViewById(R.id.frame_listview_news);
//        lvNews.addFooterView(lvNews_footer);// 添加底部视图 必须在setAdapter前
//        lvNews.setAdapter(lvNewsAdapter);
//        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // 点击头部、底部栏无效
//                if (position == 0 || view == lvNews_footer)
//                    return;
//
//                News news = null;
//                // 判断是否是TextView
//                if (view instanceof TextView) {
//                    news = (News) view.getTag();
//                } else {
//                    TextView tv = (TextView) view
//                            .findViewById(R.id.news_listitem_title);
//                    news = (News) tv.getTag();
//                }
//                if (news == null)
//                    return;
//
//                // 跳转到新闻详情
//                UIHelper.showNewsRedirect(view.getContext(), news);
//            }
//        });
//        lvNews.setOnScrollListener(new AbsListView.OnScrollListener() {
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                lvNews.onScrollStateChanged(view, scrollState);
//
//                // 数据为空--不用继续下面代码了
//                if (lvNewsData.isEmpty())
//                    return;
//
//                // 判断是否滚动到底部
//                boolean scrollEnd = false;
//                try {
//                    if (view.getPositionForView(lvNews_footer) == view
//                            .getLastVisiblePosition())
//                        scrollEnd = true;
//                } catch (Exception e) {
//                    scrollEnd = false;
//                }
//
//                int lvDataState = StringUtils.toInt(lvNews.getTag());
//                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
//                    lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
//                    lvNews_foot_more.setText(R.string.load_ing);
//                    lvNews_foot_progress.setVisibility(View.VISIBLE);
//                    // 当前pageIndex
//                    int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
//                    loadLvNewsData(curNewsCatalog, pageIndex, lvNewsHandler,
//                            UIHelper.LISTVIEW_ACTION_SCROLL);
//                }
//            }
//
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
//                        totalItemCount);
//            }
//        });
//        lvNews.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
//            public void onRefresh() {
//                loadLvNewsData(curNewsCatalog, 0, lvNewsHandler,
//                        UIHelper.LISTVIEW_ACTION_REFRESH);
//            }
//        });

        return lvNews_root;
    }

}
