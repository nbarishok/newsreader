package com.test.news.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.test.news.NewsApp;
import com.test.news.R;
import com.test.news.animations.HardwareAccelerationAnimationListener;
import com.test.news.entities.Post;
import com.test.news.entities.viewmodels.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikita on 07.10.2014.
 */
public class NewsAdapter extends BaseAdapter {

    ImageLoader mImageLoader;
    Context mContext;
    private List<Post> mViewModel;

    public NewsAdapter(Context context){
        //this(new ArrayList<Post>());
        this(new ArrayList<Post>(), context);

    }

    public NewsAdapter(List<Post> posts, Context context){
        mContext = context;
        mViewModel = posts;
        mImageLoader = NewsApp.getInstance().getImageLoader();
    }

    public void setNewPosts(List<Post> newPosts){
        if (newPosts == null) throw new NullPointerException("Can not work with NULL-collection");

        mViewModel = newPosts;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mViewModel.size();
    }

    @Override
    public Object getItem(int i) {
        return mViewModel.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PostViewHolder viewHolder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item, viewGroup, false);
            viewHolder =new PostViewHolder();
            viewHolder.mDescriptionHolder = (TextView) view.findViewById(R.id.tv_description);
            viewHolder.mAuthorHolder = (TextView) view.findViewById(R.id.tv_author);
            viewHolder.mTimeHolder = (TextView) view.findViewById(R.id.tv_time);
            viewHolder.mIsReadHolder = (ImageView) view.findViewById(R.id.iv_read_flag);
            viewHolder.mImageHolder = (NetworkImageView) view.findViewById(R.id.iv_network);

            view.setTag(viewHolder);
        }
        else{
            viewHolder = (PostViewHolder) view.getTag();
        }

        Post post = mViewModel.get(i);
        if (post != null){
            viewHolder.mDescriptionHolder.setText(post.getDescription());

            // URL to the full news  text & post# is stored in Descriptions' textview tag
            viewHolder.mDescriptionHolder.setTag(new PostReadViewModel(post.getResourceURL(), i));
            viewHolder.mAuthorHolder.setText(post.getAuthor());

            String timeString = post.getTime();
            viewHolder.mTimeHolder.setText(timeString);

            viewHolder.mIsReadHolder.setImageDrawable(mContext.getResources().getDrawable( post.isRead() ? R.drawable.read : R.drawable.not_read));
            viewHolder.mImageHolder.setImageUrl(post.getImageURL(), mImageLoader);
        }

        view.setAlpha(0);
        view.animate().alpha(1).setDuration(500).setListener(new HardwareAccelerationAnimationListener(view));

        return view;
    }

    public void updateIsRead(int postNo) {
        mViewModel.get(postNo).setIsRead(true);
    }

    static class PostViewHolder {
        TextView mDescriptionHolder;
        TextView mAuthorHolder;
        TextView mTimeHolder;
        ImageView mIsReadHolder;
        NetworkImageView mImageHolder;
    }
}
