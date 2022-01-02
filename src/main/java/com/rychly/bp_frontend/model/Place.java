package com.rychly.bp_frontend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class Place {

    private String id;
    private int x;
    private int y;
    private String label;
    private int tokens;
    private boolean isStatic;

}
