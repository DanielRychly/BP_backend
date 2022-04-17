package com.rychly.bp_backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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



    public void fireTransition(String transitionLabel){



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






    }



    public void simulateTokenFlow(List<String> firedTransitions){

        //changes the state of the petri net based on the sequence of fired transitions labels
        for(String transitionLabel : firedTransitions){

            //fire every fired transition from the sequence
            fireTransition(transitionLabel);
        }

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



