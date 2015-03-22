
package com.noprom.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noprom.app.AppContext;
import com.noprom.app.AppException;
import com.noprom.app.R;
import com.noprom.app.bean.MyInformation;
import com.noprom.app.common.UIHelper;
import com.noprom.app.ui.LoginActivity;

/**
 * 综合Tab MeFragment
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2014-2-25.
 */
public class MeFragment extends Fragment {
    private final String TAG = "MeFragment";


    private ImageView mUserInfoFace;
    private TextView mUserInfoUsername;
    private TextView mUserInfoScore;
    private TextView mUserInfoFavorite;
    private TextView mUserInfoFollows;
    private TextView mUserInfoFans;

    private MyInformation user;
    private Handler mHandler;

    private boolean mSearchCheck;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_me, container, false);
        rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // 初始化视图控件
        mUserInfoFace = (ImageView) rootView.findViewById(R.id.user_info_face);
        mUserInfoUsername = (TextView) rootView.findViewById(R.id.user_info_username);
        mUserInfoScore = (TextView) rootView.findViewById(R.id.user_info_score);
        mUserInfoFavorite = (TextView) rootView.findViewById(R.id.user_info_favorite);
        mUserInfoFollows = (TextView) rootView.findViewById(R.id.user_info_followers);
        mUserInfoFans = (TextView) rootView.findViewById(R.id.user_info_fans);



        // 初始化点击事件
        mUserInfoFace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("LOGINTYPE", 0x00);
                startActivity(intent);
            }
        });

        // 初始化视图数据
        this.initData();

        return rootView;
    }

    /**
     * 初始化视图数据
     */
    private void initData() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1 && msg.obj !=null){
                    user = (MyInformation) msg.obj;
                    // 加载用户头像
                    UIHelper.showUserFace(mUserInfoFace,user.getFace());

                    
                }
            }
        };
        this.loadUserInfoThread(false);
    }

    private void loadUserInfoThread(final boolean isRefresh) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try{
                    MyInformation user = ((AppContext) getActivity().getApplication()).getMyInformation(isRefresh);
                    msg.what = 1;
                    msg.obj = user;
                }catch (AppException e){
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                mHandler.sendMessage(msg);
            }
        }).start();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);

        //Select search item
        final MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setVisible(true);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(this.getString(R.string.search));

        ((EditText) searchView.findViewById(R.id.search_src_text))
                .setHintTextColor(getResources().getColor(R.color.nliveo_white));
        searchView.setOnQueryTextListener(onQuerySearchView);

        menu.findItem(R.id.menu_add).setVisible(true);

        mSearchCheck = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_add:
                Toast.makeText(getActivity(), R.string.add, Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_search:
                mSearchCheck = true;
                Toast.makeText(getActivity(), R.string.search, Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (mSearchCheck) {
                // implement your search here
            }
            return false;
        }
    };
}
