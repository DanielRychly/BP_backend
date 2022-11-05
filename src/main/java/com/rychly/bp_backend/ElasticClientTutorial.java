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

import java.util.LinkedHashMap;
import java.util.List;

public class ElasticClientTutorial {


    public static ElasticsearchClient initializeElasticsearchClient(){

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

        return client;

    }


    public static List<Hit<Object>> getLogsFromIndex(ElasticsearchClient client){

        try{
            SearchResponse<Object> response = client.search(s -> s
                            .index("logs")
                            .query(q -> q
                                    .match(t -> t
                                            .field("case_id")
                                            .query("sample-2-case-1")
                                    )
                            ),
                    Object.class
            );

            TotalHits total = response.hits().total();
            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;
            if (isExactResult) {
                System.out.println("There are " + total.value() + " results");
            } else {
                System.out.println("There are more than " + total.value() + " results");
            }

            List<Hit<Object>> hits = response.hits().hits();

            for (Hit<Object> hit: hits) {

                //Object o = hit.source();
                LinkedHashMap<String,String> log = (LinkedHashMap<String,String>) hit.source(); //this is how you get the log data

                System.out.println(log.get("fired_transition_id"));
                System.out.println(log.get("case_id"));
                // System.out.println("Found log, transition - " + o.getFired_transition_id() + ", score " + hit.score());
            }

            System.out.println(response.hits().hits());
            return hits;

        }
        catch(Exception e)
        {
            e.printStackTrace();



        }
        return null;




    }
    public static void main(String args[]){


        ElasticsearchClient client = initializeElasticsearchClient();

        List<Hit<Object>> hits = getLogsFromIndex(client);


        for (Hit<Object> hit: hits) {

            //Object o = hit.source();
            LinkedHashMap<String,String> log = (LinkedHashMap<String,String>) hit.source();

            System.out.println(log.get("fired_transition_id"));
            System.out.println(log.get("case_id"));
            // System.out.println("Found log, transition - " + o.getFired_transition_id() + ", score " + hit.score());
        }



    }
}
