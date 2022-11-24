package com.rychly.bp_backend.ModelerModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Arc {

    public String id;
    public String source;
    public String target;
    public String vaha;

    @Override
    public String toString() {
        return "Arc{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", vaha='" + vaha + '\'' +
                '}';
    }
}
