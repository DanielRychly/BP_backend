package com.rychly.bp_backend;

import com.rychly.bp_backend.model.PetriNet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class Controller {

    @Autowired
    private IMyService service;

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
    public String uploadLogs(@RequestParam("file") MultipartFile multipartFile){

        //inspired by https://stackoverflow.com/questions/50890359/sending-files-from-angular-6-application-to-spring-boot-web-api-required-reques
        //inspired by https://www.baeldung.com/spring-multipartfile-to-file

        if (multipartFile.isEmpty()) {
            return null;
        }

        File tmp = new File("src/main/resources/uploaded_log_file.tmp");

        try(OutputStream os = new FileOutputStream(tmp)){

            os.write(multipartFile.getBytes());

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

        return "OK";

    }

    @PostMapping("/uploadPetrNet")
    public String uploadPetriNet(@RequestParam("file") MultipartFile multipartFile){

        //inspired by https://stackoverflow.com/questions/50890359/sending-files-from-angular-6-application-to-spring-boot-web-api-required-reques
        //inspired by https://www.baeldung.com/spring-multipartfile-to-file

        if (multipartFile.isEmpty()) {
            return null;
        }

        File tmp = new File("src/main/resources/uploaded_petri_net_file.tmp");

        try(OutputStream os = new FileOutputStream(tmp)){

            os.write(multipartFile.getBytes());

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

        return "OK";

    }


}