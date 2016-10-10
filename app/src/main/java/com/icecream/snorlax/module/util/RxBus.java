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

package com.icecream.snorlax.module.util;

import com.jakewharton.rxrelay.PublishRelay;
import com.jakewharton.rxrelay.SerializedRelay;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public final class RxBus {

	private static volatile RxBus sInstance = null;

	public static RxBus getInstance() {
		if (sInstance == null) {
			synchronized (RxBus.class) {
				if (sInstance == null) {
					sInstance = new RxBus();
				}
			}
		}
		return sInstance;
	}

	private final SerializedRelay<Object, Object> mSubject;

	private RxBus() {
		mSubject = PublishRelay.create().toSerialized();
	}

	public void post(Object event) {
		mSubject.call(event);
	}

	public <T> Subscription receive(final Class<T> klass, Action1<T> onNext) {
		return receive(klass).subscribe(onNext);
	}

	public <T> Observable<T> receive(final Class<T> klass) {
		return receive().ofType(klass);
	}

	public Observable<Object> receive() {
		return mSubject;
	}
}
