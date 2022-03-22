package com.rychly.bp_backend;

import com.rychly.bp_backend.comparators.Log;
import com.rychly.bp_backend.comparators.logComparator;
import com.rychly.bp_backend.responses.FiredTransitionsResponse;
import okhttp3.*;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.util.ArrayList;

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

    private ArrayList<Log> extractLogs(String indexName) throws Exception{

        Request request = new Request.Builder().url("http://localhost:9200/"+indexName+"/_search?size=1000").get().build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()){
            String responseString = response.body().string();
            JSONObject firedJSON = new JSONObject(responseString);
            JSONArray ja = firedJSON.getJSONObject("hits").getJSONArray("hits");
            //System.out.println("ExtractedSequence:");
            //System.out.println(ja.toString());

            //get array list version
            ArrayList<Log> list = new ArrayList<Log>();
            //ArrayList<JSONObject> list = new ArrayList<JSONObject>();
            for(int i=0;i<ja.length();i++){


                Log l = new Log();

                l.setYear2TS(ja.getJSONObject(i).getJSONObject("_source").get("year2TS").toString());
                l.setMonth2TS(ja.getJSONObject(i).getJSONObject("_source").get("month2TS").toString());
                l.setDay2TS(ja.getJSONObject(i).getJSONObject("_source").get("day2TS").toString());

                l.setHour2TS(ja.getJSONObject(i).getJSONObject("_source").get("hour2TS").toString());
                l.setMinute2TS(ja.getJSONObject(i).getJSONObject("_source").get("minute2TS").toString());
                l.setSecond2TS(ja.getJSONObject(i).getJSONObject("_source").get("second2TS").toString());

                l.setCase_id(ja.getJSONObject(i).getJSONObject("_source").get("case_id").toString());

                try{
                    //some logs dont have fired transition id - tranition wasnt named
                    l.setFired_transition_id(ja.getJSONObject(i).getJSONObject("_source").get("fired_transition_id").toString());

                }catch (Exception e){

                }



                //list.add(ja.getJSONObject(i).getJSONObject("_source"));
                list.add(l);
            }

            //sort array of indexed and stripped logs
            list.sort(new logComparator());


            System.out.println("printing items in list");
            for(int i=0;i<list.size();i++){
                System.out.println(list.get(i));
            }

            //sort array list version

            return list;

            //return ja.toString();

        }else{
            System.out.println("ERR: Could not get index data");
            return null;
        }

    }

    private ArrayList<String> extractFiredTransitions(ArrayList<Log> logs){
        ArrayList<String> transitions = new ArrayList<String>();

        for(int i =0;i<logs.size();i++){
            transitions.add(logs.get(i).getFired_transition_id());
        }

        return transitions;
    }

    @PostMapping("/uploadLogs")
    public FiredTransitionsResponse uploadLogs(@RequestParam("file") MultipartFile multipartFile) throws Exception{

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
        ArrayList<Log> logs = extractLogs("logs");
        ArrayList<String> fired = extractFiredTransitions(logs);

        //System.out.println("hits as json array");
        System.out.println("fired:");
        System.out.println(fired);

        return new FiredTransitionsResponse(fired.toString());

        //return fired;
        //return null;

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