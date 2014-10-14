package com.test.news.services;

import com.test.news.entities.Post;

import java.util.List;

/**
 * Interface for async retrieving data from remote sources
 * In our case there is only one listener
 */
public interface IPostsReceiver {
    public void getPostsAsync();
    void notifyPostsReceived(List<Post> posts);
    public void addListener(IServiceResultsListener listener);
    public void removeListener();
    public boolean isProcessing();
}
