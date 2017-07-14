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
package com.qwazr.search.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qwazr.search.index.QueryContext;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.Query;

import java.io.IOException;

public class IntRangeQuery extends AbstractRangeQuery<Integer> {

	@JsonCreator
	public IntRangeQuery(@JsonProperty("field") final String field,
			@JsonProperty("lower_value") final Integer lowerValue,
			@JsonProperty("upper_value") final Integer upperValue) {
		super(field, lowerValue == null ? Integer.MIN_VALUE : lowerValue,
				upperValue == null ? Integer.MAX_VALUE : upperValue);
	}

	@Override
	public Query getQuery(final QueryContext queryContext) throws IOException {
		return IntPoint.newRangeQuery(resolveField(queryContext.getFieldMap()), lower_value, upper_value);
	}
}