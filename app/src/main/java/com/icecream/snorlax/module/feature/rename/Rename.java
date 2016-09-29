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

package com.icecream.snorlax.module.feature.rename;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.common.Strings;
import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.feature.mitm.MitmListener;
import com.icecream.snorlax.module.feature.mitm.MitmProvider;

import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import static POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import static POGOProtos.Inventory.InventoryDeltaOuterClass.InventoryDelta;
import static POGOProtos.Inventory.InventoryItemDataOuterClass.InventoryItemData;
import static POGOProtos.Inventory.InventoryItemOuterClass.InventoryItem;
import static POGOProtos.Networking.Envelopes.ResponseEnvelopeOuterClass.ResponseEnvelope;
import static POGOProtos.Networking.Requests.RequestOuterClass.Request;
import static POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import static POGOProtos.Networking.Responses.GetInventoryResponseOuterClass.GetInventoryResponse;

@Singleton
public final class Rename implements Feature, MitmListener {

	private final MitmProvider mMitmProvider;
	private final RenamePreferences mRenamePreferences;

	@Inject
	Rename(MitmProvider mitmProvider, RenamePreferences renamePreferences) {
		mMitmProvider = mitmProvider;
		mRenamePreferences = renamePreferences;
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
	public ResponseEnvelope onMitm(List<Request> requests, ResponseEnvelope envelope) {
		if (!mRenamePreferences.isEnabled()) {
			return null;
		}

		for (int i = 0; i < requests.size(); i++) {
			if (requests.get(i).getRequestType() == RequestType.GET_INVENTORY) {
				try {
					ByteString processed = processInventory(GetInventoryResponse.parseFrom(envelope.getReturns(i)));

					if (processed != null) {
						envelope = envelope.toBuilder().setReturns(i, processed).build();
					}
				}
				catch (InvalidProtocolBufferException ignored) {
				}
			}
		}
		return envelope;
	}

	private ByteString processInventory(GetInventoryResponse response) {
		if (!response.getSuccess() || !response.hasInventoryDelta()) {
			return null;
		}

		GetInventoryResponse.Builder inventory = response.toBuilder();
		InventoryDelta.Builder delta = inventory.getInventoryDelta().toBuilder();

		for (int i = 0; i < delta.getInventoryItemsCount(); i++) {

			InventoryItem.Builder item = delta.getInventoryItems(i).toBuilder();
			InventoryItemData.Builder data = item.getInventoryItemData().toBuilder();

			if (data.getPokemonData().getPokemonId() != PokemonId.MISSINGNO) {
				PokemonData.Builder pokemon = data.getPokemonData().toBuilder();

				if (Strings.isEmpty(pokemon.getNickname())) {
					pokemon.setNickname(String.format(
						"+ %s/%s/%s",
						String.valueOf(pokemon.getIndividualAttack()),
						String.valueOf(pokemon.getIndividualDefense()),
						String.valueOf(pokemon.getIndividualStamina())
					));
				}
				data.setPokemonData(pokemon);
			}

			item.setInventoryItemData(data);
			delta.setInventoryItems(i, item);
		}
		return inventory.setInventoryDelta(delta).build().toByteString();
	}
}
