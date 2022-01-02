package com.rychly.bp_frontend;

import com.rychly.bp_frontend.model.PetriNet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private IMyService service;

    @GetMapping("/test")
    public PetriNet greeting() {

        return this.service.test();


    /*
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        counter++;
        return new Greeting(counter, greeting+name);
    }*/
    }
}