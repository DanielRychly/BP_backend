package com.rychly.bp_backend;

import com.rychly.bp_backend.model.PetriNet;
import com.rychly.bp_backend.model.Place;
import com.rychly.bp_backend.model.Transition;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;

public class PlayGround {


    public static void main(String[] args){

        try{
            PetriNet processNet = unmarshall("C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\main\\resources\\processNet.xml");

        }catch (Exception e){}



    }

    public static PetriNet unmarshall(String filename) throws JAXBException, IOException {

        //inspired by:
        //https://www.baeldung.com/jaxb
        //https://stackoverflow.com/questions/16364547/how-to-parse-xml-to-java-object
        //https://stackoverflow.com/questions/51916221/javax-xml-bind-jaxbexception-implementation-of-jaxb-api-has-not-been-found-on-mo


        try{
            JAXBContext context = JAXBContext.newInstance(PetriNet.class);
            Unmarshaller u = context.createUnmarshaller();
            FileReader fr = new FileReader(filename);
            PetriNet pn = (PetriNet) u.unmarshal(fr);
            return pn;

        }catch(Exception e){
            e.printStackTrace();
            return null;

        }

    }

}
