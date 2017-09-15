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
import com.qwazr.search.index.BytesRefUtils;
import com.qwazr.search.index.FieldMap;
import com.qwazr.search.index.QueryContext;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.Collection;

public class BlendedTermQuery extends AbstractQuery {

	public class Term {

		@JsonProperty("generic_field")
		public final String genericField;
		public final String field;
		public final Object value;
		public final Float boost;

		@JsonCreator
		public Term(@JsonProperty("generic_field") final String genericField, @JsonProperty("field") final String field,
				@JsonProperty("value") final Object value, @JsonProperty("boost") final Float boost) {
			this.genericField = genericField;
			this.field = field;
			this.value = value;
			this.boost = boost;
		}

		private void add(FieldMap fieldMap, org.apache.lucene.search.BlendedTermQuery.Builder builder) {
			final org.apache.lucene.index.Term term =
					BytesRefUtils.toTerm(fieldMap == null ? field : fieldMap.resolveQueryFieldName(genericField, field),
							value);
			if (boost == null)
				builder.add(term);
			else
				builder.add(term, boost);
		}
	}

	final public Collection<Term> terms;

	@JsonCreator
	public BlendedTermQuery(@JsonProperty("terms") final Collection<Term> terms) {
		this.terms = terms;
	}

	public BlendedTermQuery term(final String genericField, final String field, final String value, final Float boost) {
		terms.add(new Term(genericField, field, value, boost));
		return this;
	}

	public BlendedTermQuery term(final String field, final String value, final Float boost) {
		return term(null, field, value, boost);
	}

	public BlendedTermQuery term(final String genericField, final String field, final String value) {
		return term(genericField, field, value, null);
	}

	public BlendedTermQuery term(final String field, final String value) {
		return term(null, field, value, null);
	}

	@Override
	final public Query getQuery(final QueryContext queryContext) throws IOException {
		org.apache.lucene.search.BlendedTermQuery.Builder builder =
				new org.apache.lucene.search.BlendedTermQuery.Builder();
		if (terms != null)
			terms.forEach(term -> term.add(queryContext.getFieldMap(), builder));
		return builder.build();
	}

}
