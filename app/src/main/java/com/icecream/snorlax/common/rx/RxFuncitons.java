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

package com.icecream.snorlax.common.rx;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class RxFuncitons {

	public static void unsubscribe(Subscription subscription) {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}

	public static void unsubscribe(CompositeSubscription subscription) {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}

	private RxFuncitons() {
		throw new AssertionError("No instances");
	}
}
