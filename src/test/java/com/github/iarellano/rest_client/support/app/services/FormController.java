package com.github.iarellano.rest_client.support.app.services;

import com.github.iarellano.rest_client.support.app.model.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FormController {

    @PostMapping("/form")
    public ResponseEntity postForm(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {
        Response response = new Response("Request received", firstName + " " + lastName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/form-multipart")
    public ResponseEntity postMultipart(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2,
            @RequestParam("filename") String filename,
            @RequestParam("filetype") String filetype) {
        Response[] response = new Response[]{
                new Response("File uploaded", file1.getOriginalFilename()),
                new Response("File uploaded", file2.getOriginalFilename())
        };
        return ResponseEntity.ok(response);
    }
}
