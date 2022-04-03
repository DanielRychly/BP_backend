package com.rychly.bp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "arc")
@XmlAccessorType(XmlAccessType.FIELD)
public class Arc {

    private String id;
    private String type;
    private String sourceId;
    private String destinationId;
    private int multiplicity;

    @Override
    public String toString() {
        return "Arc{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", destinationId='" + destinationId + '\'' +
                ", multiplicity=" + multiplicity +
                '}';
    }
}

