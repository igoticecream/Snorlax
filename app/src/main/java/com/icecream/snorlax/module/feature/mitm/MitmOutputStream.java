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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class MitmOutputStream extends ByteArrayOutputStream {

	private final OutputStream mOutputStream;

	MitmOutputStream(OutputStream outputStream) {
		super(2048);
		mOutputStream = outputStream;
	}

	@Override
	public void close() throws IOException {
		mitmStream();
		mitmDone();

		if (mOutputStream != null) {
			mOutputStream.close();
		}
		super.close();
	}

	@SuppressWarnings("unused")
	private void mitmStream() {
		ByteBuffer fromMitm = MitmProvider.processOutboundPackage(
			ByteBuffer.wrap(buf, 0, count).asReadOnlyBuffer(),
			mOutputStream != null
		);

		// Lets keep it read only for now
		/*
		if (fromMitm != null) {
			reset();
			fromMitm.rewind();
			write(fromMitm.array(), fromMitm.arrayOffset(), fromMitm.remaining());
		}
		*/
	}

	private void mitmDone() throws IOException {
		if (mOutputStream != null) {
			writeTo(mOutputStream);
		}
		reset();
	}

	@Override
	public void flush() throws IOException {
		mitmStream();
		mitmDone();

		if (mOutputStream != null) {
			mOutputStream.flush();
		}
		super.flush();
	}
}
