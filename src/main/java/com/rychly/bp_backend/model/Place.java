package com.rychly.bp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
