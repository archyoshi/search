/*
 * Copyright 2015-2020 Emmanuel Keller / QWAZR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qwazr.search.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.qwazr.search.index.IndexServiceInterface;
import com.qwazr.search.index.IndexSettingsDefinition;
import com.qwazr.search.index.QueryDefinition;
import com.qwazr.search.index.ResultDefinition;
import com.qwazr.search.query.DoubleExactQuery;
import com.qwazr.search.query.TermQuery;
import com.qwazr.search.test.units.AbstractIndexTest;
import com.qwazr.utils.ObjectMappers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonNodeTest extends AbstractIndexTest {

    static IndexServiceInterface service;

    private static JsonNode getJson(final String resourceName) throws IOException {
        try (final InputStream is = JsonNodeTest.class.getResourceAsStream(resourceName)) {
            return ObjectMappers.JSON.readTree(is);
        }
    }

    @BeforeClass
    public static void setup() {
        service = initIndexManager(true).getService();
    }

    @Test
    public void indexAndGetIssue() throws IOException {
        final JsonNode issueJson = getJson("issue.json");

        // Create the schema and the index
        service.createUpdateSchema("schema");
        service.createUpdateIndex("schema", "index",
            IndexSettingsDefinition.of().recordField("record").primaryKey("id").build());

        // Index the json doc
        service.postJson("schema", "index", issueJson);

        // Get the document by its id
        final Map<String, Object> doc1 = service.getDocument("schema", "index", "1");
        assertThat(ObjectMappers.JSON.readTree(ObjectMappers.JSON.writeValueAsString(doc1)),
            equalTo(issueJson));

        // Get the document by one root property

        final ResultDefinition.WithMap result2 = service.searchQuery("schema", "index",
            QueryDefinition.of(new DoubleExactQuery("number", 1347d))
                .returnedField("*").queryDebug(true).build(), false);
        assertThat(result2.query, equalTo("pd€number:[1347.0 TO 1347.0]"));
        final Map<String, Object> doc2 = result2.getDocuments().get(0).getFields();
        assertThat(ObjectMappers.JSON.readTree(ObjectMappers.JSON.writeValueAsString(doc2)),
            equalTo(issueJson));

        // Get the document by one deep property
        final ResultDefinition.WithMap result3 = service.searchQuery("schema", "index",
            QueryDefinition.of(new TermQuery("user.login", "octocat"))
                .returnedField("*").queryDebug(true).build(), false);
        final Map<String, Object> doc3 = result3.getDocuments().get(0).getFields();
        assertThat(result3.query, equalTo("st€user.login:octocat"));
        assertThat(ObjectMappers.JSON.readTree(ObjectMappers.JSON.writeValueAsString(doc3)),
            equalTo(issueJson));
    }
}