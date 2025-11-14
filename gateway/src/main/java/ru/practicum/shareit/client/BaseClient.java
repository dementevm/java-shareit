package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    protected BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return rest.getForEntity(path, Object.class);
    }

    protected ResponseEntity<Object> get(String path, long userId) {
        return exchange(path, HttpMethod.GET, userId, null, null);
    }

    protected ResponseEntity<Object> get(String path, long userId, Map<String, Object> params) {
        return exchange(path, HttpMethod.GET, userId, null, params);
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return rest.postForEntity(path, body, Object.class);
    }

    protected ResponseEntity<Object> post(String path, long userId, Object body) {
        return exchange(path, HttpMethod.POST, userId, body, null);
    }

    protected ResponseEntity<Object> patch(String path, Object body) {
        return exchange(path, HttpMethod.PATCH, null, body, null);
    }

    protected ResponseEntity<Object> patch(String path, long userId, Object body) {
        return exchange(path, HttpMethod.PATCH, userId, body, null);
    }

    protected ResponseEntity<Object> patch(String path, long userId, Map<String, Object> params, Object body) {
        return exchange(path, HttpMethod.PATCH, userId, body, params);
    }

    protected ResponseEntity<Object> delete(String path, long userId) {
        return exchange(path, HttpMethod.DELETE, userId, null, null);
    }

    private ResponseEntity<Object> exchange(
            String path,
            HttpMethod method,
            Long userId,
            Object body,
            Map<String, Object> params
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            if (params != null && !params.isEmpty()) {
                return rest.exchange(path, method, requestEntity, Object.class, params);
            } else {
                return rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (RestClientResponseException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }
    }
}
