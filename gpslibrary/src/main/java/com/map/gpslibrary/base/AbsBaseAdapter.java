package com.map.gpslibrary.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;


/**
 * 单布局万能适配器
 * Created by hyj on 2019/8/13
 */
public abstract class AbsBaseAdapter<T> extends BaseAdapter {

    protected Context mContext;//上下文
    private List<T> mDatas;//数据List
    private int mResId;//布局ID
    public int mCurrentPosition;//当前下标
    public int mSizeCount;//数据的总条数
    private View mEmptyView;//数据为空时需要展示的View

    /**
     * 构建方法
     * @param context 上下文
     * @param resid 布局ID
     */
    public AbsBaseAdapter(Context context, int resid){
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
    public void setDatas(List<T> datas){
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
    public void addDatas(List<T> datas) {
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
    public void removeDatas(int position){
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
    public T getData(int position){
        return mDatas.get(position);
    }

    /**
     * 获取全部数据
     * @return 全部数据
     */
    public List<T> getAllDatas(){
        return this.mDatas;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView != null){
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(mResId, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        mCurrentPosition = position;
        //给不同的控制设置值
        bindView(viewHolder, mDatas.get(position), position);

        return convertView;
    }

    //让子类重写得到ViewHolder、data、position
    public abstract void bindView(ViewHolder viewHolder, T data, int position);

    /**
     * ViewHolder作用：缓存item中的控件对象，避免多次findViewById
     */
    protected class ViewHolder{
        SparseArray<View> sparseArray = new SparseArray<>();
        View layoutView;
        public ViewHolder(View layoutView){
            this.layoutView = layoutView;
        }

        public View getView(int id){
            View view = sparseArray.get(id);
            if(view == null){
                view = layoutView.findViewById(id);
                sparseArray.put(id, view);
            }
            return view;
        }
        /**
         * 设置TextView
         * @param id
         * @param value
         * @return
         */
        public ViewHolder setTextView(int id, String value){
            TextView textView = (TextView) getView(id);
            textView.setText(value);
            return this;
        }

        /**
         * 设置图片
         * @param id
         * @param url
         * @return
         */
        public ViewHolder setImageView(int id, String url,int defaultimg){
            ImageView imageView = (ImageView) getView(id);
            Glide.with(mContext).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(defaultimg)
                    .dontAnimate()
                    .into(imageView);
            return this;
        }
    }
}
