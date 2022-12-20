package com.rychly.bp_backend.responses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// json ignore -> https://stackoverflow.com/questions/45915851/could-not-write-json-no-serializer-found-for-class-org-json-jsonobject-and-no-p

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class MyResponse {
    private String response;

}
