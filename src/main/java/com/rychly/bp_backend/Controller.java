package com.rychly.bp_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rychly.bp_backend.model.PetriNet;
import okhttp3.*;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.sql.Blob;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class Controller {

    @Autowired
    private IMyService service;

    private final OkHttpClient client = new OkHttpClient();

    private boolean indexExists(String indexName) throws Exception{

        Request request = new Request.Builder().url("http://localhost:9200/"+indexName+"/").head().build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            System.out.println("Index exists");
        }else{
            System.out.println("Index does not exist");
        }
        return response.isSuccessful();
    }

    private boolean deleteIndex(String indexName) throws Exception{

        Request request = new Request.Builder().url("http://localhost:9200/"+indexName+"/").delete().build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            System.out.println("Index deleted");
        }else{
            System.out.println("Index not deleted");
        }
        return response.isSuccessful();
    }

    private boolean createIndex(String indexName) throws Exception{
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder().url("http://localhost:9200/"+indexName+"/").put(requestBody).build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            System.out.println("Index created");
        }else{
            System.out.println("Index not created");
        }
        return response.isSuccessful();
    }

    private boolean saveMultipartFile(MultipartFile multipartFile) throws IOException{
        if (multipartFile.isEmpty()) {
            return false;
        }

        File tmp = new File("src/main/resources/uploaded_log_file.txt");

        try(OutputStream os = new FileOutputStream(tmp)){

            os.write(multipartFile.getBytes());

        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }

        //add random number of trash lines for randomizing file size
        FileWriter fileWriter = new FileWriter("src/main/resources/uploaded_log_file.txt",true);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        int numOfLines = (int)(Math.random() * 200);
        for (int i=0;i< numOfLines; i++){
            printWriter.print("//padding line for randomized size -> logstash reasons\n");
        }
        printWriter.close();

        return true;
    }

    @PostMapping("/uploadLogs")
    public String uploadLogs(@RequestParam("file") MultipartFile multipartFile) throws Exception{

        //1. check index existence, then either delete + create or only create
        if (indexExists("logs")){
            //delete it, then create it
            deleteIndex("logs");
            createIndex("logs");

        }else{
            //create it
            createIndex("logs");

        }

        //4. save uploaded file to desired location, this triggers logstash (only if it is different in size)
        /*can be cured by dirty solution - add random number of trash lines so the final saved file would not have the same size*/

        saveMultipartFile(multipartFile);





/*

        //this part is messy, first I need to delete index, then create it - create request is send asynchronously (this works),
        //but after that idk how to tell elasticsearch to get logstash input from saved file    *******

        //delete index
        //help https://www.javaguides.net/2019/05/okhttp-delete-request-java-example.html
        Request requestDeleteIndex = new Request.Builder()
                .url("http://localhost:9200/logs/").delete()
                .build();

        try (Response responseDeleteIndex = client.newCall(requestDeleteIndex).execute()) {
            if (!responseDeleteIndex.isSuccessful()) throw new IOException("Unexpected code " + responseDeleteIndex);

            Headers responseDeleteIndexHeaders = responseDeleteIndex.headers();
            for (int i = 0; i < responseDeleteIndexHeaders.size(); i++) {
                System.out.println(responseDeleteIndexHeaders.name(i) + ": " + responseDeleteIndexHeaders.value(i));
            }

            System.out.println(responseDeleteIndex.body().string());
        }

        //create index - async
        //help https://stackoverflow.com/questions/34886172/okhttp-put-example
        RequestBody requestBody = RequestBody.create(null, new byte[0]);

        Request requestCreateIndex = new Request.Builder()
                .url("http://localhost:9200/logs").put(requestBody)
                .build();

        client.newCall(requestCreateIndex).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    //here call refresh - refresh will do nothing, i need to tell elasticsearch to get data from logstash from saved file
                    //refresh index
                    //request send with help from https://square.github.io/okhttp/recipes/
                    Request requestRefreshIndex = new Request.Builder()
                            .url("http://localhost:9200/logs/_refresh")
                            .build();

                    try (Response responseRefreshIndex = client.newCall(requestRefreshIndex).execute()) {
                        if (!responseRefreshIndex.isSuccessful()) throw new IOException("Unexpected code " + responseRefreshIndex);

                        Headers responseRefreshIndexHeaders = responseRefreshIndex.headers();
                        for (int i = 0; i < responseRefreshIndexHeaders.size(); i++) {
                            System.out.println(responseRefreshIndexHeaders.name(i) + ": " + responseRefreshIndexHeaders.value(i));
                        }

                        System.out.println(responseRefreshIndex.body().string());
                    }


                    System.out.println(responseBody.string());
                }
            }
        });


        */

        /*
        try (Response responseCreateIndex = client.newCall(requestCreateIndex).execute()) {
            if (!responseCreateIndex.isSuccessful()) throw new IOException("Unexpected code " + responseCreateIndex);

            Headers responseCreateIndexHeaders = responseCreateIndex.headers();
            for (int i = 0; i < responseCreateIndexHeaders.size(); i++) {
                System.out.println(responseCreateIndexHeaders.name(i) + ": " + responseCreateIndexHeaders.value(i));
            }

            System.out.println(responseCreateIndex.body().string());
        }*/


        /*
        //refresh index
        //request send with help from https://square.github.io/okhttp/recipes/
        Request requestRefreshIndex = new Request.Builder()
                .url("http://localhost:9200/logs/_refresh")
                .build();

        try (Response responseRefreshIndex = client.newCall(requestRefreshIndex).execute()) {
            if (!responseRefreshIndex.isSuccessful()) throw new IOException("Unexpected code " + responseRefreshIndex);

            Headers responseRefreshIndexHeaders = responseRefreshIndex.headers();
            for (int i = 0; i < responseRefreshIndexHeaders.size(); i++) {
                System.out.println(responseRefreshIndexHeaders.name(i) + ": " + responseRefreshIndexHeaders.value(i));
            }

            System.out.println(responseRefreshIndex.body().string());
        }
        */

        return "OK";

    }

    @PostMapping("/uploadPetriNet")
    public String uploadPetriNet(@RequestParam("file") MultipartFile multipartFile){

        //inspired by https://stackoverflow.com/questions/50890359/sending-files-from-angular-6-application-to-spring-boot-web-api-required-reques
        //inspired by https://www.baeldung.com/spring-multipartfile-to-file

        if (multipartFile.isEmpty()) {
            return null;
        }

        File tmp = new File("src/main/resources/uploaded_petri_net_file.xml");

        try(OutputStream os = new FileOutputStream(tmp)){

            os.write(multipartFile.getBytes());

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

        return "OK";

    }

    @GetMapping("/indexAndParseLogs")
    public String indexAndParseLogs(){
        return "OK";
    }

    //deprecated
    //@PostMapping("/uploadLogs")
    public String uploadLogs__DEPRECATED(@RequestParam("file") MultipartFile multipartFile) throws Exception{

        //inspired by https://stackoverflow.com/questions/50890359/sending-files-from-angular-6-application-to-spring-boot-web-api-required-reques
        //inspired by https://www.baeldung.com/spring-multipartfile-to-file


        //this part is ok************
        if (multipartFile.isEmpty()) {
            return null;
        }

        File tmp = new File("src/main/resources/uploaded_log_file.txt");

        try(OutputStream os = new FileOutputStream(tmp)){

            os.write(multipartFile.getBytes());

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

/*

        //this part is messy, first I need to delete index, then create it - create request is send asynchronously (this works),
        //but after that idk how to tell elasticsearch to get logstash input from saved file    *******

        //delete index
        //help https://www.javaguides.net/2019/05/okhttp-delete-request-java-example.html
        Request requestDeleteIndex = new Request.Builder()
                .url("http://localhost:9200/logs/").delete()
                .build();

        try (Response responseDeleteIndex = client.newCall(requestDeleteIndex).execute()) {
            if (!responseDeleteIndex.isSuccessful()) throw new IOException("Unexpected code " + responseDeleteIndex);

            Headers responseDeleteIndexHeaders = responseDeleteIndex.headers();
            for (int i = 0; i < responseDeleteIndexHeaders.size(); i++) {
                System.out.println(responseDeleteIndexHeaders.name(i) + ": " + responseDeleteIndexHeaders.value(i));
            }

            System.out.println(responseDeleteIndex.body().string());
        }

        //create index - async
        //help https://stackoverflow.com/questions/34886172/okhttp-put-example
        RequestBody requestBody = RequestBody.create(null, new byte[0]);

        Request requestCreateIndex = new Request.Builder()
                .url("http://localhost:9200/logs").put(requestBody)
                .build();

        client.newCall(requestCreateIndex).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    //here call refresh - refresh will do nothing, i need to tell elasticsearch to get data from logstash from saved file
                    //refresh index
                    //request send with help from https://square.github.io/okhttp/recipes/
                    Request requestRefreshIndex = new Request.Builder()
                            .url("http://localhost:9200/logs/_refresh")
                            .build();

                    try (Response responseRefreshIndex = client.newCall(requestRefreshIndex).execute()) {
                        if (!responseRefreshIndex.isSuccessful()) throw new IOException("Unexpected code " + responseRefreshIndex);

                        Headers responseRefreshIndexHeaders = responseRefreshIndex.headers();
                        for (int i = 0; i < responseRefreshIndexHeaders.size(); i++) {
                            System.out.println(responseRefreshIndexHeaders.name(i) + ": " + responseRefreshIndexHeaders.value(i));
                        }

                        System.out.println(responseRefreshIndex.body().string());
                    }


                    System.out.println(responseBody.string());
                }
            }
        });


        */

        /*
        try (Response responseCreateIndex = client.newCall(requestCreateIndex).execute()) {
            if (!responseCreateIndex.isSuccessful()) throw new IOException("Unexpected code " + responseCreateIndex);

            Headers responseCreateIndexHeaders = responseCreateIndex.headers();
            for (int i = 0; i < responseCreateIndexHeaders.size(); i++) {
                System.out.println(responseCreateIndexHeaders.name(i) + ": " + responseCreateIndexHeaders.value(i));
            }

            System.out.println(responseCreateIndex.body().string());
        }*/


        /*
        //refresh index
        //request send with help from https://square.github.io/okhttp/recipes/
        Request requestRefreshIndex = new Request.Builder()
                .url("http://localhost:9200/logs/_refresh")
                .build();

        try (Response responseRefreshIndex = client.newCall(requestRefreshIndex).execute()) {
            if (!responseRefreshIndex.isSuccessful()) throw new IOException("Unexpected code " + responseRefreshIndex);

            Headers responseRefreshIndexHeaders = responseRefreshIndex.headers();
            for (int i = 0; i < responseRefreshIndexHeaders.size(); i++) {
                System.out.println(responseRefreshIndexHeaders.name(i) + ": " + responseRefreshIndexHeaders.value(i));
            }

            System.out.println(responseRefreshIndex.body().string());
        }
        */

        return "OK";

    }

}