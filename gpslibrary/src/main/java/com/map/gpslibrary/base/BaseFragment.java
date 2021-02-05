package com.map.gpslibrary.base;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.map.gpslibrary.R;
import com.map.gpslibrary.utils.ScreenUtils;

import butterknife.ButterKnife;

/**
 * Fragment基类
 * Created by hyj on 2016/9/26 0026.
 */
public abstract class BaseFragment extends Fragment {
    public static Context fragmentContext;

    private FragmentManager fragmentManager;
    //当前正在展示的Fragment
    private BaseFragment showFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentId(), container, false);
        fragmentContext = getContext();
        initView(view, savedInstanceState);
        return view;
    }

    public static Context getFragmentContext() {
        return fragmentContext;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        //获得fragmentManager对象
        fragmentManager = getChildFragmentManager();
        //查找actionbar控件设置paddingtop
        BaseActivity activity = (BaseActivity) getActivity();
        //判断是否开启沉浸状态栏
        if (activity.isOpenStatus()) {
        View actionbarview = view.findViewById(R.id.actionbar);
            if (actionbarview != null) {
                //获得状态栏的高度
                int height = ScreenUtils.getStatusHeight(getActivity());
                actionbarview.setPadding(0, height, 0, 0);
            }
        }
        initMap(view, savedInstanceState);
        init(view);
        bindListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            getBundle(bundle);
        }
        loadDatas();
    }

    protected void getBundle(Bundle bundle) {

    }

    /**
     * 绑定监听
     */
    protected void bindListener() {

    }

    /**
     * 加载数据
     */
    protected void loadDatas() {

    }

    /**
     * 初始化
     */
    protected void init(View view) {

    }

    protected void initMap(View view, @Nullable Bundle savedInstanceState){

    }

    /**
     * 初始化View
     */
    protected void initView(View view,@Nullable Bundle savedInstanceState) {

    }

    protected abstract int getContentId();




    /**
     * 显示隐藏Fragment
     */
    protected void showFragment(int resid, BaseFragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //隐藏正在暂时的Fragment
        if (showFragment != null) {
            fragmentTransaction.hide(showFragment);
        }
        //展示需要显示的Fragment对象
        Fragment mFragment = fragmentManager.findFragmentByTag(fragment.getClass().getName());
        if (mFragment != null) {
            fragmentTransaction.show(mFragment);
            showFragment = (BaseFragment) mFragment;
        } else {
            fragmentTransaction.add(resid, fragment, fragment.getClass().getName());
            showFragment = fragment;
        }
        fragmentTransaction.commit();
    }
}
