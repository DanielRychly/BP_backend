package com.rychly.bp_backend.ModelerModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Model {

    public ArrayList<Transition> _transitions;
    public ArrayList<Place> _places;
    public ArrayList<Arc> _arcs;

    public String _id;
    public String _identifier;
    public String _caseName;


    @Override
    public String toString() {
        return "Model{" +
                "_transitions=" + _transitions +
                ", _places=" + _places +
                ", _arcs=" + _arcs +
                ", _id='" + _id + '\'' +
                ", _identifier='" + _identifier + '\'' +
                ", _caseName='" + _caseName + '\'' +
                '}';
    }
}
