package com.xstocks.uc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class OssController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.oss.upload-url}")
    private String uploadUrl;


    @Value("${app.oss.access-url}")
    private String accessUrl;

    @PostMapping("/a/oss/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(uploadUrl, requestEntity, String.class);
    }

    @GetMapping("/a/oss/{imdId}")
    public ResponseEntity<byte[]> getImage(@PathVariable("imdId") String imdId) {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(accessUrl + imdId, byte[].class);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(response.getBody());
    }

}
