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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.qwazr.search.analysis.UpdatableAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.Query;

import java.io.IOException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "query")
@JsonSubTypes({ @JsonSubTypes.Type(value = BooleanQuery.class, name = "boolean_query"),
				@JsonSubTypes.Type(value = ConstantScoreQuery.class, name = "constant_score_query"),
				@JsonSubTypes.Type(value = DisjunctionMaxQuery.class, name = "disjunction_max_query"),
				@JsonSubTypes.Type(value = FuzzyQuery.class, name = "fuzzy_query"),
				@JsonSubTypes.Type(value = MatchAllDocsQuery.class, name = "match_all_docs_query"),
				@JsonSubTypes.Type(value = MatchNoDocsQuery.class, name = "match_no_docs_query"),
				@JsonSubTypes.Type(value = MultiFieldQueryParser.class, name = "multi_field_query_parser"),
				@JsonSubTypes.Type(value = PhraseQuery.class, name = "phrase_query"),
				@JsonSubTypes.Type(value = PrefixQuery.class, name = "prefix_query"),
				@JsonSubTypes.Type(value = RegexpQuery.class, name = "regexp_query"),
				@JsonSubTypes.Type(value = SpanFirstQueries.class, name = "span_first_queries"),
				@JsonSubTypes.Type(value = SpanFirstQuery.class, name = "span_first_query"),
				@JsonSubTypes.Type(value = SpanNearQuery.class, name = "span_near_query"),
				@JsonSubTypes.Type(value = SpanNotQuery.class, name = "span_not_query"),
				@JsonSubTypes.Type(value = SpanTermQuery.class, name = "span_term_query"),
				@JsonSubTypes.Type(value = StandardQueryParser.class, name = "standard_query_parser"),
				@JsonSubTypes.Type(value = TermQuery.class, name = "term_query"),
				@JsonSubTypes.Type(value = TermRangeQuery.class, name = "term_range_query") })

public abstract class AbstractQuery {

	public final Float boost;

	protected AbstractQuery(Float boost) {
		this.boost = boost;
	}

	@JsonIgnore
	protected abstract Query getQuery(UpdatableAnalyzer analyzer, String queryString)
					throws IOException, ParseException, QueryNodeException;

	public final Query getBoostedQuery(UpdatableAnalyzer analyzer, String queryString)
					throws IOException, ParseException, QueryNodeException {
		Query query = getQuery(analyzer, queryString);
		if (boost != null)
			query.setBoost(boost);
		return query;
	}
}