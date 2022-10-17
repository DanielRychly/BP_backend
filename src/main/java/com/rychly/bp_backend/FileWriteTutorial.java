package com.rychly.bp_backend;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriteTutorial {

    public static void main(String[] args){

        //new idea
        try {


            FileWriter fw = new FileWriter("auxiliray_log.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Spain");
            bw.newLine();
            bw.close();
            FileWriter fw2 = new FileWriter("auxiliray_log.txt", true);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            bw2.write("Spain");
            bw2.newLine();
            bw2.close();

            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }
}
