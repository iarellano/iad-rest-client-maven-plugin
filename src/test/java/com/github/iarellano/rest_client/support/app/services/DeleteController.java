package com.github.iarellano.rest_client.support.app.services;

import com.github.iarellano.rest_client.support.app.model.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DeleteController {

    @DeleteMapping("/delete/{email}")
    public ResponseEntity deleteItem(@PathVariable("email") String email) {
        Response response = new Response(String.format("Email %s deleted", email), email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteItem() {
        return ResponseEntity.noContent().build();
    }
}
