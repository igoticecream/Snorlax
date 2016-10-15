/*
 * Copyright (c) 2016. Pedro Diaz <igoticecream@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icecream.snorlax.app.map;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icecream.snorlax.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

@SuppressWarnings("FieldCanBeLocal")
public class MapsActivity extends AppCompatActivity {

	@BindView(R.id.toolbar)
	Toolbar mToolbar;

	private GoogleMap mMap;
	private SupportMapFragment mMapFragment;

	private Unbinder mUnbinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		mUnbinder = ButterKnife.bind(this);

		setupToolbar();
		setupMap();
	}

	private void setupToolbar() {
		setSupportActionBar(mToolbar);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	private void setupMap() {
		mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mMapFragment.getMapAsync(map -> {
			mMap = map;

			try {
				if (!mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_pogo))) {
					Timber.d("Cannot set map style");
				}
			}
			catch (Resources.NotFoundException e) {
				Timber.d("Cannot find map style");
			}

			LatLng ucab = new LatLng(10.465231614627145, -66.97446120465979);
			mMap.addMarker(new MarkerOptions().position(ucab).title("Marker in Sydney"));
			mMap.moveCamera(CameraUpdateFactory.newLatLng(ucab));
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUnbinder.unbind();
	}
}
