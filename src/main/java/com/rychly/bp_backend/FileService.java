package com.rychly.bp_backend;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class FileService {



    public Resource getFile(HttpServletResponse response) {

        return getResource(response);

    }

    private Resource getResource(HttpServletResponse response) {

        //response.setContentType("text/csv; charset=utf-8");
        //response.setHeader("Content-Disposition", "attachment; filename=processNet.xml" );
        //response.setHeader("filename", "processNet.xml");
        //response.setContentType("application/zip");
        //response.setHeader("Content-Disposition", "attachment; filename=compressed.zip" );
        //response.setHeader("filename", "compressed.zip");


        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=compressed.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        //Resource resource = resource = new FileSystemResource("C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\main\\resources\\processNet.xml");    //previous version - only one case
        Resource resource = resource = new FileSystemResource("C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\main\\resources\\compressed.zip");  //current version - downloads zip archive of process nets for all cases
        return resource;
    }
}