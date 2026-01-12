package com.sep.aiservice.gateway;

import com.sep.aiservice.dto.PagedResponseDTO;
import com.sep.aiservice.dto.RookiePageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "rookie-service",
        url = "${rookie-service.url}",
        path = "/api/rookie/users/books/pages"
)
public interface RookiePageGateway {

    @GetMapping
    PagedResponseDTO<RookiePageResponseDTO> listPages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String chapterId,
            @RequestParam(required = false) String pageType,
            @RequestParam(required = false) String isActived
    );
}