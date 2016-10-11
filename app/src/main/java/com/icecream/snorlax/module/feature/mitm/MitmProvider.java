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

package com.icecream.snorlax.module.feature.mitm;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.util.LongSparseArray;

import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.module.Log;

import static POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope;
import static POGOProtos.Networking.Envelopes.ResponseEnvelopeOuterClass.ResponseEnvelope;
import static POGOProtos.Networking.Requests.RequestOuterClass.Request;

@Singleton
@SuppressWarnings("unused")
public final class MitmProvider {

	private final LongSparseArray<List<Request>> mRequests;
	private final List<MitmListener> mListeners;

	@Inject
	MitmProvider(LongSparseArray<List<Request>> requests) {
		mRequests = requests;
		mListeners = new ArrayList<>();
	}

	public void addListenerResponse(MitmListener listener) {
		if (listener != null) {
			mListeners.add(listener);
		}
	}

	public void removeListenerResponse(MitmListener listener) {
		if (listener != null) {
			mListeners.remove(listener);
		}
	}

	ByteBuffer processOutboundPackage(ByteBuffer roData, boolean connectionOk) {
		if (!connectionOk)
			return null;

		roData.rewind();

		try {
			byte[] buffer = new byte[roData.remaining()];
			roData.get(buffer);

			synchronized (MitmProvider.class) {
				RequestEnvelope envelope = RequestEnvelope.parseFrom(buffer);
				MitmRelay.getInstance().call(envelope);

				setRequests(envelope);
			}
		}
		catch (InvalidProtocolBufferException ignored) {
		}
		catch (Throwable throwable) {
			Log.e(throwable);
		}

		return null;
	}

	private void setRequests(RequestEnvelope envelope) {
		mRequests.put(
			envelope.getRequestId(),
			envelope.getRequestsList()
		);
	}

	ByteBuffer processInboundPackage(ByteBuffer roData, boolean connectionOk) {
		if (!connectionOk)
			return null;

		roData.rewind();

		try {
			byte[] buffer = new byte[roData.remaining()];
			roData.get(buffer);

			synchronized (MitmProvider.class) {
				ResponseEnvelope envelope = ResponseEnvelope.parseFrom(buffer);
				MitmRelay.getInstance().call(envelope);

				final List<Request> requests = getRequests(envelope);
				if (requests != null && mListeners.size() > 0) {
					for (MitmListener listener : mListeners) {
						ResponseEnvelope temp = listener.onMitm(requests, envelope);
						if (temp != null) {
							envelope = temp;
						}
					}
					return ByteBuffer.wrap(envelope.toByteArray());
				}
			}
		}
		catch (InvalidProtocolBufferException ignored) {
		}
		catch (Throwable throwable) {
			Log.e(throwable);
		}

		return null;
	}

	private List<Request> getRequests(ResponseEnvelope envelope) {
		final long id = envelope.getRequestId();
		List<Request> requests = mRequests.get(id);
		mRequests.remove(id);
		return requests;
	}
}
