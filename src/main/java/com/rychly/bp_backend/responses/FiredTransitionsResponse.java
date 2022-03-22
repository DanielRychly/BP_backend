package com.rychly.bp_backend.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.util.ArrayList;


// json ignore -> https://stackoverflow.com/questions/45915851/could-not-write-json-no-serializer-found-for-class-org-json-jsonobject-and-no-p
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FiredTransitionsResponse {
    private String firedTransitions;

}
