/**
 * Copyright 2015 Emmanuel Keller / QWAZR
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

import com.qwazr.search.analysis.UpdatableAnalyzer;
import com.qwazr.search.function.AbstractValueSource;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.Objects;

public class FunctionQuery extends AbstractQuery {

	final public AbstractValueSource value_source;

	public FunctionQuery() {
		super(null);
		value_source = null;
	}

	FunctionQuery(Float boost, AbstractValueSource value_source) {
		super(boost);
		this.value_source = value_source;
	}

	@Override
	final protected Query getQuery(UpdatableAnalyzer analyzer, String queryString)
			throws IOException, ParseException, QueryNodeException {
		Objects.requireNonNull(value_source, "The value_source property is missing");
		return new org.apache.lucene.queries.function.FunctionQuery(value_source.getValueSource());
	}
}