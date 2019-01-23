package com.github.iarellano.rest_client.support.app.services;

import com.github.iarellano.rest_client.support.app.model.SimpleResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GetController {

    @RequestMapping("/greeting")
    public SimpleResponse greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new SimpleResponse("Hello, " + name);
    }

    @RequestMapping("/greeting/{name}")
    public SimpleResponse greetingPerson(@PathVariable("name") String name) {
        return new SimpleResponse("Hello, " + name);
    }

    @RequestMapping("/protected-greeting")
    public ResponseEntity<SimpleResponse> protectedGreeting(@RequestHeader("Authorization") String auth) {
        byte[] encodeCredentials = Base64.decodeBase64(auth.substring("Basic ".length()));
        String credentials = new String(encodeCredentials);
        String username = credentials.split(":")[0];
        String password = credentials.split(":")[1];

        if ("john-connor".equals(username) && "terminator".equals(password)) {
            SimpleResponse userSimpleResponse = new SimpleResponse("Hi " + username);
            return new ResponseEntity<SimpleResponse>(userSimpleResponse, HttpStatus.OK);
        } else {
            SimpleResponse userSimpleResponse = new SimpleResponse("I do not know any \"" + username + "\"");
            return new ResponseEntity<SimpleResponse>(userSimpleResponse, HttpStatus.UNAUTHORIZED);
        }
    }


}
