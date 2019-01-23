package com.github.iarellano.rest_client.support.app.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
public class RawFilePostController {

    @PostMapping("/raw-file")
    public ResponseEntity rawFileUpload() throws IOException {

//        (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//                        .getRequest());
//        InputStream is = curRequest.getInputStream();
//        byte[] buffer = new byte[1024];
//        int read = 0;
//
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        while ((read = is.read(buffer)) != -1) {
//            os.write(buffer, 0, read);
//        }
//
//        String content = new String(os.toString());

//        return ResponseEntity.ok(new Response(content));
        return ResponseEntity.ok("OK");
    }
}
