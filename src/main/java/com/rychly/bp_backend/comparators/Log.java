package com.rychly.bp_backend.comparators;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Log {
    private String year2TS;
    private String month2TS;
    private String day2TS;

    private String hour2TS;
    private String minute2TS;
    private String second2TS;

    private String case_id;
    private String fired_transition_id;

}
