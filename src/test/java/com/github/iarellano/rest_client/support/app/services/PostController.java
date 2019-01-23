package com.github.iarellano.rest_client.support.app.services;

import com.github.iarellano.rest_client.support.app.model.Person;
import com.github.iarellano.rest_client.support.app.model.SimpleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

    @RequestMapping("/persons")
    public ResponseEntity postJson(@RequestBody Person person) {

        SimpleResponse simpleResponse = new SimpleResponse("Welcome back " + person.getFirstName() + " " + person.getLastName());
        return ResponseEntity.ok(simpleResponse);
    }
}
