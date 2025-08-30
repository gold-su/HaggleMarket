package com.hagglemarket.marketweb.search.dto;

import java.time.LocalDateTime;

public interface UnifiedRow {
    Integer getId();
    String  getSource();     // "USED" | "AUCTION"
    String  getTitle();
    String  getSnippet();
    Integer getPrice();
    LocalDateTime getCreatedAt();
    Integer getHit();
    Double  getScore();
    String  getThumbnailUrl();
    Integer getThumbnailId();
}
