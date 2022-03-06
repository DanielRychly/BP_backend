package com.rychly.bp_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rychly.bp_backend.model.PetriNet;
import okhttp3.*;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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



    private boolean isIndexEmpty(String indexName) throws Exception{

        try {

            Request request = new Request.Builder().url("http://localhost:9200/" + indexName + "/_search?size=1000").get().build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {

                String responseString = response.body().string();

                System.out.println("Checking if index empty");
                System.out.println(responseString);

                //parse response to json - not needed
                JSONObject obj = new JSONObject(responseString);

                int numberOfHits = Integer.parseInt(obj.getJSONObject("hits").getJSONObject("total").getString("value"));
                System.out.println(numberOfHits);


                if(numberOfHits==0){
                    //index is empty, logstash has not feeded it
                    return true;
                }else{
                    //index has data
                    return false;
                }




            } else {
                System.out.println("ERR: Could check if index is empty");
                return true;
            }
        }catch(Exception e){
            System.out.print(e.getCause());
        }
        return true;
    }


    private String extractFiredSequence(String indexName) throws Exception{
        Request request = new Request.Builder().url("http://localhost:9200/"+indexName+"/_search?size=1000").get().build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()){
            String responseString = response.body().string();

            System.out.println("Index data");
            System.out.println(responseString);

            //parse response to json - not needed
            //JSONObject obj = new JSONObject(response.body().string());


            //send it to the frontend to show fired sequence todo ******************
            return responseString;

        }else{
            System.out.println("ERR: Could not get index data");
            return "ERR: Could not get index data";
        }

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

        //here the logstash should be working
        //checking, if thh index is empty - then sleep
        while(isIndexEmpty("logs")){
            //sleep
            Thread.sleep(200);
        }

        //send fired to frontend
        String fired = extractFiredSequence("logs");
        System.out.println(fired);



        return fired;

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






}