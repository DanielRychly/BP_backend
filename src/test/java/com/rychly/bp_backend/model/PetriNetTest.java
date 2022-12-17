package com.rychly.bp_backend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PetriNetTest {



    @Test
    @DisplayName("Unmarshall Xml File Of A Petri Net")
    public void unmarshallXmlFileOfAPetriNet() throws JAXBException, IOException {

        //inspired by:
        //https://www.baeldung.com/jaxb
        //https://stackoverflow.com/questions/16364547/how-to-parse-xml-to-java-object
        //https://stackoverflow.com/questions/51916221/javax-xml-bind-jaxbexception-implementation-of-jaxb-api-has-not-been-found-on-mo

        String filename = "C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\test\\java\\com\\rychly\\bp_backend\\model\\test_model_1.xml";



            JAXBContext context = JAXBContext.newInstance(PetriNet.class);
            Unmarshaller u = context.createUnmarshaller();
            FileReader fr = new FileReader(filename);
            final PetriNet pn =  (PetriNet)u.unmarshal(fr);




        assertAll(()-> assertEquals("p1",(pn.getAllPlaces()).get(0).getId()),
                ()-> assertEquals("p2",(pn.getAllPlaces()).get(1).getId()),
                ()-> assertEquals("p3",(pn.getAllPlaces()).get(2).getId()),
                ()-> assertEquals("p4",(pn.getAllPlaces()).get(3).getId()),
                ()-> assertEquals("p5",(pn.getAllPlaces()).get(4).getId()),
                ()-> assertEquals("p6",(pn.getAllPlaces()).get(5).getId()),
                ()-> assertEquals("p7",(pn.getAllPlaces()).get(6).getId()));

        //todo tu nasledne su prehodene hodnoty expected a actual, funguje to, ale ma to byt opacne

        assertAll( () -> assertEquals(pn.getAllTransitions().get(0).getId(),"t1"),
                () -> assertEquals(pn.getAllTransitions().get(1).getId(),"t2"),
                () -> assertEquals(pn.getAllTransitions().get(2).getId(),"t3"),
                () -> assertEquals(pn.getAllTransitions().get(3).getId(),"t4"),
                () -> assertEquals(pn.getAllTransitions().get(4).getId(),"t5"),
                () -> assertEquals(pn.getAllTransitions().get(5).getId(),"t6"),
                () -> assertEquals(pn.getAllTransitions().get(6).getId(),"t7"),
                () -> assertEquals(pn.getAllTransitions().get(7).getId(),"t8"));

        assertAll( () -> assertEquals(pn.getAllArcs().get(0).getId(),"a1"),
                () -> assertEquals(pn.getAllArcs().get(1).getId(),"a2"),
                () -> assertEquals(pn.getAllArcs().get(2).getId(),"a3"),
                () -> assertEquals(pn.getAllArcs().get(3).getId(),"a4"),
                () -> assertEquals(pn.getAllArcs().get(4).getId(),"a5"),
                () -> assertEquals(pn.getAllArcs().get(5).getId(),"a6"),
                () -> assertEquals(pn.getAllArcs().get(6).getId(),"a7"),
                () -> assertEquals(pn.getAllArcs().get(7).getId(),"a8"),
                () -> assertEquals(pn.getAllArcs().get(8).getId(),"a9"),
                () -> assertEquals(pn.getAllArcs().get(9).getId(),"a10"),
                () -> assertEquals(pn.getAllArcs().get(10).getId(),"a11"),
                () -> assertEquals(pn.getAllArcs().get(11).getId(),"a12"),
                () -> assertEquals(pn.getAllArcs().get(12).getId(),"a13"),
                () -> assertEquals(pn.getAllArcs().get(13).getId(),"a14"),
                () -> assertEquals(pn.getAllArcs().get(14).getId(),"a15"),
                () -> assertEquals(pn.getAllArcs().get(15).getId(),"a16"));

        assertAll( () -> assertEquals(pn.getAllArcs().get(0).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(1).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(2).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(3).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(4).getMultiplicity(),3),
                () -> assertEquals(pn.getAllArcs().get(5).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(6).getMultiplicity(),2),
                () -> assertEquals(pn.getAllArcs().get(7).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(8).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(9).getMultiplicity(),2),
                () -> assertEquals(pn.getAllArcs().get(10).getMultiplicity(),3),
                () -> assertEquals(pn.getAllArcs().get(11).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(12).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(13).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(14).getMultiplicity(),1),
                () -> assertEquals(pn.getAllArcs().get(14).getMultiplicity(),1));
    }

    @Test
    @DisplayName("simulate token flow")
    void simulateTokenFlow() throws JAXBException, IOException{

        String filename = "C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\test\\java\\com\\rychly\\bp_backend\\model\\test_model_1.xml";
        JAXBContext context = JAXBContext.newInstance(PetriNet.class);
        Unmarshaller u = context.createUnmarshaller();
        FileReader fr = new FileReader(filename);
        final PetriNet pn =  (PetriNet)u.unmarshal(fr);
        PetriNet processNet = pn.simulateTokenFlow(new ArrayList<>(Arrays.asList("t1","t2","t4","t4","t4","t6","t8")));
        assertEquals(1,pn.getAllPlaces().get(6).getTokens());



    }


}