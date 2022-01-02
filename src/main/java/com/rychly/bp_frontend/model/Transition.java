package com.rychly.bp_frontend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Transition {

    private String id;
    private int x;
    private int y;
    private String label;

}
