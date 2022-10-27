package com.rychly.bp_backend.model;

import lombok.Getter;
import lombok.Setter;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.*;
import java.util.*;

@Setter
@XmlRootElement(name = "document")
public class PetriNet {


    @XmlElement(name = "place")
    private List<Place> places = null;

    @XmlElement(name = "transition")
    private List<Transition> transitions = null;

    @XmlElement(name = "arc")
    private List<Arc> arcs = null;

    @XmlTransient
    private int processNetPlacesCurrentId = 1;
    @XmlTransient
    private int processNetCurrentTransitionId = 1;

    @XmlTransient
    private int processNetCurrentArcId = 1;

    @XmlTransient
    private final int PIXEL_CONSTANT = 6;

    @XmlTransient
    private int currentX = 10 * PIXEL_CONSTANT;

    @XmlTransient
    private int currentY = 10 * PIXEL_CONSTANT;




    public String getPlaceLabelById(String id){
        String label = null;
        for(Place p: this.places){
            if(p.getId().equals(id)){
                label = p.getLabel();
            }

        }
        return label;
    }

    public PetriNet fireTransition(String transitionLabel, PetriNet processNet){



        //localize transition
        Transition firedTransition = null;
        for(Transition t: this.transitions){
            if(t.getLabel().equals(transitionLabel)){
                firedTransition = t;
            }
        }

        //get input arcss
        List<Arc> inputArcs = new ArrayList<Arc>();
        for(Arc a: this.arcs){
            if(a.getDestinationId().equals(firedTransition.getId())){
                inputArcs.add(a);
            }
        }

        //get output arcs
        List<Arc> outputArcs = new ArrayList<Arc>();
        for(Arc a: this.arcs){
            if(a.getSourceId().equals(firedTransition.getId())){
                outputArcs.add(a);
            }
        }

        //consume tokens from input place, number of tokens is multiplicity of input arc
        for(Arc a: inputArcs){
            String labelOfInputPlace = a.getSourceId();
            int multiplicity = a.getMultiplicity();

            for(Place p: this.places){
                if(p.getId().equals(labelOfInputPlace)){
                    p.setTokens(p.getTokens()-multiplicity);
                }
            }
        }

        //produce tokens into the output place, number of tokens is multiplicity of output arc
        for(Arc a: outputArcs){

            String labelOfOutputPlace = a.getDestinationId();
            int multiplicity = a.getMultiplicity();

            for(Place p: this.places){
                if(p.getId().equals(labelOfOutputPlace)){
                    p.setTokens(p.getTokens()+multiplicity);

                }
            }
        }

        //**********************
        //process net stuff
        //**********************

        //add fired transition into the process net
        Transition transitionInProcessNet = new Transition();
        transitionInProcessNet.setId("t"+processNetCurrentTransitionId++);
        transitionInProcessNet.setX(0);
        transitionInProcessNet.setY(0);

        transitionInProcessNet.setLabel(firedTransition.getLabel());
        transitionInProcessNet.setIdOfTheOriginalTransition(firedTransition.getId());

        processNet.transitions.add(transitionInProcessNet);

        //add arcs between transition and input places in process net

        //get input arcs in the original net
        ArrayList<Arc> inputArcsOfTheFiredTransitionInOriginalNet = getInputArcsOfTheTransition(firedTransition.getId());

        //for each input arc add multiplicity of arcs between place grouped by origin id anf new transition in process net

        for (Arc a:inputArcsOfTheFiredTransitionInOriginalNet) {

            //add multiplicity of arc between places with origin id and transitionInProcessNet
            String sourceId = a.getSourceId();
            String destId = transitionInProcessNet.getId();

            for (int i = 0; i< a.getMultiplicity();i++){

                Place nextFreePlaceInProcessNet = findNextFreePlaceInProcessNet(processNet,sourceId);

                Arc arcInProcessNet = new Arc();
                arcInProcessNet.setId("a"+processNetCurrentArcId++);
                arcInProcessNet.setSourceId(nextFreePlaceInProcessNet.getId());
                arcInProcessNet.setDestinationId(transitionInProcessNet.getId());
                arcInProcessNet.setMultiplicity(1);

                //add the arc to the process net
                processNet.arcs.add(arcInProcessNet);
            }


        }


        //get output arcs in the original net
        ArrayList<Arc> outputArcsOfTheFiredTransitionInOriginalNet = getOutputArcsOfTheTransition(firedTransition.getId());

        //for each output arc add multiplicity of arcs between the new transition in process net and new place

        for (Arc a:outputArcsOfTheFiredTransitionInOriginalNet) {

            //add multiplicity of arc between transitionInProcessNet and new places
            //add new places

            for (int i=0;i<a.getMultiplicity();i++){

                Place newPlaceInProcessNet = new Place();
                newPlaceInProcessNet.setId("p"+this.processNetPlacesCurrentId++);
                newPlaceInProcessNet.setLabel(a.getDestinationId());
                newPlaceInProcessNet.setX(0);
                newPlaceInProcessNet.setY(0);


                newPlaceInProcessNet.setTokens(0);
                newPlaceInProcessNet.setIdOfTheOriginPlace(a.getDestinationId());

                processNet.places.add(newPlaceInProcessNet);

                //add arc

                Arc newArcInTheProcessNet = new Arc();
                newArcInTheProcessNet.setId("a"+this.processNetCurrentArcId++);
                newArcInTheProcessNet.setSourceId(transitionInProcessNet.getId());
                newArcInTheProcessNet.setDestinationId(newPlaceInProcessNet.getId());
                newArcInTheProcessNet.setMultiplicity(1);

                processNet.arcs.add(newArcInTheProcessNet);

            }


        }



        return processNet;

    }

