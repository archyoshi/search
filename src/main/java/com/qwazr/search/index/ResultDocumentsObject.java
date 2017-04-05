/**
 * Copyright 2017 Emmanuel Keller / QWAZR
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
 **/
package com.qwazr.search.index;

import com.qwazr.utils.FieldMapWrapper;
import org.apache.lucene.search.ScoreDoc;

import java.util.List;

final class ResultDocumentsObject<T> extends ResultDocumentsAbstract<ResultDocumentObject<T>> {

	private final FieldMapWrapper<T> wrapper;

	ResultDocumentsObject(final QueryContext context, final QueryDefinition queryDefinition,
			final FieldMapWrapper<T> wrapper) {
		super((QueryContextImpl) context, queryDefinition, wrapper.fieldMap.keySet());
		this.wrapper = wrapper;
	}

	@Override
	ResultDocumentBuilder<ResultDocumentObject<T>> newResultDocumentBuilder(int absolutePos, ScoreDoc scoreDoc) {
		return new ResultDocumentObject.Builder<>(absolutePos, scoreDoc, wrapper);
	}

	@Override
	ResultDefinition<ResultDocumentObject<T>> newResultDefinition(ResultDocumentsBuilder resultDocumentsBuilder,
			List<ResultDocumentObject<T>> documents) {
		return new ResultDefinition.WithObject<>(resultDocumentsBuilder, documents);
	}

}
