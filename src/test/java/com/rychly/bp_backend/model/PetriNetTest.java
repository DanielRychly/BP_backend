package com.rychly.bp_backend.model;

import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PetriNetTest {

    private final OkHttpClient client = new OkHttpClient();


    @Test
    @DisplayName("Unmarshall xml file of a petri net")
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



        assertAll( () -> assertEquals("t1",pn.getAllTransitions().get(0).getId()),
                () -> assertEquals("t2",pn.getAllTransitions().get(1).getId()),
                () -> assertEquals("t3",pn.getAllTransitions().get(2).getId()),
                () -> assertEquals("t4",pn.getAllTransitions().get(3).getId()),
                () -> assertEquals("t5",pn.getAllTransitions().get(4).getId()),
                () -> assertEquals("t6",pn.getAllTransitions().get(5).getId()),
                () -> assertEquals("t7",pn.getAllTransitions().get(6).getId()),
                () -> assertEquals("t8",pn.getAllTransitions().get(7).getId()));



        assertAll( () -> assertEquals("a1",pn.getAllArcs().get(0).getId()),
                () -> assertEquals("a2",pn.getAllArcs().get(1).getId()),
                () -> assertEquals("a3",pn.getAllArcs().get(2).getId()),
                () -> assertEquals("a4",pn.getAllArcs().get(3).getId()),
                () -> assertEquals("a5",pn.getAllArcs().get(4).getId()),
                () -> assertEquals("a6",pn.getAllArcs().get(5).getId()),
                () -> assertEquals("a7",pn.getAllArcs().get(6).getId()),
                () -> assertEquals("a8",pn.getAllArcs().get(7).getId()),
                () -> assertEquals("a9",pn.getAllArcs().get(8).getId()),
                () -> assertEquals("a10",pn.getAllArcs().get(9).getId()),
                () -> assertEquals("a11",pn.getAllArcs().get(10).getId()),
                () -> assertEquals("a12",pn.getAllArcs().get(11).getId()),
                () -> assertEquals("a13",pn.getAllArcs().get(12).getId()),
                () -> assertEquals("a14",pn.getAllArcs().get(13).getId()),
                () -> assertEquals("a15",pn.getAllArcs().get(14).getId()),
                () -> assertEquals("a16",pn.getAllArcs().get(15).getId()));


        assertAll( () -> assertEquals(1,pn.getAllArcs().get(0).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(1).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(2).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(3).getMultiplicity()),

                () -> assertEquals(3,pn.getAllArcs().get(4).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(5).getMultiplicity()),
                () -> assertEquals(2,pn.getAllArcs().get(6).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(7).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(8).getMultiplicity()),
                () -> assertEquals(2,pn.getAllArcs().get(9).getMultiplicity()),
                () -> assertEquals(3,pn.getAllArcs().get(10).getMultiplicity()),

                () -> assertEquals(1,pn.getAllArcs().get(11).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(12).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(13).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(14).getMultiplicity()),
                () -> assertEquals(1,pn.getAllArcs().get(14).getMultiplicity()));
    }


    @Test
    @DisplayName("Marshall xml file of a petri net")
    public void marshallXmlFileOfAPetriNet() throws JAXBException, IOException {


        String filename = "C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\test\\java\\com\\rychly\\bp_backend\\model\\test_model_1.xml";

        //unmarshall

        JAXBContext context = JAXBContext.newInstance(PetriNet.class);
        Unmarshaller u = context.createUnmarshaller();
        FileReader fr = new FileReader(filename);
        final PetriNet pn =  (PetriNet)u.unmarshal(fr);

        //marshall

        Marshaller mar= context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(pn, new File("C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\test\\java\\com\\rychly\\bp_backend\\model\\marshalled_test_model_1.xml"));


        //read file content

        Path fileName= Path.of("C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\test\\java\\com\\rychly\\bp_backend\\model\\marshalled_test_model_1.xml");

        // Now calling Files.readString() method to
        // read the file
        String fileContentAsString = Files.readString(fileName);

        //assert equals
        assertAll(()->assertEquals(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<document>\n" +
                "    <place>\n" +
                "        <id>p1</id>\n" +
                "        <x>60</x>\n" +
                "        <y>220</y>\n" +
                "        <tokens>1</tokens>\n" +
                "        <isStatic>false</isStatic>\n" +
                "    </place>\n" +
                "    <place>\n" +
                "        <id>p2</id>\n" +
                "        <x>220</x>\n" +
                "        <y>220</y>\n" +
                "        <tokens>0</tokens>\n" +
                "        <isStatic>false</isStatic>\n" +
                "    </place>\n" +
                "    <place>\n" +
                "        <id>p3</id>\n" +
                "        <x>420</x>\n" +
                "        <y>140</y>\n" +
                "        <tokens>0</tokens>\n" +
                "        <isStatic>false</isStatic>\n" +
                "    </place>\n" +
                "    <place>\n" +
                "        <id>p4</id>\n" +
                "        <x>420</x>\n" +
                "        <y>300</y>\n" +
                "        <tokens>0</tokens>\n" +
                "        <isStatic>false</isStatic>\n" +
                "    </place>\n" +
                "    <place>\n" +
                "        <id>p5</id>\n" +
                "        <x>660</x>\n" +
                "        <y>140</y>\n" +
                "        <tokens>0</tokens>\n" +
                "        <isStatic>false</isStatic>\n" +
                "    </place>\n" +
                "    <place>\n" +
                "        <id>p6</id>\n" +
                "        <x>820</x>\n" +
                "        <y>220</y>\n" +
                "        <tokens>0</tokens>\n" +
                "        <isStatic>false</isStatic>\n" +
                "    </place>\n" +
                "    <place>\n" +
                "        <id>p7</id>\n" +
                "        <x>1020</x>\n" +
                "        <y>220</y>\n" +
                "        <tokens>0</tokens>\n" +
                "        <isStatic>false</isStatic>\n" +
                "    </place>\n" +
                "    <transition>\n" +
                "        <id>t1</id>\n" +
                "        <x>140</x>\n" +
                "        <y>220</y>\n" +
                "        <label>t1</label>\n" +
                "    </transition>\n" +
                "    <transition>\n" +
                "        <id>t2</id>\n" +
                "        <x>300</x>\n" +
                "        <y>140</y>\n" +
                "        <label>t2</label>\n" +
                "    </transition>\n" +
                "    <transition>\n" +
                "        <id>t3</id>\n" +
                "        <x>300</x>\n" +
                "        <y>300</y>\n" +
                "        <label>t3</label>\n" +
                "    </transition>\n" +
                "    <transition>\n" +
                "        <id>t4</id>\n" +
                "        <x>540</x>\n" +
                "        <y>140</y>\n" +
                "        <label>t4</label>\n" +
                "    </transition>\n" +
                "    <transition>\n" +
                "        <id>t5</id>\n" +
                "        <x>540</x>\n" +
                "        <y>220</y>\n" +
                "        <label>t5</label>\n" +
                "    </transition>\n" +
                "    <transition>\n" +
                "        <id>t6</id>\n" +
                "        <x>740</x>\n" +
                "        <y>140</y>\n" +
                "        <label>t6</label>\n" +
                "    </transition>\n" +
                "    <transition>\n" +
                "        <id>t7</id>\n" +
                "        <x>740</x>\n" +
                "        <y>300</y>\n" +
                "        <label>t7</label>\n" +
                "    </transition>\n" +
                "    <transition>\n" +
                "        <id>t8</id>\n" +
                "        <x>900</x>\n" +
                "        <y>220</y>\n" +
                "        <label>t8</label>\n" +
                "    </transition>\n" +
                "    <arc>\n" +
                "        <id>a1</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>p1</sourceId>\n" +
                "        <destinationId>t1</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a2</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>t1</sourceId>\n" +
                "        <destinationId>p2</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a3</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>p2</sourceId>\n" +
                "        <destinationId>t2</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a4</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>p2</sourceId>\n" +
                "        <destinationId>t3</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a5</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>t2</sourceId>\n" +
                "        <destinationId>p3</destinationId>\n" +
                "        <multiplicity>3</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a6</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>t3</sourceId>\n" +
                "        <destinationId>p4</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a7</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>p3</sourceId>\n" +
                "        <destinationId>t5</destinationId>\n" +
                "        <multiplicity>2</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a8</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>p3</sourceId>\n" +
                "        <destinationId>t4</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a9</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>t4</sourceId>\n" +
                "        <destinationId>p5</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a10</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>t5</sourceId>\n" +
                "        <destinationId>p5</destinationId>\n" +
                "        <multiplicity>2</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a11</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>p5</sourceId>\n" +
                "        <destinationId>t6</destinationId>\n" +
                "        <multiplicity>3</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a12</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>t6</sourceId>\n" +
                "        <destinationId>p6</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a13</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>p4</sourceId>\n" +
                "        <destinationId>t7</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a14</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>t7</sourceId>\n" +
                "        <destinationId>p6</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a15</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>p6</sourceId>\n" +
                "        <destinationId>t8</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "    <arc>\n" +
                "        <id>a16</id>\n" +
                "        <type>regular</type>\n" +
                "        <sourceId>t8</sourceId>\n" +
                "        <destinationId>p7</destinationId>\n" +
                "        <multiplicity>1</multiplicity>\n" +
                "    </arc>\n" +
                "</document>\n").toString().replaceAll(" ",""),fileContentAsString.replaceAll(" ","")));

        //delete marshalled file
        File myObj = new File("C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\test\\java\\com\\rychly\\bp_backend\\model\\marshalled_test_model_1.xml");
        myObj.delete();



    }

    @Test
    @DisplayName("Simulate token flow")
    void simulateTokenFlow() throws JAXBException, IOException{

        String filename = "C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\test\\java\\com\\rychly\\bp_backend\\model\\test_model_1.xml";
        JAXBContext context = JAXBContext.newInstance(PetriNet.class);
        Unmarshaller u = context.createUnmarshaller();
        FileReader fr = new FileReader(filename);
        final PetriNet pn =  (PetriNet)u.unmarshal(fr);
        PetriNet processNet = pn.simulateTokenFlow(new ArrayList<>(Arrays.asList("t1","t2","t4","t4","t4","t6","t8")));
        assertEquals(1,pn.getAllPlaces().get(6).getTokens());



    }


    @Test
    @DisplayName("Place objects nicely")
    void placeObjectsNicely() throws JAXBException, IOException{

        String filename = "C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\test\\java\\com\\rychly\\bp_backend\\model\\test_model_for_placing_objects.xml";

        JAXBContext context = JAXBContext.newInstance(PetriNet.class);

        Unmarshaller u = context.createUnmarshaller();
        FileReader fr = new FileReader(filename);
        final PetriNet pn =  ((PetriNet)u.unmarshal(fr)).removeCoordinates().BFSTraversalToAddCoordinates();


        //assert if coordinates are correct
        assertAll(

                ()->assertEquals(140,pn.getAllPlaces().get(0).getX()),
                ()->assertEquals(280,pn.getAllPlaces().get(1).getX()),
                ()->assertEquals(420,pn.getAllPlaces().get(2).getX()),
                ()->assertEquals(420,pn.getAllPlaces().get(3).getX()),
                ()->assertEquals(560,pn.getAllPlaces().get(4).getX()),
                ()->assertEquals(560,pn.getAllPlaces().get(5).getX()),
                ()->assertEquals(700,pn.getAllPlaces().get(6).getX()),


                ()->assertEquals(70,pn.getAllPlaces().get(0).getY()),
                ()->assertEquals(70,pn.getAllPlaces().get(1).getY()),
                ()->assertEquals(70,pn.getAllPlaces().get(2).getY()),
                ()->assertEquals(140,pn.getAllPlaces().get(3).getY()),
                ()->assertEquals(70,pn.getAllPlaces().get(4).getY()),
                ()->assertEquals(140,pn.getAllPlaces().get(5).getY()),
                ()->assertEquals(140,pn.getAllPlaces().get(6).getY()),

                ()->assertEquals(210,pn.getAllTransitions().get(0).getX()),
                ()->assertEquals(350,pn.getAllTransitions().get(1).getX()),
                ()->assertEquals(490,pn.getAllTransitions().get(2).getX()),
                ()->assertEquals(490,pn.getAllTransitions().get(3).getX()),
                ()->assertEquals(630,pn.getAllTransitions().get(4).getX()),

                ()->assertEquals(70,pn.getAllTransitions().get(0).getY()),
                ()->assertEquals(70,pn.getAllTransitions().get(1).getY()),
                ()->assertEquals(70,pn.getAllTransitions().get(2).getY()),
                ()->assertEquals(140,pn.getAllTransitions().get(3).getY()),
                ()->assertEquals(140,pn.getAllTransitions().get(4).getY())



        );

    }

    @Test
    @DisplayName("Fire transition")
    void fireTransition() throws JAXBException, IOException{

        String filename = "C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\test\\java\\com\\rychly\\bp_backend\\model\\test_model_for_fire_transition.xml";

        JAXBContext context = JAXBContext.newInstance(PetriNet.class);

        Unmarshaller u = context.createUnmarshaller();
        FileReader fr = new FileReader(filename);
        PetriNet pn = (PetriNet)u.unmarshal(fr);

        //initialize process net
        PetriNet processNet = new PetriNet();
        processNet.setPlaces(new ArrayList<Place>());
        processNet.setTransitions(new ArrayList<Transition>());
        processNet.setArcs(new ArrayList<Arc>());
        processNet = processNet.addInitialPlacesToProcessNet(processNet);


        //fire every fired transition from the sequence
        processNet = pn.fireTransition("t1",processNet);


        final PetriNet pn2 =  pn;   //bc pn must be final

        assertAll(()->assertEquals(0,pn2.getAllPlaces().get(0).getTokens()),
                ()->assertEquals(1,pn2.getAllPlaces().get(1).getTokens()));



    }




}