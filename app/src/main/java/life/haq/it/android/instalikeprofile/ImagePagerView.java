/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
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
package life.haq.it.android.instalikeprofile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import life.haq.it.android.instalikeprofile.adapter.ImagePagerFragment;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerView extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		int frIndex = getIntent().getIntExtra("yes", 0);
		Fragment fr;
		String tag;
		tag = ImagePagerFragment.class.getSimpleName();
		fr = getSupportFragmentManager().findFragmentByTag(tag);
		if (fr == null) {
			fr = new ImagePagerFragment();
			fr.setArguments(getIntent().getExtras());
		}

		setTitle("View photo");
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
    }
	public void nameClick(View v) {
		finish();
	}
	public void imageClick(View v) {
		LinearLayout PhotoViewHead = (LinearLayout) findViewById(R.id.photo_view_head);
		if (PhotoViewHead.getVisibility() == View.VISIBLE) {
			PhotoViewHead.setVisibility(View.INVISIBLE);
		} else {
			PhotoViewHead.setVisibility(View.VISIBLE);
		}
	};
}