package com.icecream.snorlax.module.feature.hatch;

import android.support.v4.util.Pair;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.feature.mitm.MitmRelay;
import com.icecream.snorlax.module.util.Log;
import com.icecream.snorlax.module.util.RxFuncitons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import POGOProtos.Data.PokemonDataOuterClass;
import POGOProtos.Inventory.InventoryDeltaOuterClass;
import POGOProtos.Inventory.InventoryItemDataOuterClass;
import POGOProtos.Inventory.InventoryItemOuterClass;
import POGOProtos.Networking.Requests.RequestOuterClass;
import POGOProtos.Networking.Requests.RequestTypeOuterClass;
import POGOProtos.Networking.Responses.GetHatchedEggsResponseOuterClass;
import POGOProtos.Networking.Responses.GetInventoryResponseOuterClass;
import rx.Observable;
import rx.Subscription;

@Singleton
public final class Hatch implements Feature {

    private final MitmRelay mMitmRelay;
    private final HatchPreferences mPreferences;
    private final HatchNotification mHatchNotification;
    private Subscription mSubscription;

    private final Map<Long, EggHatchRewards> hatchedEggs = new HashMap<>();

    public Hatch(MitmRelay mitmRelay, HatchPreferences hatchPreferences, HatchNotification hatchNotification) {
        this.mMitmRelay = mitmRelay;
        this.mPreferences = hatchPreferences;
        this.mHatchNotification = hatchNotification;
    }

    private void onHatch(ByteString bytes) {
        try {
            GetHatchedEggsResponseOuterClass.GetHatchedEggsResponse getHatchedEggsResponse = GetHatchedEggsResponseOuterClass.GetHatchedEggsResponse.parseFrom(bytes);

            if (!getHatchedEggsResponse.getSuccess())
                return;

            for (int i = 0, l = getHatchedEggsResponse.getPokemonIdCount(); i < l; i++) {
                EggHatchRewards eggHatchRewards = new EggHatchRewards(getHatchedEggsResponse.getExperienceAwarded(i),
                        getHatchedEggsResponse.getCandyAwarded(i),
                        getHatchedEggsResponse.getStardustAwarded(i));
                hatchedEggs.put(getHatchedEggsResponse.getPokemonId(i), eggHatchRewards);
            }

        } catch (InvalidProtocolBufferException e) {
            Log.d("GetHatchedEggsResponse failed: %s" + e.getMessage());
            Log.e(e);
        }
    }


    private void onInventoryUpdate(ByteString bytes) {
        try {
            GetInventoryResponseOuterClass.GetInventoryResponse getInventoryResponse = GetInventoryResponseOuterClass.GetInventoryResponse.parseFrom(bytes);

            if (!getInventoryResponse.getSuccess() || !getInventoryResponse.hasInventoryDelta())
                return;

            // First time (delta.getOriginalTimestampMs() == 0L) the server sends the full inventory,
            // after that only what has changed since the last request
            InventoryDeltaOuterClass.InventoryDelta inventoryDelta = getInventoryResponse.getInventoryDelta();

            List<EggHatchRewards> hatchedEggsAwardedList = new ArrayList();
            List<PokemonDataOuterClass.PokemonData> hatchedEggsAwardedListPokemonData = new ArrayList();

            for (InventoryItemOuterClass.InventoryItem inventoryItem : inventoryDelta.getInventoryItemsList()) {
                if (inventoryItem.hasInventoryItemData()) {
                    InventoryItemDataOuterClass.InventoryItemData inventoryItemData = inventoryItem.getInventoryItemData();

                    if (inventoryItemData.hasPokemonData()) {
                        PokemonDataOuterClass.PokemonData pokemonData = inventoryItemData.getPokemonData();
                        long pokemonId = pokemonData.getId();

                        if (hatchedEggs.containsKey(pokemonId)) {
                            hatchedEggsAwardedList.add(hatchedEggs.get(pokemonId));
                            hatchedEggs.remove(pokemonId);

                            hatchedEggsAwardedListPokemonData.add(pokemonData);

                        }
                    }
                }
            }


        } catch (InvalidProtocolBufferException e) {
            Log.d("GetInventoryResponse failed: %s" + e.getMessage());
            Log.e(e);
        }
    }

    @Override
    public void subscribe() {
        unsubscribe();

        mSubscription = mMitmRelay
                .getObservable()
                .compose(mPreferences.isEnabled())
                .flatMap(envelope -> {
                    List<RequestOuterClass.Request> requests = envelope.getRequest().getRequestsList();

                    for (int i = 0; i < requests.size(); i++) {
                        RequestTypeOuterClass.RequestType type = requests.get(i).getRequestType();

                        switch (type) {
                            case GET_HATCHED_EGGS:
                            case GET_INVENTORY:
                                return Observable.just(new Pair<>(type, envelope.getResponse().getReturns(i)));
                            default:
                                break;
                        }

                    }
                    return Observable.empty();
                })
                .subscribe(pair -> {
                            switch (pair.first) {
                                case GET_HATCHED_EGGS:
                                    onHatch(pair.second);
                                    break;
                                case GET_INVENTORY:
                                    onInventoryUpdate(pair.second);
                                    break;
                                default:
                                    break;
                            }
                        }
                        , Log::e);
    }

    @Override
    public void unsubscribe() {
        RxFuncitons.unsubscribe(mSubscription);
    }
}
