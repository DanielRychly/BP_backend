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



