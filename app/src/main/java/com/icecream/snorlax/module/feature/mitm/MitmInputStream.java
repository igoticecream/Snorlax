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
import java.io.InputStream;
import java.nio.ByteBuffer;

import android.support.annotation.NonNull;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
final class MitmInputStream extends InputStream {

	private static final int sAverageSize = 4096;

	private boolean mMitmDone;
	private ByteBuffer mByteBuffer;

	MitmInputStream(InputStream inputStream) {
		mMitmDone = false;

		if (inputStream == null) {
			return;
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream(sAverageSize);

		byte[] bytes = new byte[sAverageSize];
		int bytesRead;

		try {
			while ((bytesRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
				os.write(bytes, 0, bytesRead);
			}
			os.flush();

			inputStream.close();
		}
		catch (IOException e) {
			os.reset();
		}

		mByteBuffer = ByteBuffer.wrap(os.toByteArray());
	}

	@Override
	public int read() throws IOException {
		mitmStream();

		if (!mByteBuffer.hasRemaining()) {
			return -1;
		}
		return mByteBuffer.get() & 0xFF;
	}

	@Override
	public int read(@NonNull byte[] bytes, int off, int len) throws IOException {
		mitmStream();

		if (!mByteBuffer.hasRemaining()) {
			return -1;
		}

		len = Math.min(len, mByteBuffer.remaining());
		mByteBuffer.get(bytes, off, len);
		return len;
	}

	@Override
	public int available() throws IOException {
		mitmStream();

		if (!mByteBuffer.hasRemaining()) {
			return 0;
		}
		return mByteBuffer.remaining();
	}

	protected void mitmStream() {
		if (mMitmDone)
			return;

		ByteBuffer fromMitm = MitmProvider.processInboundPackage(
			mByteBuffer.asReadOnlyBuffer(),
			mByteBuffer.hasRemaining()
		);

		// Lets keep it read only for now
		/*
		if (fromMitm != null) {
			mByteBuffer = fromMitm;
		}
		*/
		mMitmDone = true;
	}
}
