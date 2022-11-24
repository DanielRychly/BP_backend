package com.rychly.bp_backend;

import com.rychly.bp_backend.ModelerModel.Model;
import com.rychly.bp_backend.comparators.Log;
import com.rychly.bp_backend.comparators.logComparator;
import com.rychly.bp_backend.model.PetriNet;
import com.rychly.bp_backend.responses.FiredTransitionsResponse;
import okhttp3.*;
import okhttp3.RequestBody;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.mock.web.MockMultipartFile;



@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class Controller {

    public ArrayList<String> firedTransitions = new ArrayList<String>();

    @Autowired
    public Controller(FileService fileService) {
        this.fileService = fileService;
    }

    private final OkHttpClient client = new OkHttpClient();

    private final FileService fileService;

    private boolean indexExists(String indexName) throws Exception{

        Request request = new Request.Builder().url("http://localhost:9200/"+indexName+"/").head().build();
        Response response = client.newCall(request).execute();

        boolean success = response.isSuccessful();

        if(success){
            System.out.println("Index exists");

        }else{
            System.out.println("Index does not exist");

        }
        response.body().close();

        return success;
    }

    private boolean deleteIndex(String indexName) throws Exception{

        Request request = new Request.Builder().url("http://localhost:9200/"+indexName+"/").delete().build();
        Response response = client.newCall(request).execute();
        boolean success = response.isSuccessful();

        if(success){
            System.out.println("Index deleted");

        }else{
            System.out.println("Index not deleted");

        }
        response.body().close();
        return success;
    }

    private boolean createIndex(String indexName) throws Exception{
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder().url("http://localhost:9200/"+indexName+"/").put(requestBody).build();
        Response response = client.newCall(request).execute();
        boolean success = response.isSuccessful();
        if(success){
            System.out.println("Index created");
        }else{
            System.out.println("Index not created");
        }
        response.body().close();

        return success;
    }

    private boolean saveMultipartFile(MultipartFile multipartFile, String filename) throws Exception{

        //inspired by https://stackoverflow.com/questions/50890359/sending-files-from-angular-6-application-to-spring-boot-web-api-required-reques
        //inspired by https://www.baeldung.com/spring-multipartfile-to-file


        if (multipartFile.isEmpty()) {
            return false;
        }



        File tmp = new File("src/main/resources/" + filename);

        try(OutputStream os = new FileOutputStream(tmp)){

            os.write(multipartFile.getBytes());


        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }


        return true;
    }



    private boolean isIndexEmpty(String indexName) throws Exception{

        try {

            Request request = new Request.Builder().url("http://localhost:9200/" + indexName + "/_search?size=1000").get().build();
            Response response = client.newCall(request).execute();

            boolean success = response.isSuccessful();
            if (success) {

                String responseString = response.body().string();

                System.out.println("Checking if index empty");
                System.out.println(responseString);

                //parse response to json - not needed
                JSONObject obj = new JSONObject(responseString);

                int numberOfHits = Integer.parseInt(obj.getJSONObject("hits").getJSONObject("total").getString("value"));
                System.out.println(numberOfHits);


                if(numberOfHits==0){
                    //index is empty, logstash has not feeded it

                    response.body().close();
                    return true;
                }else{

                    response.body().close();
                    return false;
                }




            } else {
                response.body().close();
                System.out.println("ERR: Could check if index is empty");
                return true;
            }
        }catch(Exception e){

            System.out.print(e.getCause());
        }
        return true;
    }

    private ArrayList<Log> extractLogs(String indexName,String caseName) throws Exception{

        Request request = new Request.Builder().url("http://localhost:9200/"+indexName+"/_search?size=1000").get().build();
        Response response = client.newCall(request).execute();
        boolean success = response.isSuccessful();

        if(success){
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


                //filter only logs with desired case id (case id and case name used interchangeably :/ )

                if (l.getCase_id().equals(caseName)){
                    list.add(l);
                }

                //not filtered logs
                //list.add(l);

            }

            response.body().close();

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

    public PetriNet unmarshall(String filename) throws JAXBException, IOException {

        //inspired by:
        //https://www.baeldung.com/jaxb
        //https://stackoverflow.com/questions/16364547/how-to-parse-xml-to-java-object
        //https://stackoverflow.com/questions/51916221/javax-xml-bind-jaxbexception-implementation-of-jaxb-api-has-not-been-found-on-mo


        try{
            JAXBContext context = JAXBContext.newInstance(PetriNet.class);
            Unmarshaller u = context.createUnmarshaller();
            FileReader fr = new FileReader(filename);
            PetriNet pn = (PetriNet) u.unmarshal(fr);
            return pn;

        }catch(Exception e){
            e.printStackTrace();
            return null;

        }

    }

    public void marshal(PetriNet pn) throws JAXBException, IOException {


        JAXBContext context = JAXBContext.newInstance(PetriNet.class);
        Marshaller mar= context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(pn, new File("src/main/resources/processNet.xml"));
    }

    @PostMapping("/uploadLogs")
    @CrossOrigin(origins = "http://localhost:4200") //does not have to be here
    public FiredTransitionsResponse uploadLogs(@RequestParam("file") MultipartFile multipartFile, @RequestParam("caseName") String caseName) throws Exception{

        //0. print caseName
        System.out.println("Case name: ");
        System.out.println(caseName);

        //1. check index existence, then either delete + create or only create
        if (indexExists("logs")){
            //delete it, then create it
            deleteIndex("logs");
            createIndex("logs");

        }else{
            //create it
            createIndex("logs");

        }

        //4. append logs to log file
        saveMultipartFile(multipartFile,"uploaded_log_file.txt");

        //here the logstash should be working
        //checking, if thh index is empty - then sleep
        while(isIndexEmpty("logs")){

            //sleep
            Thread.sleep(200);
        }

        //send fired to frontend
        ArrayList<Log> logs = extractLogs("logs",caseName);
        ArrayList<String> fired = extractFiredTransitions(logs);
        this.firedTransitions = fired;


        //System.out.println("hits as json array");
        System.out.println("fired:");
        System.out.println(fired);

        return new FiredTransitionsResponse(fired.toString());

        //return fired;
        //return null;

    }

    @PostMapping("/uploadPetriNet")
    public String uploadPetriNet(@RequestParam("file") MultipartFile multipartFile) throws Exception{



        //1. save original net xml file
        saveMultipartFile(multipartFile,"uploaded_petri_net_file.xml");

        //2. parse it - > create object of that petri net, use JAXB
        PetriNet originalPetriNet = unmarshall("src/main/resources/uploaded_petri_net_file.xml");
        //System.out.println("original petri net:");
        //System.out.println(originalPetriNet);

        //3. compute token flow and create process net object
        PetriNet processNet = originalPetriNet.simulateTokenFlow(this.firedTransitions);
        //System.out.println("original petri net after computed token flow:");
        //System.out.println(originalPetriNet);

        //4.create process net xml from object
        marshal(processNet);

        //5. send process net xml to frontend for download


        return "OK";

    }

    @GetMapping(value = "/downloadProcessNet", produces = "text/xml; charset=utf-8")
    @ResponseStatus(HttpStatus.OK)
    public Resource getFileFromFileSystem (HttpServletResponse response) {

        //inspired by https://roufid.com/angular-download-file-spring-boot/
        return fileService.getFile(response);
    }

    @PostMapping("/receiveAndPrintModel")
    public String receiveAndPrintModel(@org.springframework.web.bind.annotation.RequestBody Model model ){
        System.out.println("going to print model:");
        System.out.println(model);
        return "OK";
    }

    @GetMapping("/test")
    public void test(){
        System.out.println("Test request");
    }


}