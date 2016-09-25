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

import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.module.util.Log;

import static POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope;
import static POGOProtos.Networking.Envelopes.ResponseEnvelopeOuterClass.ResponseEnvelope;

final class MitmProvider {

	static ByteBuffer processOutboundPackage(ByteBuffer roData, boolean connectionOk) {
		if (!connectionOk)
			return null;

		roData.rewind();

		try {
			byte[] buffer = new byte[roData.remaining()];
			roData.get(buffer);

			synchronized (MitmProvider.class) {
				RequestEnvelope envelope = RequestEnvelope.parseFrom(buffer);
				MitmRelay.getInstance().call(envelope);
			}
		}
		catch (InvalidProtocolBufferException ignored) {
		}
		catch (Throwable throwable) {
			Log.e(throwable);
		}

		return null;
	}

	static ByteBuffer processInboundPackage(ByteBuffer roData, boolean connectionOk) {
		if (!connectionOk)
			return null;

		roData.rewind();

		try {
			byte[] buffer = new byte[roData.remaining()];
			roData.get(buffer);

			synchronized (MitmProvider.class) {
				ResponseEnvelope envelope = ResponseEnvelope.parseFrom(buffer);
				MitmRelay.getInstance().call(envelope);
			}
		}
		catch (InvalidProtocolBufferException ignored) {
		}
		catch (Throwable throwable) {
			Log.e(throwable);
		}

		return null;
	}

	private MitmProvider() {
		throw new AssertionError("No instances");
	}
}
