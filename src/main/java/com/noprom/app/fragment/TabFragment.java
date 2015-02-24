package com.noprom.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noprom.app.R;
import com.noprom.app.adapter.TabAdapter;


public class TabFragment extends Fragment {

    private int pos;
    public TabFragment(int pos){
        this.pos = pos;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag, container, false);
        TextView tv = (TextView) view.findViewById(R.id.id_tv);
        tv.setText(TabAdapter.titles[pos]);
        return view;
    }
}
