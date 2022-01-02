package com.rychly.bp_frontend;

import com.rychly.bp_frontend.model.PetriNet;

import java.io.File;

public interface IMyService {

    public PetriNet processReceivedXMLFile(File xmlFile);
    public void processReceivedLogs();
    public void parsePetriNet();
    public void parseLogs();
    public void calculateFinalStateOfNet();
    public void simulateTokenFlow();
    public void generateProcessNet();

    public PetriNet test();

}
