package com.rychly.bp_backend;

import com.rychly.bp_backend.model.PetriNet;

import java.io.File;

public interface IMyService {

    public PetriNet processReceivedXMLFile(File xmlFile);
    public void processReceivedLogs();
    public void parsePetriNet();
    public File parseLogs(File file);
    public void calculateFinalStateOfNet();
    public void simulateTokenFlow();
    public void generateProcessNet();


}
