package com.test.news.services;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.test.news.NewsApp;
import com.test.news.R;
import com.test.news.entities.Post;
import com.test.news.utilities.JsonToPostParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Nikita on 07.10.2014.
 */
public class PostsReceiverImpl implements IPostsReceiver, Response.Listener<JSONObject>, Response.ErrorListener{

    //TODO encapsulate logic of retrieving API UPL
    private static final String ApiEntryPoint = "http://novo.wada.vn/api/news?top=10&format=json&lang=en";

    private static final String TagJsonObj = "json_obj_req_news";

    private IServiceResultsListener mListener;
    private boolean mIsProcessing = false;
    @Override
    public void onResponse(JSONObject jsonObject) {
        try{
            List<Post> posts = JsonToPostParser.parse(jsonObject);
            mIsProcessing = false;
            notifyPostsReceived(posts);
        }
        catch (JSONException ex){
            //TODO logic to handle json parsing exceptions
            Toast.makeText(NewsApp.getInstance(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        //TODO add logic for proper exception handling / message showing

        Toast.makeText(NewsApp.getInstance(),NewsApp.getInstance().getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        mIsProcessing = false;
        notifyPostsReceived(null);
    }

    @Override
    public void getPostsAsync() {
        mIsProcessing = true;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, ApiEntryPoint, null, this, this);
        NewsApp.getInstance().addToRequestQueue(jsObjRequest, TagJsonObj);
    }

    @Override
    public void notifyPostsReceived(List<Post> posts) {
        if (mListener == null) throw new NullPointerException("No listeners to represent data from service");

        mListener.onReceive(posts);
    }

    @Override
    public void addListener(IServiceResultsListener listener) {
        mListener = listener;
    }

    @Override
    public void removeListener() {
        mListener = null;
    }

    @Override
    public boolean isProcessing() {
        return mIsProcessing;
    }
}
