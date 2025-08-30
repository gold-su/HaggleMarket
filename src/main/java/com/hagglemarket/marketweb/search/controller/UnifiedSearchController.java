package com.hagglemarket.marketweb.search.controller;

import com.hagglemarket.marketweb.search.dto.SearchItemDto;
import com.hagglemarket.marketweb.search.service.UnifiedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UnifiedSearchController {
    private final UnifiedSearchService service;


    @GetMapping("/search")
    public Page<SearchItemDto> search(@RequestParam(required = false) String q,
                                      @RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "20") Integer size) {
        return service.search(q, page, size);
    }
}
