package com.hagglemarket.marketweb.search.service;

import com.hagglemarket.marketweb.search.dto.UnifiedRow;
import com.hagglemarket.marketweb.search.repository.UnifiedSearchRepository;
import com.hagglemarket.marketweb.search.dto.SearchItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnifiedSearchService {
    private final UnifiedSearchRepository repo;

    public Page<SearchItemDto> search(String q, Integer page, Integer size) {
        String query = q == null ? "" : q.trim();
        int p = page == null ? 0 : Math.max(page, 0);
        int s = size == null ? 20 : Math.max(size, 1);
        Pageable pageable = PageRequest.of(p, s);

        boolean tryFullText = query.length() >= 2;
        Page<UnifiedRow> raw = tryFullText ? repo.unifiedSearch(query, true, pageable) : Page.empty(pageable);
        if (raw.isEmpty()) raw = repo.unifiedSearch(query, false, pageable);

        return raw.map(r -> SearchItemDto.builder()
                .id(r.getId()).source(r.getSource()).title(r.getTitle())
                .snippet(r.getSnippet()).price(r.getPrice())
                .createdAt(r.getCreatedAt()).hit(r.getHit()).score(r.getScore())
                .thumbnailUrl(r.getThumbnailUrl())    // USED
                .thumbnailId(r.getThumbnailId())      // AUCTION
                .build());
    }
}
