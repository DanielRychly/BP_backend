package com.rychly.bp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class PetriNet {

    private ArrayList<Place> Places = new ArrayList<Place>();
    private ArrayList<Transition> Transitions = new ArrayList<Transition>();
    private ArrayList<Arc> Arcs = new ArrayList<Arc>();


}
