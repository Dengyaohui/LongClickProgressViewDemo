package com.map.gpslibrary.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView万能适配器
 * Created by Gavin on 2020/8/21
 **/
public abstract class AbsBaseRecyclerViewAdapter<D,V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    protected Context mContext;//上下文
    protected List<D> mDatas;//数据List
    protected int mResId;//布局ID
    public int mCurrentPosition;//当前下标
    public int mSizeCount;//数据的总条数
    private View mEmptyView;//数据为空时需要展示的View

    /**
     * 构建方法
     * @param context 上下文
     *
     */
    public AbsBaseRecyclerViewAdapter(Context context){
        this.mContext = context;
        this.mDatas = new ArrayList<>();
    }

    /**
     * 构建方法
     * @param context 上下文
     * @param resid 布局ID
     */
    public AbsBaseRecyclerViewAdapter(Context context, int resid){
        this.mContext = context;
        this.mResId = resid;
        this.mDatas = new ArrayList<>();
    }

    /**
     * 设置空数据时展示的View
     * @param emptyView
     */
    public void setEmptyView(View emptyView){
        this.mEmptyView = emptyView;
        this.mEmptyView.setVisibility(View.VISIBLE);
    }

    /**
     * set数据并且同时刷新页面
     * @param datas
     */
    public void setDatas(List<D> datas){
        Log.e("hyj", "setDatas: ======>" );
        this.mDatas = datas;
        mSizeCount = datas.size();
        if(mSizeCount == 0){
            if(this.mEmptyView != null){
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if(this.mEmptyView != null){
                mEmptyView.setVisibility(View.GONE);
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * addAll数据并且同时刷新页面
     * @param datas
     */
    public void addDatas(List<D> datas) {
        this.mDatas.addAll(datas);
        mSizeCount = this.mDatas.size();
        if(mSizeCount == 0){
            if(this.mEmptyView != null){
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if(this.mEmptyView != null){
                mEmptyView.setVisibility(View.GONE);
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * 移除position下标的数据并刷新页面
     * @param position
     */
    public void removeData(int position){
        this.mDatas.remove(position);
        this.notifyDataSetChanged();
    }

    /**
     * 获取当前下标
     * @return
     */
    public int getCurrentPosition(){
        return mCurrentPosition;
    }

    /**
     * 获取单个数据
     * @param position
     * @return
     */
    public D getData(int position){
        return mDatas.get(position);
    }

    /**
     * 获取全部数据
     * @return 全部数据
     */
    public List<D> getAllDatas(){
        return this.mDatas;
    }

    /**
     * 获取AbsBaseRecyclerViewHolder
     * @param parent 根布局View
     * @param resource 布局ID
     * @return AbsBaseRecyclerViewHolder
     */
    public AbsBaseRecyclerViewHolder getViewHolder(@NonNull ViewGroup parent, @LayoutRes int resource){
        return new AbsBaseRecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(resource, parent, false));
    }

    @Override
    public int getItemCount() {
        return this.mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
