package com.rychly.bp_backend;

import com.rychly.bp_backend.responses.MyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class Controller {

    private final Service service;

    @Autowired
    public Controller(Service service) {

        this.service = service;

    }

    @PostMapping("/uploadLogs")
    @CrossOrigin(origins = "http://localhost:4200") //does not have to be here
    public MyResponse uploadLogs(@RequestParam("file") MultipartFile multipartFile,
                                 @RequestParam("modelId") String modelId) throws Exception{

        return this.service.uploadLogs(multipartFile,modelId);


    }

    @PostMapping("/uploadPetriNetAsString")
    @CrossOrigin(origins = "http://localhost:4200") //does not have to be here
    public String uploadPetriNetAsString(@RequestParam("netAsString") String  str) throws Exception{

        //version to upload receive petri net xml as a string from frontend

        return this.service.uploadPetriNetAsString(str);

    }

    @GetMapping(value = "/downloadProcessNet")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "http://localhost:4200")
    public Resource getFileFromFileSystem (HttpServletResponse response) {

        //inspired by https://roufid.com/angular-download-file-spring-boot/
        return service.getFile(response);

    }




}