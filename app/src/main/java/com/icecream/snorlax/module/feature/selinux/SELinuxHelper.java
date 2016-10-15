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

package com.icecream.snorlax.module.feature.selinux;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Locale;

import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.icecream.snorlax.common.Strings;

import eu.chainfire.libsuperuser.Shell;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
final class SELinuxHelper {

	static final String ENFORCING = "Enforcing";
	static final String PERMISSIVE = "Permissive";

	static Boolean isPermissive() {
		final String getenforce = getEnforce();
		if (Strings.isNull(getenforce)) {
			return null;
		}
		else {
			return getenforce.equalsIgnoreCase(PERMISSIVE);
		}
	}

	@Nullable
	@SELinuxMode
	static String getEnforce() {
		List<String> outputs = Shell.SH.run(new String[]{"getenforce"});

		if (outputs != null && outputs.size() > 0) {
			final String output = outputs.get(0);
			if (output.equalsIgnoreCase(ENFORCING)) {
				return ENFORCING;
			}
			else if (output.equalsIgnoreCase(PERMISSIVE)) {
				return PERMISSIVE;
			}
		}
		return null;
	}

	static void setEnforce(@SELinuxMode String mode) {
		Shell.SU.run(new String[]{
			String.format(Locale.US, "setenforce %d", mode.equalsIgnoreCase(PERMISSIVE) ? 0 : 1)
		});
	}

	private SELinuxHelper() {
		throw new AssertionError("No instances");
	}

	@StringDef({ENFORCING, PERMISSIVE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface SELinuxMode {

	}
}
