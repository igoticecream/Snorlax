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

import java.util.Objects;

import com.jakewharton.rxrelay.PublishRelay;
import com.jakewharton.rxrelay.SerializedRelay;

import rx.AsyncEmitter;
import rx.Observable;

import static POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope;
import static POGOProtos.Networking.Envelopes.ResponseEnvelopeOuterClass.ResponseEnvelope;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class MitmRelay {

	private static volatile MitmRelay sInstance = null;

	public static MitmRelay getInstance() {
		if (sInstance == null) {
			synchronized (MitmRelay.class) {
				if (sInstance == null) {
					sInstance = new MitmRelay();
				}
			}
		}
		return sInstance;
	}

	private final SerializedRelay<RequestEnvelope, RequestEnvelope> mRelayRequest;
	private final SerializedRelay<ResponseEnvelope, ResponseEnvelope> mRelayResponse;
	private final Observable<MitmEnvelope> mEnvelope;

	private MitmRelay() {
		mRelayRequest = PublishRelay.<RequestEnvelope>create().toSerialized();
		mRelayResponse = PublishRelay.<ResponseEnvelope>create().toSerialized();

		mEnvelope = Observable
			.fromEmitter(emitter -> mRelayRequest.asObservable().subscribe(emitter::onNext), AsyncEmitter.BackpressureMode.BUFFER)
			.cast(RequestEnvelope.class)
			.flatMap(request -> Observable
				.fromEmitter(emitter -> mRelayResponse.asObservable().subscribe(emitter::onNext), AsyncEmitter.BackpressureMode.BUFFER)
				.cast(ResponseEnvelope.class)
				.filter(response -> Objects.equals(request.getRequestId(), response.getRequestId()))
				.map(response -> MitmEnvelope.create(request, response))
			)
			.share();
	}

	void call(RequestEnvelope envelope) {
		mRelayRequest.call(envelope);
	}

	void call(ResponseEnvelope envelope) {
		mRelayResponse.call(envelope);
	}

	public Observable<MitmEnvelope> getObservable() {
		return mEnvelope;
	}
}
