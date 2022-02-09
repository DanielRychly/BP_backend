package com.rychly.bp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Arc {

    private String id;
    private String type;
    private String sourceId;
    private String destinationId;
    private int multiplicity;

}
