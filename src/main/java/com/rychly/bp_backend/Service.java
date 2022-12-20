package com.rychly.bp_backend;

import com.rychly.bp_backend.comparators.Log;
import com.rychly.bp_backend.comparators.logComparator;
import com.rychly.bp_backend.model.PetriNet;
import com.rychly.bp_backend.responses.FiredTransitionsResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class Service {


    public ArrayList<String> firedTransitions = new ArrayList<String>();

    public HashMap<String,ArrayList<String>> caseToFiredTransitions = new HashMap<String,ArrayList<String>>();

    private final OkHttpClient client = new OkHttpClient();

    private final String ELASTIC_PATH = "http://localhost:9200/";

    //private static Logger logger = LogManager.getLogger(Service.class);

    Logger logger = LoggerFactory.getLogger(Service.class);

    /*
    public void testLogger(){
        logger.log(Level.INFO,"logger working");
        //logger.log(Level.DEBUG,"");
    }*/

    /*
    public void initializeLogger() throws IOException{
        InputStream inputStream = new FileInputStream("src\\main\\resources\\log4j2.xml");
        ConfigurationSource source = new ConfigurationSource(inputStream);
        Configurator.initialize(null, source);

    }*/


    private boolean indexExists(String indexName) throws Exception{

        Request request = new Request.Builder().url(ELASTIC_PATH+indexName+"/").head().build();
        Response response = client.newCall(request).execute();
        boolean success = response.isSuccessful();
        if(success){
            //System.out.println("Index exists");
            logger.info("Index exists");
        }else{
            //System.out.println("Index does not exist");
            logger.info("Index does not exist");
        }
        response.body().close();
        return success;
    }

    private boolean deleteIndex(String indexName) throws Exception{

        Request request = new Request.Builder().url(ELASTIC_PATH+indexName+"/").delete().build();
        Response response = client.newCall(request).execute();
        boolean success = response.isSuccessful();

        if(success){
            //System.out.println("Index deleted");
            logger.info("Index deleted");

        }else{
            //System.out.println("Index not deleted");
            logger.info("Index not deleted");

        }
        response.body().close();
        return success;
    }

    private boolean createIndex(String indexName) throws Exception{
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder().url(ELASTIC_PATH+indexName+"/").put(requestBody).build();
        Response response = client.newCall(request).execute();
        boolean success = response.isSuccessful();
        if(success){
            //System.out.println("Index created");
            logger.info("Index created");
        }else{
            //System.out.println("Index not created");
            logger.info("Index not created");
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

    private ArrayList<String> readLogFileLineByLineAndCreateMapping(String modelId){


        //inspired by https://www.javatpoint.com/how-to-read-file-line-by-line-in-java        java read file
        //inspired by https://www.w3schools.com/java/java_regex.asp                           java regexp
        //inspired by https://www.freeformatter.com/java-regex-tester.html#before-output      testing regex pattern on logs


        Pattern pattern = Pattern.compile("^.*created for petri net with id ." + modelId +".*$", Pattern.CASE_INSENSITIVE);
        ArrayList<String> matchedLines = new ArrayList<>();


        try
        {

            //System.out.println("printing the logs");
            logger.info("printing the logs");
            //the file to be opened for reading
            FileInputStream fis=new FileInputStream("src/main/resources/uploaded_log_file.txt");
            Scanner sc=new Scanner(fis);    //file to be scanned
            //returns true if there is another line to read
            while(sc.hasNextLine())
            {

                String line = sc.nextLine();

                Matcher matcher = pattern.matcher(line);

                boolean matchFound = matcher.find();

                if(matchFound) {
                    //System.out.println(line);
                    logger.info(line);
                    matchedLines.add(line);
                } else {

                }

            }
            sc.close();     //closes the scanner
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


        ArrayList<String> caseNames = new ArrayList<String>();
        //System.out.println("printing case names for the net");
        logger.info("printing case names for the net");
        for (String log:matchedLines){
            caseNames.add(log.split("Case ")[1].split(" ")[0]);

        }
        return caseNames;
    }

    private boolean isIndexEmpty(String indexName) throws Exception{

        try {

            Request request = new Request.Builder().url(ELASTIC_PATH + indexName + "/_search?size=1000").get().build();
            Response response = client.newCall(request).execute();

            boolean success = response.isSuccessful();
            if (success) {

                String responseString = response.body().string();

                //System.out.println("Checking if index empty");
                //System.out.println(responseString);

                logger.info("Checking if index empty");
                logger.info(responseString);

                //parse response to json - not needed
                JSONObject obj = new JSONObject(responseString);

                int numberOfHits = Integer.parseInt(obj.getJSONObject("hits").getJSONObject("total").getString("value"));
                //System.out.println(numberOfHits);
                //logger.info(String(numberOfHits));


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
                //System.out.println("ERR: Could not check if index is empty");
                logger.info("ERR: Could not check if index is empty");
                return true;
            }
        }catch(Exception e){

            //System.out.print(e.getCause());
           // logger.info(e.getCause());
        }
        return true;
    }

    private ArrayList<Log> extractLogs(String indexName, String caseName) throws Exception{

        Request request = new Request.Builder().url(ELASTIC_PATH+indexName+"/_search?size=1000").get().build();
        Response response = client.newCall(request).execute();
        boolean success = response.isSuccessful();

        if(success){
            String responseString = response.body().string();
            JSONObject firedJSON = new JSONObject(responseString);
            JSONArray ja = firedJSON.getJSONObject("hits").getJSONArray("hits");

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


            //System.out.println("printing items in list");
            logger.info("printing items in list");

            for(int i=0;i<list.size();i++){
                //System.out.println(list.get(i));
                logger.info(list.get(i).toString());
            }

            //sort array list version

            return list;

            //return ja.toString();

        }else{
            //System.out.println("ERR: Could not get index data");
            logger.info("ERR: Could not get index data");
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

    public void marshal(PetriNet pn,String caseName) throws JAXBException, IOException {


        JAXBContext context = JAXBContext.newInstance(PetriNet.class);
        Marshaller mar= context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(pn, new File("src/main/resources/processNet_case-" + caseName + ".xml"));
    }


    public void zipFiles(ArrayList<String> caseNames){

        try{


            String fileName;

            final List<String> srcFiles = new ArrayList<>();
            for (String cn: caseNames){
                fileName = "src/main/resources/processNet_case-"+cn+".xml";
                srcFiles.add(fileName);
            }

            final FileOutputStream fos = new FileOutputStream("src/main/resources/compressed.zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            for (String srcFile : srcFiles) {
                File fileToZip = new File(srcFile);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }

            zipOut.close();
            fos.close();
        }catch (Exception e){
            //System.out.println(e.getMessage());
            logger.info(e.getMessage());
        }
    }

    //write to file inspired by https://www.baeldung.com/java-write-to-file
    public void saveStringRepresentationOfModelLocally(String str) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/uploaded_petri_net_file.xml"));
        writer.write(str);

        writer.close();
    }

    public void cleanUp(){

        //inspired by https://www.geeksforgeeks.org/files-deleteifexists-method-in-java-with-examples/
        try{

            Path dir = FileSystems.getDefault().getPath( "src/main/resources/" );
            DirectoryStream<Path> stream = Files.newDirectoryStream( dir, "*.{xml,zip}" );
            for (Path path : stream) {
                Files.deleteIfExists(path);

            }
            stream.close();
            //System.out.println("clean up successful");
            logger.info("clean up successful");

        }catch (Exception e){
            //System.out.println("clean up failed");
            logger.info("clean up failed");
        }


    }

    public Resource getFile(HttpServletResponse response) {

        return getResource(response);

    }

    private Resource getResource(HttpServletResponse response) {

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=compressed.zip");
        response.setStatus(HttpServletResponse.SC_OK);
        Resource resource = resource = new FileSystemResource("C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\main\\resources\\compressed.zip");  //current version - downloads zip archive of process nets for all cases
        return resource;
    }



    public FiredTransitionsResponse uploadLogs(MultipartFile multipartFile,
                                               String caseName,
                                               String modelId) throws Exception{



        //0.1 print caseName and model id
        //System.out.println("Case name: ");
        //System.out.println(caseName);
        //System.out.println("Model id: ");
        //System.out.println(modelId);

        logger.info("Case name: ");
        logger.info(caseName);
        logger.info("Model id: ");
        logger.info(modelId);

        //0.2 perform cleanup
        cleanUp();

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

        ArrayList<String> caseNamesForTheModel = readLogFileLineByLineAndCreateMapping(modelId);

        //here the logstash should be working
        //checking, if thh index is empty - then sleep
        while(isIndexEmpty("logs")){

            //sleep
            Thread.sleep(200);
        }

        //new process for mode cases

        for (String cn:caseNamesForTheModel){

            ArrayList<Log> logs = extractLogs("logs",cn);
            ArrayList<String> fired = extractFiredTransitions(logs);
            this.caseToFiredTransitions.put(cn,fired);
            caseName = cn;

        }

        //System.out.println(caseToFiredTransitions.toString());
        logger.info(caseToFiredTransitions.toString());

        //OLD process for one case id - TODO remove
        //send fired to frontend - it will send fired sequence from the last
        //case to frontend, but it is unnecessary
        ArrayList<Log> logs = extractLogs("logs",caseName);
        ArrayList<String> fired = extractFiredTransitions(logs);
        this.firedTransitions = fired;

        return new FiredTransitionsResponse(fired.toString());



    }

    public String uploadPetriNetAsString(String str) throws Exception{

        //1. save original net xml file
        saveStringRepresentationOfModelLocally(str);

        for (String caseName:this.caseToFiredTransitions.keySet()){
            //2. parse it - > create object of that petri net, use JAXB
            PetriNet originalPetriNet = unmarshall("src/main/resources/uploaded_petri_net_file.xml");
            PetriNet processNet = originalPetriNet.simulateTokenFlow(this.caseToFiredTransitions.get(caseName));
            marshal(processNet,caseName);

        }

        //lazy convert of keyset to array list of strings
        ArrayList<String> caseNames = new ArrayList<>();
        for (String cn:this.caseToFiredTransitions.keySet()){
            caseNames.add(cn);
        }

        //zip files
        zipFiles(caseNames);



        return "OK";

    }





}