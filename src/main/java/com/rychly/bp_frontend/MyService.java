package com.rychly.bp_frontend;

import com.rychly.bp_frontend.model.*;
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
    public PetriNet test(){

        Place p1 = new Place("p1",10,10,null,1,false);
        Place p2 = new Place("p2",20,20,null,0,false);
        Transition t1 = new Transition("t1",15,15,null);
        Arc a1 = new Arc("a1","regular","p1","t1",1);
        Arc a2 = new Arc("a2","regular","t1","p2",1);

        ArrayList<Place> places = new ArrayList<Place>();
        ArrayList<Transition> transitions = new ArrayList<Transition>();
        ArrayList<Arc> arcs = new ArrayList<Arc>();
        places.add(p1);
        places.add(p2);
        transitions.add(t1);
        arcs.add(a1);
        arcs.add(a2);

        PetriNet pn = new PetriNet(places,transitions,arcs);
        return pn;
    }
}
