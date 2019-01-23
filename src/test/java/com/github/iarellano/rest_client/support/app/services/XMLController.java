package com.github.iarellano.rest_client.support.app.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XMLController {

    @GetMapping("/xml")
    public ResponseEntity getXml() {
        String xml = "<test:parent version=\"1.0\" xmlns:test=\"http://localhost/root\">\n" +
                "    <child:innerChild xmlns:child=\"http://localhost/root/child\">\n" +
                "        <grandChild>I am grandson of my parent's parent</grandChild>\n" +
                "    </child:innerChild>\n" +
                "</test:parent>";
        return ResponseEntity.ok()
                .header("Content-Type", "application/xml")
                .header("Content-Length", Integer.toString(xml.length()))
                .body(xml);
    }
}