    public PetriNet simulateTokenFlow(List<String> firedTransitions){


        //returns process net calculated by simulating token flow

        PetriNet processNet = new PetriNet();
        processNet.transitions = new ArrayList<Transition>();
        processNet.places = new ArrayList<Place>();
        processNet.arcs = new ArrayList<Arc>();

        //add initial places to the process net
        processNet = addInitialPlacesToProcessNet(processNet);

        //changes the state of the petri net based on the sequence of fired transitions labels
        for(String transitionLabel : firedTransitions){

            //fire every fired transition from the sequence
            processNet = fireTransition(transitionLabel,processNet);
        }

        //dfs
        depthFirstTraversal();

        return processNet;

    }

    @Override
    public String toString() {
        return "PetriNet{" +
                "Places=" + places +
                ", Transitions=" + transitions +
                ", Arcs=" + arcs +
                '}';
    }


    //add initial places
    public PetriNet addInitialPlacesToProcessNet(PetriNet processNet){

        //find place with token/s
        Place initialPlace = null;

        ArrayList<Place> firstPlacesInProcessNet = new ArrayList<Place>();

        for (Place p: this.places){
            if(p.getTokens()!=0){

                initialPlace = p;

                //create new places that represent tokens

                for (int i=0;i<p.getTokens();i++){

                    Place place = new Place();
                    place.setId("p"+processNetPlacesCurrentId++);
                    place.setX(0); //get algo for nice placing of the elements
                    place.setY(0); //get algo for nice placing of the elements
                    place.setLabel(p.getLabel());
                    place.setTokens(1);
                    place.setIdOfTheOriginPlace(p.getId());
                    firstPlacesInProcessNet.add(place);

                }

                break;
            }
        }

        processNet.setPlaces(firstPlacesInProcessNet);

        return processNet;
    }

    public Place findNextFreePlaceInProcessNet(PetriNet processNet,String originId) {

        for (Place p:processNet.places){



            //if the place has the right origin id
            if(p.getIdOfTheOriginPlace().equals(originId)){

                boolean isFree = true;

                //and has no arc
                for (Arc a:processNet.arcs){

                    if (a.getSourceId().equals(p.getId())){
                        isFree = false;
                        break;
                    }else{
                        isFree = true;

                    }

                }

                if(isFree){
                    return p;
                }
            }



        }

        return null;




    }


    public ArrayList<Arc> getInputArcsOfTheTransition(String transitionId){

        ArrayList<Arc> inputArcs = new  ArrayList<Arc>();

        for(Arc a: this.arcs){
            if(a.getDestinationId().equals(transitionId)){
                inputArcs.add(a);
            }
        }

        return inputArcs;
    }

    public ArrayList<Arc> getOutputArcsOfTheTransition(String transitionId){

        ArrayList<Arc> outputArcs = new  ArrayList<Arc>();

        for(Arc a: this.arcs){
            if(a.getSourceId().equals(transitionId)){
                outputArcs.add(a);
            }
        }

        return outputArcs;
    }



    //todo ended here DFS as algo for nice placement

    public void depthFirstTraversal() {

        Place root = this.places.get(0);
        PetriNet net = this;

        Set<Object> visited = new LinkedHashSet<Object>();
        Stack<Object> stack = new Stack<Object>();

        stack.push(root);





        while (!stack.isEmpty()) {

            Object node = stack.pop();

            if (!visited.contains(node)) {

                if(node instanceof Place){

                    visited.add(((Place)node));

                    for (Object t : net.getAdjNodes(node)) {
                        stack.push(((Transition)t));
                    }


                    //add coordinates to node

                } else if (node instanceof Transition) {

                    visited.add(((Transition)node));

                    for (Object p : net.getAdjNodes(node)) {
                        stack.push(((Place)p));
                    }

                    //add coordinates to node

                }

            }
        }


    }

    public void addCoordinatesToNode(Object node, int x, int y){}

    public List<Object> getAdjNodes(Object node){


        List<Object> adjNodes = new ArrayList<Object>();

        if(node instanceof Place){
            //get only one transition
            for(Arc a:this.arcs){
                if(a.getSourceId().equals(((Place) node).getId())){

                    //add transition to adj nodes
                    Transition adjNode = null;
                    for (Transition t:this.transitions){
                        if(t.getId().equals(a.getDestinationId())){
                            adjNode = t;
                            adjNodes.add(adjNode);
                            break;
                        }
                    }


                }
            }

        }
        else if(node instanceof Transition){

            //get all places
            for(Arc a:this.arcs){

                if(a.getSourceId().equals(((Transition) node).getId())){

                    //add place to adj nodes
                    Place adjNode = null;
                    for (Place p:this.places){
                        if(p.getId().equals(a.getDestinationId())){
                            adjNode = p;
                            adjNodes.add(adjNode);

                        }
                    }


                }
            }

        }

        return adjNodes;

    }



}



