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

package com.icecream.snorlax.module.feature.lure;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.module.Log;
import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.feature.mitm.MitmListener;
import com.icecream.snorlax.module.feature.mitm.MitmProvider;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static POGOProtos.Map.Fort.FortModifierOuterClass.FortModifier;
import static POGOProtos.Networking.Envelopes.ResponseEnvelopeOuterClass.ResponseEnvelope;
import static POGOProtos.Networking.Requests.RequestOuterClass.Request;
import static POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import static POGOProtos.Networking.Responses.FortDetailsResponseOuterClass.FortDetailsResponse;

@Singleton
public final class Lure implements Feature, MitmListener {

	private final MitmProvider mMitmProvider;
	private final LurePreferences mLurePreference;

	@Inject
	Lure(final MitmProvider mitmProvider, final LurePreferences lurePreferences) {
		mMitmProvider = mitmProvider;
		mLurePreference = lurePreferences;
	}

	@Override
	public void subscribe() {
		unsubscribe();
		mMitmProvider.addListenerResponse(this);
	}

	@Override
	public void unsubscribe() {
		mMitmProvider.removeListenerResponse(this);
	}

	@Override
	public ResponseEnvelope onMitm(final List<Request> requests, final ResponseEnvelope envelope) {
		for (int i = 0; i < requests.size(); i++) {
			if (!(requests.get(i).getRequestType() == RequestType.FORT_DETAILS)) {
				continue;
			}

			try {
				final ByteString processed = processFortDetail(FortDetailsResponse.parseFrom(envelope.getReturns(i)).toBuilder());
				if (processed == null) {
					continue;
				}

				return envelope.toBuilder().setReturns(i, processed).build();
			} catch (InvalidProtocolBufferException e) {
				Log.d("FortDetailsResponse failed: %s" + e.getMessage());
				Log.e(e);
			}
		}

		return envelope;
	}

	private ByteString processFortDetail(final FortDetailsResponse.Builder response) {
		if (!mLurePreference.isEnabled()) {
			return null;
		}

		final int nbModifier = response.getModifiersCount();
		for (int i = 0; i < nbModifier; i++) {
			final FortModifier.Builder modifier = response.getModifiers(i).toBuilder();

			modifier.setDeployerPlayerCodename(formatPlayerCodename(modifier));

			response.setModifiers(i, modifier);
		}

		return response.build().toByteString();
	}

	private String formatPlayerCodename(final FortModifier.Builder modifier) {
		final int remainingSeconds = (int) ((modifier.getExpirationTimestampMs() - (new Date()).getTime()) / 1000);
		if (remainingSeconds < 0) {
			return modifier.getDeployerPlayerCodename();
		}

		final int formatSeconds = remainingSeconds % 60;
		final int formatMinutes = remainingSeconds / 60;

		StringBuilder builder = new StringBuilder()
				.append(modifier.getDeployerPlayerCodename())
				.append(" - ")
				.append(formatMinutes > 0 ? formatMinutes + "min " : " ")
				.append(formatSeconds + "s");

		return builder.toString().trim();
	}
}
