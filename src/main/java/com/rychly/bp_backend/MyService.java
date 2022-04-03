package com.rychly.bp_backend;

import com.rychly.bp_backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

@Service
public class MyService implements IMyService{

    private PetriNet currentPN;

    @Autowired
    public MyService(){}

    @Override
    public PetriNet processReceivedXMLFile(File xmlFile){

        //TODO
        return null;
    }

    @Override
    public void processReceivedLogs() {

    }

    @Override
    public void parsePetriNet() {

    }

    @Override
    public File parseLogs(File file) {
        return file;
    }

    @Override
    public void calculateFinalStateOfNet() {

    }

    @Override
    public void simulateTokenFlow() {

    }

    @Override
    public void generateProcessNet() {

    }


}
