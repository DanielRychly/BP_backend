package com.rychly.bp_backend;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.rychly.bp_backend.comparators.Log;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.util.List;

public class ElasticClientTutorial {

    public static void main(String args[]){

        // Create the low-level client
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        System.out.println("rest client created");
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        System.out.println("transport created");
        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);
        System.out.println("elastic client created");



        try{
            SearchResponse<Log> response = client.search(s -> s
                            .index("logs")
                            .query(q -> q
                                    .match(t -> t
                                            .field("case_id")
                                            .query("ordes-case-1")
                                    )
                            ),
                    Log.class
            );

            TotalHits total = response.hits().total();
            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;
            if (isExactResult) {
                System.out.println("There are " + total.value() + " results");
            } else {
                System.out.println("There are more than " + total.value() + " results");
            }

            List<Hit<Log>> hits = response.hits().hits();
            for (Hit<Log> hit: hits) {
                Log log = hit.source();
                System.out.println("Found log, transition - " + log.getFired_transition_id() + ", score " + hit.score());
            }

            System.out.println(response.hits().hits());

        }
        catch(Exception e)
        {
            e.printStackTrace();
            //System.out.println("Exception");
        }

    }
}
