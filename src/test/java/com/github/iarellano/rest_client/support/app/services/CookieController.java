package com.github.iarellano.rest_client.support.app.services;

import com.github.iarellano.rest_client.support.app.model.SimpleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@RestController
public class CookieController {

    @GetMapping("/redirect-with-cookie")
    public void redirectWithCookie(HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("uuid", UUID.randomUUID().toString());
        cookie.setDomain("localhost.local");
        cookie.setPath("/");
        response.addCookie(cookie);

        cookie = new Cookie("another-uuid", UUID.randomUUID().toString());
        cookie.setDomain("localhost.local");
        cookie.setPath("/");
        response.addCookie(cookie);
        response.sendRedirect("/redirected-with-cookie");
        response.setStatus(302);
    }

    @GetMapping("/redirected-with-cookie")
    public ResponseEntity redirectLandingReturnCookie(@CookieValue("uuid") String cookieId) {
        return ResponseEntity.ok(new SimpleResponse(cookieId));
    }
}
