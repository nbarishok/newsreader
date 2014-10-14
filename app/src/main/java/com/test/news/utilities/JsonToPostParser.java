package com.test.news.utilities;

import com.test.news.entities.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikita on 07.10.2014.
 */
public class JsonToPostParser {
    private static final String sRootTAG = "result";
    private static final String sErrorTAG = "error";
    private static final String sNewsTAG = "news";
    private static final String sAuthorTAG = "author";
    private static final String sDescriptionTAG = "description";
    private static final String sImageUrlTAG = "image";
    private static final String sResourceUrlTAG = "url";
    private static final String sTimestampTAG = "timestamp";

    public static List<Post> parse(JSONObject jsonObject) throws JSONException{
        List<Post> result = new ArrayList<Post>();

        JSONObject root = jsonObject.getJSONObject(sRootTAG);
        String error = root.getString(sErrorTAG);
        if (error != "null") throw new JSONException(error);

        JSONArray news = root.getJSONArray(sNewsTAG);
        for (int i = 0; i<news.length(); i++){
            JSONObject obj = news.getJSONObject(i);
            Post post = parsePost(obj);
            result.add(post);

        }
        return result;
    }

    private static Post parsePost(JSONObject obj) throws JSONException{
        String lAuthor = obj.getString(sAuthorTAG);
        String lDescription = obj.getString(sDescriptionTAG);
        String lImageUrl = obj.getString(sImageUrlTAG);
        String lResourceUrl = obj.getString(sResourceUrlTAG);
        int lTimestamp = obj.getInt(sTimestampTAG);

        return  new Post(lDescription, lAuthor, lImageUrl,lResourceUrl, lTimestamp);
    }
}