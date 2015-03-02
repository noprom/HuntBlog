package com.noprom.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noprom.app.bean.News;

import java.util.List;

/**
 * 新闻资讯Adapter类
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/3/2.
 */
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
     *
     * @param context
     * @param listItems
     * @param listContainer
     * @param itemViewResource
     */
    public ListViewNewsAdapter(Context context, List<News> listItems, LayoutInflater listContainer, int itemViewResource) {
        mContext = context;
        this.listItems = listItems;
        this.listContainer = listContainer;
        this.itemViewResource = itemViewResource;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * ListView Item设置
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //自定义视图
        ListItemView listItemView = null;
        if(convertView == null){
            // 获取list_item布局文件的视图
            convertView = listContainer.inflate(this.itemViewResource,null);
            listItemView = new ListItemView();
            // 获取控件对象
            listItemView.title = convertView.findViewById(R.id.new)
        }

        return convertView;
    }
}
