package com.mycro.micro.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mycro.micro.R;
import com.mycro.micro.View.CircleImageView;
import com.mycro.micro.bean.StyleBean;

import java.util.List;


public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.StyleViewHolder> {

    //1. 创建视图控件
    //2. 为视图控件提供数据
    private Context mContext;
    private List<StyleBean> mStyleBeanList;

    //LayoutInflater 是用来将 layout.xml 布局文件添加到指定 View 中，或者是将 layout.xml 布局文件转化为对应的 View 对象。
    private LayoutInflater mLayoutInflater;

    public StyleAdapter(Context mContext, List<StyleBean> mStyleBeanList) {
        this.mContext = mContext;
        this.mStyleBeanList = mStyleBeanList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public StyleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 将 Layout 文件 加载 View
        View view = mLayoutInflater.inflate(R.layout.style_item_list, parent, false);
        StyleViewHolder styleViewHolder = new StyleViewHolder(view);
        return styleViewHolder;
    }

    @Override
    public int getItemCount() {
        return mStyleBeanList.size();
    }

    /**
     * 将视图和数据绑定在一起
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull StyleViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //其实这里绑定的都是单条数据,把单条数据取出来
        StyleBean newBean = mStyleBeanList.get(position);
        holder.mTitle.setText(newBean.getStyleName());
        holder.mIvImage.setImageResource(newBean.getImageResourceId());

        //点击事件，整个的recycleView是没办法实现点击事件的方法的
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "你点击了" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    class StyleViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;
        CircleImageView mIvImage;
        //获取对应单个item的view的外部布局
        LinearLayout rlContainer;

        public StyleViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.style_text);
            mIvImage = itemView.findViewById(R.id.style_image);
            rlContainer =itemView.findViewById(R.id.rl_item_container);
        }
    }

}
