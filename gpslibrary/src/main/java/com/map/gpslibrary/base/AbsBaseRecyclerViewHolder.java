package com.map.gpslibrary.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * RecyclerView万能ViewHolder
 * Created by Gavin on 2020/8/21
 **/
public class AbsBaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    //采用稀疏数组，性能较高的SparseArray
    private SparseArray<View> sparseArray = new SparseArray<>();

    private View layoutView;//根布局

    public AbsBaseRecyclerViewHolder(View layoutView) {
        super(layoutView);
        this.layoutView = layoutView;
    }

    /**
     * 获取View
     *
     * @param id
     * @return
     */
    public View getView(int id) {
        View view = sparseArray.get(id);
        if (view == null) {
            view = layoutView.findViewById(id);
            sparseArray.put(id, view);
        }
        return view;
    }

    /**
     * 设置TextView
     *
     * @param id
     * @param value
     * @return
     */
    public AbsBaseRecyclerViewHolder setTextView(int id, String value) {
        TextView textView = (TextView) getView(id);
        textView.setText(value);
        return this;
    }

    /**
     * 设置图片
     *
     * @param id
     * @param url
     * @return
     */
    public AbsBaseRecyclerViewHolder setImageView(Context context, int id, String url, int defaultimg) {
        ImageView imageView = (ImageView) getView(id);
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(defaultimg)
                .dontAnimate()
                .into(imageView);
        return this;
    }
}