package com.rychly.bp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@Setter
@XmlRootElement(name = "transition")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transition {

    private String id;
    private int x;
    private int y;
    private String label;

    @XmlTransient
    public String idOfTheOriginalTransition;

    @XmlTransient
    public boolean wasVisited = false;  //bc algo for nice placement


    @Override
    public String toString() {
        return "Transition{" +
                "id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", label='" + label + '\'' +
                '}';
    }
}
