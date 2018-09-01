/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package life.haq.it.android.instalikeprofile.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import life.haq.it.android.instalikeprofile.R;
import life.haq.it.android.instalikeprofile.app.TouchImageView;
import life.haq.it.android.instalikeprofile.volley.AppController;

import static com.android.volley.VolleyLog.TAG;

public class ImagePagerFragment extends Fragment {

	public int INDEX = 2;
	private ViewPagerAdapter ViewPagerAdapter;
	private ViewPager pager;
	private String imageURL;
	private int IMAGE_URLS_LENGTH;
	private ArrayList<String> IMAGE_URLS;
	private String URL_PROFILE;
	private int photoPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_image_pager, container, false);
		pager = (ViewPager) rootView.findViewById(R.id.pager);
		ViewPagerAdapter = new ViewPagerAdapter(getActivity());
		pager.setAdapter(ViewPagerAdapter);

		Intent profileValues = getActivity().getIntent();
		int profileId = profileValues.getIntExtra("id", 4);
		String ProfileName = profileValues.getStringExtra("name");
		imageURL = profileValues.getStringExtra("url");
		photoPosition = profileValues.getIntExtra("photoPosition",0);
		URL_PROFILE = profileValues.getStringExtra("ProfileUrl");

		//setting name and profile pic from previous intent
		TextView nameText = (TextView) rootView.findViewById(R.id.name);
		NetworkImageView avatar = (NetworkImageView) rootView.findViewById(R.id.profilePic);
		nameText.setText(ProfileName);
		ImageLoader.getInstance().displayImage("http://photos.opendp.co/avatar/"+profileId, avatar, new SimpleImageLoadingListener());

		Log.d(TAG, "Photo Position: " + photoPosition);

		IMAGE_URLS = new ArrayList<String>();
		LoadMorePager(URL_PROFILE);

		return rootView;
	}

	public void LoadMorePager(String LoadUrl) {
		// We first check for cached request
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Cache.Entry entry = cache.get(LoadUrl);
		if (entry != null) {
			// fetch the data from cache
			try {
				String data = new String(entry.data, "UTF-8");
				try {
					//feedItems.clear();
					setViewPager(new JSONObject(data));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			// making fresh volley request and getting json
			JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
					LoadUrl, null, new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					VolleyLog.d(TAG, "Response: " + response.toString());
					if (response != null) {
						setViewPager(response);
					}
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.d(TAG, "Error: " + error.getMessage());
					//Toast.makeText(view.getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
				}
			});
			// Adding request to volley request queue
			AppController.getInstance().addToRequestQueue(jsonReq);
		}
	}

    public void setViewPager(JSONObject response) {
		try {
			JSONObject profileDetails = (JSONObject) response;

			JSONArray feedArray = response.getJSONArray("photos");

			for (int i = 0; i < feedArray.length(); i++) {

				JSONObject feedObj = (JSONObject) feedArray.get(i);
				IMAGE_URLS.add(feedObj.getString("photo_url"));
			}

			if (profileDetails.getInt("total") > 15) {
				JSONObject paging = response.getJSONObject("paging");
				String nextUrl = paging.optString("next");

				if (nextUrl != null) {
					LoadMorePager(nextUrl);
				}
			}

			IMAGE_URLS_LENGTH = profileDetails.getInt("total");
			// notify data changes to list adapater
			ViewPagerAdapter.notifyDataSetChanged();
			pager.setCurrentItem(photoPosition);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private class ViewPagerAdapter extends PagerAdapter {

		private LayoutInflater inflater;
		private DisplayImageOptions options;

		ViewPagerAdapter(Context context) {
			inflater = LayoutInflater.from(context);

			options = new DisplayImageOptions.Builder()
					//.showImageForEmptyUri(R.drawable.explore_icon)
					//.showImageOnFail(R.drawable.explore_icon_selected)
					.showImageOnLoading(R.drawable.loading_pic_dots)
					.resetViewBeforeLoading(true)
					.cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300))
					.build();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return IMAGE_URLS_LENGTH;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.iv_photo);
			//final ImageView spinner = (ImageView) imageLayout.findViewById(R.id.loading);

			ImageLoader.getInstance().displayImage(IMAGE_URLS.get(position), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					//spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

					//spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					//spinner.setVisibility(View.GONE);
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}
}