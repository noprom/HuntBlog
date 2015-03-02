package com.noprom.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noprom.app.R;
import com.noprom.app.adapter.ListViewNewsAdapter;
import com.noprom.app.bean.News;

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

    private ListViewNewsAdapter lvNewsAdapter;
    private List<News> lvNewsData = new ArrayList<News>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.initNewsListView(inflater);

        return inflater.inflate(R.layout.fragment_main_news, container, false);
    }

    /**
     * 初始化新闻资讯列表
     */
    public void initNewsListView(LayoutInflater inflater){
        lvNewsAdapter = new ListViewNewsAdapter(getActivity(),lvNewsData,R.layout.news_listitem);
        lvNews_footer = inflater.inflate(R.layout.list)

    }

}
