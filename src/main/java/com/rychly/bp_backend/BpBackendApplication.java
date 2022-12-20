package com.rychly.bp_backend;

import com.rychly.bp_backend.model.PetriNet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class BpBackendApplication {



    public static void main(String[] args) {
        SpringApplication.run(BpBackendApplication.class, args);





    }




}
