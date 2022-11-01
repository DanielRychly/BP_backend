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

        response.setContentType("text/csv; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=processNet.xml" );
        response.setHeader("filename", "processNet.xml");
        Resource resource = resource = new FileSystemResource("C:\\Users\\rychl\\BP_stuff\\BP_backend\\src\\main\\resources\\processNet.xml");
        return resource;
    }
}