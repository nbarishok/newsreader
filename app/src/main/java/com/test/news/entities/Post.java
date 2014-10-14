package com.test.news.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.test.news.utilities.DateTimeUtilities;

import java.net.URL;

/**
 * Created by Nikita on 07.10.2014.
 */
public class Post implements Parcelable {

    private String mDescription;
    private String mAuthor;
    private String mImageUrl;

    private String mResourceUrl;
    private int mTimestamp;

    private boolean mIsRead;

    public Post(String description, String resourceRef, int timestamp){
        this(description, null,null, resourceRef, timestamp);

    }

    public Post(String description, String author, String imageURL, String resourceRef, int timestamp){
        mDescription = description;
        mAuthor = author;
        mImageUrl = imageURL;
        mResourceUrl = resourceRef;
        mTimestamp = timestamp;

        mIsRead = false;
    }

    public void setIsRead(boolean value){ mIsRead = value; }
    public String getAuthor() {return  mAuthor; }
    public String getDescription(){ return mDescription; }
    public String getImageURL() {return mImageUrl; }
    public String getResourceURL () { return  mResourceUrl; }

    //Parcelable part
    public Post(Parcel in){
        String[] data = new String[4];
        in.readStringArray(data);
        this.mDescription = data[0];
        this.mAuthor = data[1];
        this.mImageUrl = data[2];
        this.mResourceUrl = data[3];

        this.mIsRead = in.readByte() != 0;
        this.mTimestamp = in.readInt();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.mDescription,
                this.mAuthor,
                this.mImageUrl,
                mResourceUrl});
        dest.writeByte((byte) (mIsRead ? 1 : 0));
        dest.writeInt(mTimestamp);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public boolean isRead() {
        return mIsRead;
    }

    public String getTime(){
        return DateTimeUtilities.parseTimestamp(mTimestamp);
    }
}
