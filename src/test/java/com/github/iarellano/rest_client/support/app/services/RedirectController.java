package com.github.iarellano.rest_client.support.app.services;

import com.github.iarellano.rest_client.support.app.model.SimpleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RedirectController {

    @GetMapping("/redirect-me")
    public RedirectView redirectMe() {
        return new RedirectView("/redirect-landing");
    }

    @GetMapping("/redirect-landing")
    public ResponseEntity redirectLanding() {
        return ResponseEntity.ok(new SimpleResponse("You have been redirected here!"));
    }
}
