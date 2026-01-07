package com.sep.arservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class HttpBytesFetcher {

    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] get(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url is blank");
        }

        URI uri = URI.create(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes("image/png,image/jpeg,image/*,*/*"));

        ResponseEntity<byte[]> res = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                byte[].class
        );

        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null || res.getBody().length == 0) {
            throw new IllegalStateException("Cannot fetch bytes: " + url + ", status=" + res.getStatusCode());
        }
        return res.getBody();
    }
}

