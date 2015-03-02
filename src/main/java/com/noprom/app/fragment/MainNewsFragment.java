package com.noprom.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.noprom.app.R;
import com.noprom.app.adapter.ListViewNewsAdapter;
import com.noprom.app.bean.News;
import com.noprom.app.bean.NewsList;
import com.noprom.app.bean.Notice;
import com.noprom.app.common.UIHelper;
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
public class MainNewsFragment extends Fragment {

    private View lvNews_footer;
    private TextView lvNews_foot_more;
    private ProgressBar lvNews_foot_progress;
    private ListView lvNews_root;

    private PullToRefreshListView lvNews;
    private ListViewNewsAdapter lvNewsAdapter;
    private List<News> lvNewsData = new ArrayList<News>();
    private int lvNewsSumData;

    private Handler lvNewsHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lvNews_root = (ListView) inflater.inflate(R.layout.fragment_main_news,container,false);
        lvNewsAdapter = new ListViewNewsAdapter(getActivity(),lvNewsData,R.layout.news_listitem);
        lvNews_footer = inflater.inflate(R.layout.listview_footer, null);
        lvNews_foot_more = (TextView) lvNews_footer.findViewById(R.id.listview_foot_more);
        lvNews_foot_progress = (ProgressBar) lvNews_footer.findViewById(R.id.listview_foot_progress);
        lvNews = (PullToRefreshListView) lvNews_root.findViewById(R.id.listview_news);
        lvNews.addFooterView(lvNews_footer);    // 添加底部视图必须在setAdapter之间
        lvNews.setAdapter(lvNewsAdapter);

        // 设置每个选项的点击事件
        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击头部、底部栏目无效
                if(position == 0 || view ==lvNews_footer)
                    return;

                News news = null;
                if(view instanceof TextView){
                    news = (News) view.getTag();
                }else{
                    TextView tv = (TextView) view.findViewById(R.id.news_listitem_title);
                    news = (News) tv.getTag();
                }
                if(news == null)
                    return;

                // TODO 跳转到新闻详情页面

            }
        });

        // TODO 设置下滑滚动事件


        // 加载ListView数据
        this.initListViewNewsData();

        return lvNews_root;
    }


    /**
     * 加载ListView数据
     */
    private void initListViewNewsData() {
        // 初始化handler

    }

    /**
     * 获取listview的初始化Handler
     * @param lv
     * @param adapter
     * @param more
     * @param progress
     * @param pageSize
     * @return
     */
    private Handler getLvHandler(final PullToRefreshListView lv,
                                 final BaseAdapter adapter,final TextView more,
                                 final ProgressBar progress,final int pageSize){

        return new Handler(){
            @Override
            public void handleMessage(Message msg) {
//                super.handleMessage(msg);

            }
        };
    }

    /**
     * listview数据处理
     * @param what
     * @param obj
     * @param objtype
     * @param actiontype
     * @return
     */
    private Notice handleLvData(int what,Object obj,int objtype,int actiontype){
        Notice notice = null;
        switch (actiontype){
            case UIHelper.LISTVIEW_ACTION_INIT:
            case UIHelper.LISTVIEW_ACTION_REFRESH:
            case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
                int newdata = 0;    // 新加载数据，只有刷新动作才会使用到
                switch (objtype){
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
