package com.test.news.services;

import com.test.news.entities.Post;

import java.util.List;

/**
 * Created by Nikita on 07.10.2014.
 */
public interface IServiceResultsListener {
    public void onReceive(List<Post> posts);
}
