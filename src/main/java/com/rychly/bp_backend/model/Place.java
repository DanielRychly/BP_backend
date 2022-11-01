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
@XmlRootElement(name = "place")
@XmlAccessorType(XmlAccessType.FIELD)
public class Place {

    private String id;
    private int x;
    private int y;
    private String label;
    private int tokens;
    private boolean isStatic;


    //in case of the process net, we keep the id of the source place of the token
    //which is represented by this place
    @XmlTransient
    public String idOfTheOriginPlace;

    @XmlTransient
    public boolean wasVisited = false;  //bc algo for nice placement


    @XmlTransient
    public Point parentLocation;


    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", label='" + label + '\'' +
                ", tokens=" + tokens +
                ", isStatic=" + isStatic +
                ", idOfTheOriginPlace=" + idOfTheOriginPlace +
                '}';
    }
}
