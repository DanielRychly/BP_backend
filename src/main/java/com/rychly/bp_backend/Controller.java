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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    //deprecated
    @GetMapping("/test")
    public PetriNet greeting() {

        return this.service.test();


    /*
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        counter++;
        return new Greeting(counter, greeting+name);
    }*/
    }


    //deprecated but working
    /*
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile multipartFile){

        //inspired by https://stackoverflow.com/questions/50890359/sending-files-from-angular-6-application-to-spring-boot-web-api-required-reques
        //inspired by https://www.baeldung.com/spring-multipartfile-to-file

        if (multipartFile.isEmpty()) {
            return null;
        }

        File tmp = new File("src/main/resources/uploaded_file.tmp");

        try(OutputStream os = new FileOutputStream(tmp)){

            os.write(multipartFile.getBytes());

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

        return "OK";

    }
*/

    @PostMapping("/uploadLogs")
    public String uploadLogs(@RequestParam("file") MultipartFile multipartFile) throws Exception{

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


}