package com.test.news.entities.viewmodels;

/**
 * Created by Nikita on 14.10.2014.
 */
public class PostReadViewModel{
    String mUrl;
    int mPostNo;

    public PostReadViewModel(String url, int postNo){
        mUrl = url;
        mPostNo = postNo;
    }

    public String getUrl() {return mUrl;}
    public int getPostNo() { return mPostNo; }
}
