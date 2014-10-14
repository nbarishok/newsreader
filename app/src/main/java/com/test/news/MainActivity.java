package com.test.news;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.news.adapters.NewsAdapter;
import com.test.news.animations.AnimUtils;
import com.test.news.entities.Post;
import com.test.news.entities.viewmodels.PostReadViewModel;
import com.test.news.services.IPostsReceiver;
import com.test.news.services.IServiceResultsListener;
import com.test.news.services.PostsReceiverImpl;
import com.test.news.utilities.DateTimeUtilities;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements IServiceResultsListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private IPostsReceiver mReceiver;

    private final static String PostsSaveKey = "com.test.news.mainactivity.postssavekey";

    private TextView mLastUpdate;
    private String mLastUpdateCache;

    private LinearLayout mUpdateButton;
    private ListView mList;

    private ViewGroup mContainer;

    private NewsAdapter mAdapter;
    private ArrayList<Post> mPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState != null){
            mPosts = savedInstanceState.getParcelableArrayList(PostsSaveKey);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLastUpdate = (TextView) findViewById(R.id.tv_update);
        mUpdateButton = (LinearLayout) findViewById(R.id.ll_update);

        mList = (ListView) findViewById(R.id.list);
        mContainer = (ViewGroup) findViewById(R.id.container);

        View empty = getLayoutInflater().inflate(R.layout.lv_empty_view, null, false);
        addContentView(empty, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mList.setEmptyView(empty);

        mAdapter = mPosts == null ? new NewsAdapter(this) : new NewsAdapter(mPosts, this);
        mList.setAdapter(mAdapter);
        mReceiver = new PostsReceiverImpl();
        mLastUpdate.setText(mLastUpdateCache == null ? getString(R.string.was_not_updated) : mLastUpdateCache);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mReceiver.addListener(this);
        mUpdateButton.setOnClickListener(this);
        mList.setOnItemClickListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mUpdateButton.setOnClickListener(null);
        mList.setOnItemClickListener(null);
        mList.setOnScrollListener(null);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mList != null){
            enableUpdateButtonAutoHide(mList);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        state.putParcelableArrayList(PostsSaveKey, mPosts);
        super.onSaveInstanceState(state);
    }

    @Override
    public void onReceive(List<Post> posts) {
        if (posts == null) //error
        {
            mLastUpdate.setText(mLastUpdateCache);
            return;
        }
        this.mPosts = new ArrayList<Post>(posts);
        mAdapter.setNewPosts(posts);
        String tvUpdateText = getString(R.string.tv_update_string_base) + DateTimeUtilities.getDateTimeNowString();
        mLastUpdate.setText(tvUpdateText);
        mLastUpdateCache = tvUpdateText;
    }

    @Override
    public void onClick(View view) {
        if (mReceiver.isProcessing()) {
            Toast.makeText(this, getString(R.string.processing_now), Toast.LENGTH_SHORT).show();
            return;
        }
        mReceiver.getPostsAsync();
        mLastUpdate.setText(getString(R.string.tv_loading));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        PostReadViewModel vm = (PostReadViewModel)view.findViewById(R.id.tv_description).getTag();

        if (vm == null) throw new NullPointerException("Url was not found");

        String redirText = getString(R.string.redir_string_base).concat(vm.getUrl());
        Toast.makeText(this, redirText, Toast.LENGTH_SHORT).show();

        ImageView iv = (ImageView) view.findViewById(R.id.iv_read_flag);
        iv.setImageDrawable(this.getResources().getDrawable( R.drawable.read));

        // Actually information about news being read must be stored in db
        // but for test case purposes it is enough
        mAdapter.updateIsRead(vm.getPostNo());
    }


    //region Hiding | Showing Update Button

    private boolean mUpdateButtonAutoHideEnabled = false;
    private boolean mUpdateButtonShown = true;
    private int mUpdateButtonAutoHideMinY;
    private int mUpdateButtonAutoHideSensivity;
    private int mUpdateButtonAutoHideSignal = 0;

    private void initUpdateButtonAutoHide() {
        mUpdateButtonAutoHideEnabled = true;
        mUpdateButtonAutoHideMinY = 152;
        mUpdateButtonAutoHideSensivity = 48;
    }

    protected void enableUpdateButtonAutoHide(final ListView listView) {
        initUpdateButtonAutoHide();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            final static int ITEMS_THRESHOLD = 3;
            int lastFvi = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE,
                        lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE :
                                lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE
                );
                lastFvi = firstVisibleItem;
            }
        });
    }

    private void onMainContentScrolled(int currentY, int deltaY) {
        if (deltaY > mUpdateButtonAutoHideSensivity) {
            deltaY = mUpdateButtonAutoHideSensivity;
        } else if (deltaY < -mUpdateButtonAutoHideSensivity) {
            deltaY = -mUpdateButtonAutoHideSensivity;
        }

        if (Math.signum(deltaY) * Math.signum(mUpdateButtonAutoHideSignal) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            mUpdateButtonAutoHideSignal = deltaY;
        } else {
            // add to accumulated signal
            mUpdateButtonAutoHideSignal += deltaY;
        }

        boolean shouldShow = currentY < mUpdateButtonAutoHideMinY ||
                (mUpdateButtonAutoHideSignal <= -mUpdateButtonAutoHideSensivity);
        autoShowOrHideUpdateButton(shouldShow);
    }

    protected void autoShowOrHideUpdateButton(boolean show) {
        if (show == mUpdateButtonShown) {
            return;
        }

        mUpdateButtonShown = show;
        showHideUpdateButton(show);
    }

    public void showHideUpdateButton(boolean show) {
        if (show) {
            AnimUtils.doYTranslation(mUpdateButton, 0);
        } else {
            AnimUtils.moveBehindParentBottom(mUpdateButton, mContainer);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
