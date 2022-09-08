package com.rychly.bp_backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@Setter
@XmlRootElement(name = "document")
public class PetriNet {

    /*
    @XmlElement(name = "place")
    public List<Place> places = null;

    @XmlElement(name = "transition")
    public List<Transition> transitions = null;

    @XmlElement(name = "arc")
    public List<Arc> arcs = null;

    public int processNetPlacesCurrentId = 0;
    public int processNetArcsCurrentId = 0;
    public int processNetPlacesCurrentX = 0;
    public int processNetPlacesCurrentY = 0;
    */

    @XmlElement(name = "place")
    private List<Place> places = null;

    @XmlElement(name = "transition")
    private List<Transition> transitions = null;

    @XmlElement(name = "arc")
    private List<Arc> arcs = null;

    @XmlTransient
    public int processNetTransitionsCurrentId = 1;
    @XmlTransient
    public int processNetPlacesCurrentId = 1;
    @XmlTransient
    public int processNetArcsCurrentId = 1;
    @XmlTransient
    public int processNetCurrentX = 20;
    @XmlTransient
    public int processNetCurrentY = 20;


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


            /*
            //added for creating places in process net  ****************************************************************
            for(int i=0;i<multiplicity;i++){
                Place p = new Place();
                p.setId("p" + processNetPlacesCurrentId);
                p.setLabel("p" + processNetPlacesCurrentId);
                p.setX(processNetPlacesCurrentX);
                p.setY(processNetPlacesCurrentY);

                processNet.places.add(p);


                processNetPlacesCurrentId++;
                processNetPlacesCurrentX += 20;
                processNetPlacesCurrentY += 20;

                Arc processNetArc = new Arc();
                processNetArc.setId("a"+processNetArcsCurrentId);
                processNetArc.setMultiplicity(1);
                processNetArc.setDestinationId(p.getId());
                processNetArc.setSourceId(firedTransition.getId());
                processNet.arcs.add(a);

                processNetArcsCurrentId++;




            }
            */


        }

        //now i have fired transition, input arcs, output arcs
        //so I can add stuff to the process net


        //todo it is not ok from here *******************************
        //delete from here to next todo

        //add transition
        Transition t = new Transition();
        t.setId("t"+processNetTransitionsCurrentId);
        t.setLabel(firedTransition.getLabel());
        t.setX(processNetCurrentX);
        t.setY(processNetCurrentY);
        processNet.transitions.add(t);

        //update current values
        processNetTransitionsCurrentId++;
        processNetCurrentX +=40;
        processNetCurrentY +=40;

        //add places
        //for each output arc add multiplicity times new place
        for(Arc arc:outputArcs){
            for(int i=0;i<arc.getMultiplicity();i++){
                Place p = new Place();
                p.setId("p"+processNetPlacesCurrentId);
                //p.setLabel(arc.getDestinationId());
                p.setLabel(getPlaceLabelById(arc.getDestinationId()));
                p.setX(processNetCurrentX);
                p.setY(processNetCurrentY);

                processNet.places.add(p);

                //add also arc between fired transition and new place
                Arc a = new Arc();
                a.setId("a"+processNetArcsCurrentId);
                a.setSourceId(t.getId());
                a.setDestinationId(p.getId());
                a.setMultiplicity(1);
                processNet.arcs.add(a);

                //update vlaues
                processNetPlacesCurrentId++;
                processNetCurrentX+=40;
                processNetCurrentY+=40;
                processNetArcsCurrentId++;
            }
        }

        //for each input arc add input arc to the process net from place with
        for(Arc arc:inputArcs){



        }




        //todo I still need to add arcs from places to transitions in Process net



        //todo it is not ok to here ********************************

        return processNet;


    }



    public PetriNet simulateTokenFlow(List<String> firedTransitions){

        //returns process net calculated by simulating token flow

        PetriNet processNet = new PetriNet();
        processNet.transitions = new ArrayList<Transition>();
        processNet.places = new ArrayList<Place>();
        processNet.arcs = new ArrayList<Arc>();

        //changes the state of the petri net based on the sequence of fired transitions labels
        for(String transitionLabel : firedTransitions){

            //fire every fired transition from the sequence
            processNet = fireTransition(transitionLabel,processNet);
        }

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
}



