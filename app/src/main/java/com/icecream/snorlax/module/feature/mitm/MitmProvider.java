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
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.util.LongSparseArray;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.common.Strings;
import com.icecream.snorlax.module.util.Log;

import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import static POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import static POGOProtos.Inventory.InventoryDeltaOuterClass.InventoryDelta;
import static POGOProtos.Inventory.InventoryItemDataOuterClass.InventoryItemData;
import static POGOProtos.Inventory.InventoryItemOuterClass.InventoryItem;
import static POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope;
import static POGOProtos.Networking.Envelopes.ResponseEnvelopeOuterClass.ResponseEnvelope;
import static POGOProtos.Networking.Requests.RequestOuterClass.Request;
import static POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import static POGOProtos.Networking.Responses.GetInventoryResponseOuterClass.GetInventoryResponse;
import static android.R.attr.id;

@Singleton
final class MitmProvider {

	private final LongSparseArray<List<Request>> mRequests;

	@Inject
	MitmProvider(LongSparseArray<List<Request>> requests) {
		mRequests = requests;
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

				processOutBuffer(envelope);
			}
		}
		catch (InvalidProtocolBufferException ignored) {
		}
		catch (Throwable throwable) {
			Log.e(throwable);
		}

		return null;
	}

	private void processOutBuffer(RequestEnvelope envelope) {
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

				ByteBuffer processed = processInBuffer(envelope);

				if (processed != null) {
					return processed;
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

	private ByteBuffer processInBuffer(ResponseEnvelope envelope) throws InvalidProtocolBufferException {
		List<Request> requests = mRequests.get(envelope.getRequestId());

		if (requests == null) {
			return null;
		}

		boolean isDone = false;
		for (int i = 0; i < requests.size(); i++) {
			if (requests.get(i).getRequestType() == RequestType.GET_INVENTORY) {
				ByteString processed = processInventoryResponse(GetInventoryResponse.parseFrom(envelope.getReturns(i)));

				if (processed != null) {
					ResponseEnvelope.Builder builder = envelope.toBuilder();
					builder.setReturns(i, processed);
					envelope = builder.build();
					isDone = true;
				}
			}
		}
		mRequests.remove(id);

		if (!isDone) {
			return null;
		}

		return ByteBuffer.wrap(envelope.toByteArray());
	}

	private ByteString processInventoryResponse(GetInventoryResponse response) {
		if (!response.getSuccess() || !response.hasInventoryDelta()) {
			return null;
		}

		boolean isDone = false;
		GetInventoryResponse.Builder inventory = response.toBuilder();
		InventoryDelta.Builder inventoryDelta = inventory.getInventoryDelta().toBuilder();
		for (int i = 0; i < inventoryDelta.getInventoryItemsCount(); i++) {
			InventoryItem.Builder inventoryItem = inventoryDelta.getInventoryItems(i).toBuilder();
			InventoryItemData.Builder itemData = inventoryItem.getInventoryItemData().toBuilder();

			if (itemData.getPokemonData().getPokemonId() != PokemonId.MISSINGNO) {
				PokemonData.Builder Pokemon = itemData.getPokemonData().toBuilder();
				String nickname = Pokemon.getNickname();

				if (Strings.isEmpty(nickname)) {
					Pokemon.setNickname(
						String.format(
							"%s/%s/%s",
							String.valueOf(Pokemon.getIndividualAttack()),
							String.valueOf(Pokemon.getIndividualDefense()),
							String.valueOf(Pokemon.getIndividualStamina())
						)
					);
					itemData.setPokemonData(Pokemon);
					inventoryItem.setInventoryItemData(itemData);
					inventoryDelta.setInventoryItems(i, inventoryItem);
					inventory.setInventoryDelta(inventoryDelta);
					isDone = true;
				}
			}
		}
		if (isDone) {
			return inventory.build().toByteString();
		}

		return null;
	}
}
