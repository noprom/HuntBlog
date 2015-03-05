package com.noprom.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
<<<<<<< HEAD
import android.widget.ImageView;
import android.widget.TextView;

import com.noprom.app.R;
import com.noprom.app.bean.News;
import com.noprom.app.common.StringUtils;
=======
>>>>>>> parent of 4c8ab42... 【新增】listview_item and some dimens

import java.util.List;

/**
 * 新闻资讯Adapter类
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/3/2.
 */
<<<<<<< HEAD
public class ListViewNewsAdapter extends BaseAdapter {
    private Context mContext;// 上下文
    private List<News> listItems;// 数据集合
    private LayoutInflater listContainer;//视图容器
    private int itemViewResource;//自定义项视图源

    static class ListItemView {                //自定义控件集合
        public TextView title;
        public TextView author;
        public TextView date;
        public TextView count;
        public ImageView flag;
    }

    /**
     * 实例化Adapter
     * @param context
     * @param data
     * @param resource
     */
    public ListViewNewsAdapter(Context context, List<News> data,int resource) {
        this.mContext = context;
        this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
        this.itemViewResource = resource;
        this.listItems = data;
    }
=======
public class ListViewNewsAdapter extends BaseAdapter{
    private Context mContext;   // 上下文
    private List
>>>>>>> parent of 4c8ab42... 【新增】listview_item and some dimens

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
<<<<<<< HEAD

        //自定义视图
        ListItemView listItemView = null;
        if(convertView == null){
            // 获取list_item布局文件的视图
            convertView = listContainer.inflate(this.itemViewResource,null);
            listItemView = new ListItemView();
            // 获取控件对象
            listItemView.title = (TextView)convertView.findViewById(R.id.news_listitem_title);
            listItemView.author = (TextView)convertView.findViewById(R.id.news_listitem_author);
            listItemView.count= (TextView)convertView.findViewById(R.id.news_listitem_commentCount);
            listItemView.date= (TextView)convertView.findViewById(R.id.news_listitem_date);
            listItemView.flag= (ImageView)convertView.findViewById(R.id.news_listitem_flag);

            // 设置控件集到convertView
            convertView.setTag(listItemView);
        }else{
            listItemView = (ListItemView) convertView.getTag();
        }

        // 设置文字和图片
        News news = listItems.get(position);

        listItemView.title.setText(news.getTitle());
        listItemView.title.setTag(news);//设置隐藏参数(实体类)
        listItemView.author.setText(news.getAuthor());
        listItemView.date.setText(StringUtils.friendly_time(news.getPubDate()));
        listItemView.count.setText(news.getCommentCount()+"");

        if(StringUtils.isToday(news.getPubDate()))
            listItemView.flag.setVisibility(View.VISIBLE);
        else
            listItemView.flag.setVisibility(View.GONE);

        return convertView;
=======
        return null;
>>>>>>> parent of 4c8ab42... 【新增】listview_item and some dimens
    }
}
