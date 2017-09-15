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
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanPositionRangeQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import java.io.IOException;

public class SpanPositionsQuery extends AbstractFieldQuery {

	final public Integer distance;
	final public String query_string;

	@JsonCreator
	public SpanPositionsQuery(@JsonProperty("generic_field") final String genericField,
			@JsonProperty("field") final String field, @JsonProperty("distance") final Integer distance,
			@JsonProperty("query_string") final String queryString) {
		super(genericField, field);
		this.distance = distance;
		this.query_string = queryString;
	}

	public SpanPositionsQuery(final String field, final Integer distance, final String queryString) {
		this(null, field, distance, queryString);
	}

	@Override
	final public Query getQuery(final QueryContext queryContext) throws IOException {

		final BooleanQuery.Builder builder = new BooleanQuery.Builder();
		final String resolvedField = resolveField(queryContext.getFieldMap());
		try (final TokenStream tokenStream = queryContext.getQueryAnalyzer().tokenStream(resolvedField, query_string)) {
			final CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			final PositionIncrementAttribute pocincrAttribute =
					tokenStream.getAttribute(PositionIncrementAttribute.class);
			tokenStream.reset();
			int pos = 0;
			while (tokenStream.incrementToken()) {
				final String charTerm = charTermAttribute.toString();
				int start = pos - distance;
				if (start < 0)
					start = 0;
				final int end = pos + distance + 1;
				for (int i = start; i < end; i++) {
					final float dist = Math.abs(i - pos) + 1;
					final float boost = 1 / dist;
					final SpanTermQuery spanTermQuery = new SpanTermQuery(new Term(resolvedField, charTerm));
					Query query = new BoostQuery(new SpanPositionRangeQuery(spanTermQuery, i, i + 1), boost);
					builder.add(new BooleanClause(query, BooleanClause.Occur.SHOULD));
				}
				pos += pocincrAttribute.getPositionIncrement();
			}
			return builder.build();
		}
	}

}
