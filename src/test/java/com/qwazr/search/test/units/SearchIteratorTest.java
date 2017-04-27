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
 */
package com.qwazr.search.test.units;

import com.qwazr.search.field.FieldDefinition;
import com.qwazr.search.index.QueryDefinition;
import com.qwazr.search.query.IntExactQuery;
import com.qwazr.search.query.MatchAllDocsQuery;
import com.qwazr.search.query.TermQuery;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class SearchIteratorTest extends AbstractIndexTest {

	private static List<IndexRecord> documents;
	private static List<IndexRecord> subset;

	@BeforeClass
	public static void setup() throws IOException, InterruptedException, URISyntaxException {
		initIndexService();
		documents = new ArrayList<>();
		subset = new ArrayList<>();
		for (int i = 0; i < RandomUtils.nextInt(300, 500); i++)
			documents.add(new IndexRecord(Integer.toString(i)).intPoint(RandomUtils.nextInt(0, 2)));
		indexService.postDocuments(documents);
	}

	void checkIterate(Iterator<IndexRecord> iterator, Set<String> ids) {
		int initialSize = ids.size();
		int count = 0;
		while (iterator.hasNext()) {
			final IndexRecord record = iterator.next();
			Assert.assertNotNull(record);
			Assert.assertTrue(ids.remove(record.id));
			count++;
		}

		Assert.assertEquals(initialSize, count);
		Assert.assertEquals(0, ids.size());

		try {
			iterator.next();
			Assert.fail("NoSuchElementException not thrown");
		} catch (NoSuchElementException e) {
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void iterateAll() throws ReflectiveOperationException {
		final Iterator<IndexRecord> iterator =
				indexService.searchIterator(QueryDefinition.of(new MatchAllDocsQuery()).build(), IndexRecord.class);
		Assert.assertNotNull(iterator);

		final Set<String> ids = new HashSet<>();
		documents.forEach(r -> ids.add(r.id));
		Assert.assertEquals(ids.size(), documents.size());

		checkIterate(iterator, ids);
	}

	@Test
	public void iterateSubSet() throws ReflectiveOperationException {

		final Iterator<IndexRecord> iterator =
				indexService.searchIterator(QueryDefinition.of(new IntExactQuery("intPoint", 0)).build(),
						IndexRecord.class);
		Assert.assertNotNull(iterator);

		final Set<String> ids = new HashSet<>();
		documents.stream().filter(doc -> doc.intPoint == 0).forEach(r -> ids.add(r.id));
		Assert.assertNotEquals(0, ids.size());
		Assert.assertNotEquals(documents.size(), ids.size());

		checkIterate(iterator, ids);
	}

	@Test
	public void iterateNone() throws ReflectiveOperationException {

		final Iterator<IndexRecord> iterator =
				indexService.searchIterator(QueryDefinition.of(new TermQuery(FieldDefinition.ID_FIELD, 0)).build(),
						IndexRecord.class);
		Assert.assertNotNull(iterator);
		Assert.assertFalse(iterator.hasNext());

		try {
			iterator.next();
			Assert.fail("NoSuchElementException not thrown");
		} catch (NoSuchElementException e) {
			Assert.assertNotNull(e);
		}
	}
}
