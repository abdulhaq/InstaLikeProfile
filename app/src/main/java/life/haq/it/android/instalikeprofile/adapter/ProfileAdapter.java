package life.haq.it.android.instalikeprofile.adapter;

/**
 * Created by abdul_000 on 11/26/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import life.haq.it.android.instalikeprofile.R;
import life.haq.it.android.instalikeprofile.data.FeedItem;

public class ProfileAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    //com.android.volley.toolbox.ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private DisplayImageOptions options;

    public ProfileAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;

        options = new DisplayImageOptions.Builder()
                //.cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.grid_photos, null);

        /*if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader(); */

        ImageView girdPhoto = (ImageView) convertView.findViewById(R.id.profileGridPhoto);

        FeedItem item = feedItems.get(position);

        // set gird Photos
        ImageLoader.getInstance().displayImage(item.getProfilePic(), girdPhoto, options, new SimpleImageLoadingListener());
        //girdPhoto.setImageUrl(item.getProfilePic(), imageLoader);
        girdPhoto.setTag(item.getImge());

        TextView name = (TextView) convertView.findViewById(R.id.ProfileName);
        //set name if exsist
        if (item.getName() != null) {
            name.setText(item.getName());
            //set id in tag attribute of textview
            name.setTag(item.getId());
        }

        return convertView;
    }
}