/*
 * Copyright 2015-2017 Emmanuel Keller / QWAZR
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qwazr.search.function;

import java.util.Objects;

abstract class AbstractFieldSource<T extends AbstractFieldSource> extends AbstractValueSource<T> {

	public final String field;

	protected AbstractFieldSource(Class<T> ownClass, String field) {
		super(ownClass);
		this.field = field;
	}

	@Override
	protected boolean isEqual(T q) {
		return Objects.equals(field, q.field);
	}

	final protected void check() {
		Objects.requireNonNull(field, "The field property is missing");
	}
}
