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

package com.icecream.snorlax.module.feature.capture;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.common.rx.RxBus;
import com.icecream.snorlax.common.rx.RxFuncitons;
import com.icecream.snorlax.module.Log;
import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.feature.mitm.MitmRelay;

import POGOProtos.Networking.Requests.RequestTypeOuterClass;
import rx.Observable;
import rx.Subscription;

import static POGOProtos.Networking.Requests.RequestOuterClass.Request;
import static POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import static POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse;

@Singleton
public final class Capture implements Feature {

	private final MitmRelay mMitmRelay;
	private final CapturePreferences mPreferences;
	private final CaptureNotification mCaptureNotification;
	private final RxBus mRxBus;

	private Subscription mSubscription;

	@Inject
	Capture(MitmRelay mitmRelay, CapturePreferences preferences, CaptureNotification captureNotification, RxBus rxBus) {
		mMitmRelay = mitmRelay;
		mPreferences = preferences;
		mCaptureNotification = captureNotification;
		mRxBus = rxBus;
	}

	private void onCapture(ByteString bytes) {
		try {
			CatchPokemonResponse response = CatchPokemonResponse.parseFrom(bytes);

			final CatchPokemonResponse.CatchStatus status = response.getStatus();

			if (mPreferences.isEnabled() && !status.equals(CatchPokemonResponse.CatchStatus.CATCH_MISSED)) {
				mCaptureNotification.show(formatCapture(response.getStatus().name()));
			}
			mRxBus.post(new CaptureEvent(status));
		}
		catch (InvalidProtocolBufferException e) {
			Log.d("CatchPokemonResponse failed: %s" + e.getMessage());
			Log.e(e);
		}
	}

	private String formatCapture(String status) {
		StringBuilder builder = new StringBuilder();
		for (String part : status.split("_")) {
			builder
				.append(part.charAt(0))
				.append(part.substring(1).toLowerCase(Locale.US))
				.append(" ");
		}
		return builder.toString().trim();
	}

	@Override
	public void subscribe() {
		unsubscribe();

		mSubscription = mMitmRelay
			.getObservable()
			.flatMap(envelope -> {
				List<Request> requests = envelope.getRequest().getRequestsList();

				for (int i = 0; i < requests.size(); i++) {
					RequestTypeOuterClass.RequestType type = requests.get(i).getRequestType();

					if (type == RequestType.CATCH_POKEMON) {
						return Observable.just(envelope.getResponse().getReturns(i));
					}
				}
				return Observable.empty();
			})
			.subscribe(this::onCapture, Log::e);
	}

	@Override
	public void unsubscribe() {
		RxFuncitons.unsubscribe(mSubscription);
	}
}
