package com.rychly.bp_backend.ModelerModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    public String _id;
    public String _x;
    public String _y;
    public String _label;
    public String _marking;

    @Override
    public String toString() {
        return "Place{" +
                "_id='" + _id + '\'' +
                ", _x='" + _x + '\'' +
                ", _y='" + _y + '\'' +
                ", _label='" + _label + '\'' +
                ", _marking='" + _marking + '\'' +
                '}';
    }
}
