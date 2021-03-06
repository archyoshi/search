# How to build a search request

Here is the most complex search request.

It includes:

* Full text query
* Filtered query
* Boolean query
* Facet query and facet count
* Snippet and highlighting
* Sorting
* Collector Functions
* Paging
* Returned fields

```json
{
  "query": {
    "Bool": {
      "clauses": [
        {
          "occur": "must",
          "query": {
            "MultiFieldQueryParser": {
              "fields": [
                "name",
                "description"
              ],
              "query_string": "Article",
              "boosts": {
                "name": 10,
                "description": 1
              }
            }
          }
        },
        {
          "occur": "filter",
          "query": {
            "Bool": {
              "clauses": [
                {
                  "occur": "should",
                  "query": {
                    "FacetPath": {
                      "dimension": "category",
                      "path": [
                        "science"
                      ]
                    }
                  }
                }
              ]
            }
          }
        }
      ]
    }
  },
  "returned_fields": [
    "name"
  ],
  "start": 0,
  "rows": 10,
  "facets": {
    "category": {},
    "FacetQueries": {
      "queries": {
        "AllDocs": {
          "MatchAllDocs": {}
        },
        "2016,January": {
          "TermRange": {
            "field": "single_date",
            "lower_term": "201601",
            "upper_term": "201602",
            "include_lower": true,
            "include_upper": false
          }
        }
      }
    }
  },
  "sorts": {
    "$score": "descending"
  },
  "highlighters": {
    "my_custom_snippet": {
      "field": "description",
      "pre_tag": "<strong>",
      "post_tag": "</strong>",
      "escape": false,
      "multivalued_separator": " ",
      "ellipsis": "… ",
      "max_passages": 5,
      "max_length": 5000,
      "break_iterator": {
        "type": "sentence",
        "language": "en-US"
      }
    }
  }
}

```

If you already build an index while following the examples on this documentation,
you can test this request using this curl command:

(Where the payload file (my_payload) contains the search request).

```bash
curl -XPOST -H 'Content-Type: application/json' -d @my_payload \
    "http://localhost:9091/indexes/my_index/search"
```
