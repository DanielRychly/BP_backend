package com.rychly.bp_backend;


import com.rychly.bp_backend.comparators.Log;
import com.rychly.bp_backend.comparators.logComparator;
import com.rychly.bp_backend.model.PetriNet;
import com.rychly.bp_backend.responses.FiredTransitionsResponse;
import okhttp3.*;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public FiredTransitionsResponse uploadLogs(@RequestParam("file") MultipartFile multipartFile,
                                               @RequestParam("caseName") String caseName,
                                               @RequestParam("modelId") String modelId) throws Exception{

        return this.service.uploadLogs(multipartFile,caseName,modelId);


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