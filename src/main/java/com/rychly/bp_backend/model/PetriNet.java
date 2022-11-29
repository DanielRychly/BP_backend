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
        //processNet = DFSTraversalToAddCoordinates(processNet);
        processNet = BFSTraversalToAddCoordinates(processNet);

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

    public PetriNet BFSTraversalToAddCoordinates(PetriNet processNet){

        //each node must remember the parent node coordinates
        //and based on them find out if the next spot to right
        //is occupied or not, if so then add to the Y in loop
        //till you find first free spot

        final int PIXEL_CONSTANT = 7;
        int currentX = 10 * PIXEL_CONSTANT;
        int currentY = 10 * PIXEL_CONSTANT;

        Queue<Object> queue = new LinkedList<Object>();
        ArrayList<Point> occupied = new ArrayList<>();
        Set<Object> visited = new LinkedHashSet<Object>();

        Place root = processNet.places.get(0);
        root.setParentLocation(new Point(currentX,currentY));
        occupied.add(new Point(currentX,currentY));

        queue.offer(root);  //add root to the queue

        while (!queue.isEmpty()) {

            //Object node = queue.pop();
            Object node = queue.poll();



            if (!visited.contains(node)){

                if(node instanceof Place){

                    //add location to the node here based on the parent location
                    currentX = ((Place) node).getParentLocation().getX();
                    currentY = ((Place) node).getParentLocation().getY();
                    currentX +=10* PIXEL_CONSTANT;

                    while(isPointInTheArray(occupied,new Point(currentX,currentY))){
                        currentY += 10 * PIXEL_CONSTANT;
                    }

                    ((Place) node).setX(currentX);
                    ((Place) node).setY(currentY);
                    occupied.add(new Point(currentX,currentY));

                    for (Object t : processNet.getAdjNodes(node)) {

                        //add parent location to each adjacent vertex
                        ((Transition)t).setParentLocation(new Point(currentX,currentY));
                        queue.offer(((Transition)t));
                    }


                }
                else if (node instanceof Transition) {

                    //add location to the node here based on the parent location
                    currentX = ((Transition) node).getParentLocation().getX();
                    currentY = ((Transition) node).getParentLocation().getY();
                    currentX +=10 * PIXEL_CONSTANT;

                    while(isPointInTheArray(occupied,new Point(currentX,currentY))){
                        currentY += 10 * PIXEL_CONSTANT;
                    }

                    ((Transition) node).setX(currentX);
                    ((Transition) node).setY(currentY);
                    occupied.add(new Point(currentX,currentY));



                    for (Object p : processNet.getAdjNodes(node)) {

                        //add parent location to each adjacent vertex
                        ((Place)p).setParentLocation(new Point(currentX,currentY));
                        queue.offer(((Place)p));
                    }
                }

            }
            visited.add(node);
        }

        return processNet;

    }


    public boolean isPointInTheArray(ArrayList<Point> list, Point point){


        for (Point p:list) {
            if(p.getX()==point.getX() && p.getY() == point.getY()){
               return true;
            }
        }
        return false;

    }

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



