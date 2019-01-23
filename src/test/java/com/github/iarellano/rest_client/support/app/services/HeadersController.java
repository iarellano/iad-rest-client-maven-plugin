package com.github.iarellano.rest_client.support.app.services;

import com.github.iarellano.rest_client.support.app.model.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HeadersController {

    @RequestMapping("/headers")
    public ResponseEntity echoHeaders(@RequestHeader HttpHeaders httpHeaders) {

        List<Header> headers = new ArrayList<>();
        for (String header : httpHeaders.keySet()) {
            int size = httpHeaders.get(header).size();
            headers.add(new Header(header, httpHeaders.get(header).toArray(new String[size])));
        }
        return ResponseEntity.ok(headers);

    }
}
