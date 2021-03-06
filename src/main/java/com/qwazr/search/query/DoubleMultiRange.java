/*
 * Copyright 2015-2020 Emmanuel Keller / QWAZR
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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qwazr.search.analysis.AnalyzerDefinition;
import com.qwazr.search.annotations.QuerySampleCreator;
import com.qwazr.search.field.FieldDefinition;
import com.qwazr.search.field.FieldTypeInterface;
import com.qwazr.search.index.IndexSettingsDefinition;
import com.qwazr.search.index.QueryContext;
import com.qwazr.utils.ArrayUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.search.Query;

public class DoubleMultiRange extends AbstractMultiRangeQuery<DoubleMultiRange> {

    @JsonProperty("lower_values")
    final public double[] lowerValues;
    @JsonProperty("upper_values")
    final public double[] upperValues;

    @JsonCreator
    public DoubleMultiRange(@JsonProperty("generic_field") final String genericField,
                            @JsonProperty("field") final String field,
                            @JsonProperty("lower_values") final double[] lowerValues,
                            @JsonProperty("upper_values") final double[] upperValues) {
        super(DoubleMultiRange.class, genericField, field);
        this.lowerValues = lowerValues;
        this.upperValues = upperValues;
    }

    public DoubleMultiRange(final String field, final double[] lowerValues, final double[] upperValues) {
        this(null, field, lowerValues, upperValues);
    }

    public DoubleMultiRange(final String field, final double lowerValue, final double upperValue) {
        this(field, new double[]{lowerValue}, new double[]{upperValue});
    }

    @QuerySampleCreator(docUri = CORE_BASE_DOC_URI + "core/org/apache/lucene/document/DoublePoint.html#newRangeQuery-java.lang.String-double:A-double:A-")
    public DoubleMultiRange(final IndexSettingsDefinition settings,
                            final Map<String, AnalyzerDefinition> analyzers,
                            final Map<String, FieldDefinition> fields) {
        this(getDoubleField(fields, () -> "double_field"), new double[]{1.57d, 6.28d}, new double[]{3.14d, 7.85d});
    }

    @Override
    @JsonIgnore
    protected boolean isEqual(final DoubleMultiRange q) {
        return super.isEqual(q) && Arrays.equals(lowerValues, q.lowerValues) &&
            Arrays.equals(upperValues, q.upperValues);
    }

    @Override
    public Query getQuery(final QueryContext queryContext) {
        return DoublePoint.newRangeQuery(
            resolvePointField(queryContext.getFieldMap(), 0D, FieldTypeInterface.ValueType.doubleType),
            lowerValues, upperValues
        );
    }

    public static class Builder extends AbstractBuilder<Double, Builder> {

        public Builder(String genericField, String field) {
            super(genericField, field);
        }

        @Override
        protected Builder me() {
            return this;
        }

        @Override
        protected DoubleMultiRange build(final String field, final Collection<Double> lowerValues,
                                         final Collection<Double> upperValues) {
            return new DoubleMultiRange(field, ArrayUtils.toPrimitiveDouble(lowerValues),
                ArrayUtils.toPrimitiveDouble(upperValues));
        }
    }

}
