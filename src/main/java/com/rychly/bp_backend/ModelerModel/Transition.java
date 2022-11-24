package com.rychly.bp_backend.ModelerModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transition {

    public String id;
    public String x;
    public String y;
    public String label;

    @Override
    public String toString() {
        return "Transition{" +
                "id='" + id + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
