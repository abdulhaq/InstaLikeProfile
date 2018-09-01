package life.haq.it.android.instalikeprofile;

/**
 * Created by Abdul Haq (it.haq.life) on 19-08-2018.
 */
        import android.annotation.TargetApi;
        import android.app.ActionBar;
        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AbsListView;
        import android.widget.AdapterView;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.Cache;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.VolleyLog;
        import com.android.volley.toolbox.ImageLoader;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.NetworkImageView;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.UnsupportedEncodingException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;

        import life.haq.it.android.instalikeprofile.adapter.ProfileAdapter;
        import life.haq.it.android.instalikeprofile.app.FollowButton;
        import life.haq.it.android.instalikeprofile.app.GridViewWithHeader;
        import life.haq.it.android.instalikeprofile.data.FeedItem;
        import life.haq.it.android.instalikeprofile.volley.AppController;

public class Profile extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    ImageLoader mImageLoader;
    private GridViewWithHeader gridView;
    private List<FeedItem> feedItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProfileAdapter GridAdapter;
    boolean loadingMore = false;

    private String URL_PROFILE;
    private String nextUrl;
    private int profileId;
    private String profileName;
    private JSONObject ProfileJsonData;

    private FollowButton followButton;
    private int FollowState;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setBackButton();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        profileId = 4;
        profileName = "Abdul Haq";
        URL_PROFILE = "http://it.haq.life/res/other/InstaLikeProfile.json";

        mImageLoader = AppController.getInstance().getImageLoader();


        View headerview = (View) getLayoutInflater().inflate(R.layout.row_profile_header, null);
        gridView = (GridViewWithHeader) findViewById(R.id.gridView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.SwipeToRefreshColor);

        gridView.addHeaderView(headerview);
        feedItems = new ArrayList<FeedItem>();
        //pagerItems = new ArrayList<SlideItem>();
        GridAdapter = new ProfileAdapter(this, feedItems);
        gridView.setAdapter(GridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView gridPhoto = (ImageView) view.findViewById(R.id.profileGridPhoto);
                String imgUrl = (String) gridPhoto.getTag();
                Intent photoIntent = new Intent(view.getContext(), ImagePagerView.class);
                //profileIntent.putExtra("name", nameText.getText());
                photoIntent.putExtra("id", profileId);
                photoIntent.putExtra("name", profileName);
                photoIntent.putExtra("url", imgUrl);
                photoIntent.putExtra("photoPosition", position-3);
                //Toast.makeText(getApplicationContext(), "Photo no: "+position, Toast.LENGTH_LONG).show();
                photoIntent.putExtra("ProfileUrl", URL_PROFILE);
                startActivity(photoIntent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                // swipeRefreshLayout.setRefreshing(true);
                LoadProfile();
            }
        });
    }

    @TargetApi(14)
    public void setBackButton() {
        actionBar.setIcon(R.drawable.nav_arrow_back);
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        LoadProfile();
    }

    public void LoadProfile() {
        //setting name and profile pic from previous intent
        TextView ProfileName = (TextView) findViewById(R.id.ProfileName);
        NetworkImageView profilePicture = (NetworkImageView) findViewById(R.id.profilePic);
        ProfileName.setText(profileName);
        profilePicture.setImageUrl("http://photos.opendp.co/avatar/"+profileId, mImageLoader);
        followButton = (FollowButton) findViewById(R.id.btnFollow);

        followButton.setOnClickListener(new View.OnClickListener() {
            // Runs when the user touches the button
            @Override
            public void onClick(View v) {
                // Same as TriToggleButton's _state
                // Will be 0, 1, or 2

                FollowState = followButton.getState();
                try {
                    switch (FollowState) {
                        case 0: // Do whatever is needed when the button changes to state 0
                            // It may be calling methods, setting variables, changing the UI, ect.
                            break;
                        case 1:
                            //case 1 means showing follow button. when follow button is clicked start showing following button
                            //and send request to server to follow profile
                            followButton.setState(2);
                            String follow_url = "http://api.opendp.co/add/follow?profile_id=";
                            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                                    follow_url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d("Error: " + error.getMessage());
                                    //Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                                }
                            });
                            // Adding request to volley request queue
                            AppController.getInstance().addToRequestQueue(jsonReq);
                            break;
                        case 2:
                            //case 1 means showing follow button. when follow button is clicked start showing following button
                            //and send request to server to follow profile
                            followButton.setState(1);
                            String unfollow_url = "http://api.opendp.co/add/unfollow?profile_id=";
                            JsonObjectRequest jsonReq1 = new JsonObjectRequest(Request.Method.GET,
                                    unfollow_url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d("Error: " + error.getMessage());
                                    //Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                                }
                            });
                            // Adding request to volley request queue
                            AppController.getInstance().addToRequestQueue(jsonReq1);
                            break;
                        default:
                            break; // Should never occur
                    }

//					Log.d(DEBUGTAG, "loopButton _state now::  " + loopButton.getState());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

                }
            }
        });

        //set title as profile name
        setTitle(profileName);

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_PROFILE);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    //feedItems.clear();
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                URL_PROFILE, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d("Response: " + response.toString());
                if (response != null) {
                    parseJsonFeed(response);
                    ProfileJsonData = response;
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                    }
                }, 2000);
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);

        //setting  listener on scroll event of the list
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //what is the bottom item that is visible
                int lastInScreen = firstVisibleItem + visibleItemCount;

                //is the bottom item visible & not loading more already? Load more!
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {

                    //perform action
                    //Set flag so we cant load new items 2 at the same time
                    loadingMore = true;
                    Log.d("LoadMore Command", "Load More please!");
                    //Toast.makeText(getApplicationContext(), "Load more please!", Toast.LENGTH_SHORT).show();
                    LoadMorePhotos();
                }
            }
        });
    }

    /**
     * Parsing json reponse and passing the data to feed view list aapter
     */
    private void parseJsonFeed(JSONObject response) {

        try {
            TextView ProfileName = (TextView) findViewById(R.id.ProfileName);
            TextView NumPics = (TextView) findViewById(R.id.NumPics);
            TextView NumFollowers = (TextView) findViewById(R.id.NumFollowers);
            TextView NumViews = (TextView) findViewById(R.id.NumViews);

            JSONObject profileDetails = (JSONObject) response;
            ProfileName.setText(profileDetails.getString("name"));
            NumPics.setText(profileDetails.getString("pictures_count"));
            NumFollowers.setText(profileDetails.getString("profile_followers"));
            NumViews.setText(profileDetails.getString("profile_views"));

            //make follow button as it should be
            String user_following = profileDetails.getString("user_following");
            if (user_following == "false") {

                followButton.setState(1);
            } else {
                followButton.setState(2);
            }

            JSONArray feedArray = response.getJSONArray("photos");
            feedItems.clear();
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                FeedItem item = new FeedItem();
                item.setProfilePic(feedObj.getString("thumbnail_url"));
                item.setImge(feedObj.getString("photo_url"));
                feedItems.add(item);
            }
            if (response.getJSONObject("paging") != null) {
                JSONObject paging = response.getJSONObject("paging");
                nextUrl = paging.optString("next");
            }

            // notify data changes to list adapater
            GridAdapter.notifyDataSetChanged();

            loadingMore = false;

        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
// stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }

    //load more request code below
    private void LoadMorePhotos() {

        // making fresh volley request and getting json
        if (nextUrl != null) {
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    nextUrl, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d("Response: " + response.toString());
                    if (response != null) {
                        try {
                            JSONArray feedArray = response.getJSONArray("photos");
                            for (int i = 0; i < feedArray.length(); i++) {
                                JSONObject feedObj = (JSONObject) feedArray.get(i);
                                FeedItem item = new FeedItem();
                                item.setProfilePic(feedObj.getString("thumbnail_url"));
                                item.setImge(feedObj.getString("photo_url"));
                                feedItems.add(item);
                            }

                            JSONObject paging = response.getJSONObject("paging");
                            nextUrl = paging.optString("next");

                            // notify data changes to list adapater
                            GridAdapter.notifyDataSetChanged();

                            loadingMore = false;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //swipeRefreshLayout.setRefreshing(false);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error: " + error.getMessage());
                    // stopping swipe refresh
                    //swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        } else {
            //Toast.makeText(getApplicationContext(), "Next url is null.", Toast.LENGTH_LONG).show();
            Log.d("LoadMore Command", "Next url is null.");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //menu click handler
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
