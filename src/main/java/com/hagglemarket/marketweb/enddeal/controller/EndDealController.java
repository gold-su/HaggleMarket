package com.hagglemarket.marketweb.enddeal.controller;

import com.hagglemarket.marketweb.enddeal.service.EndDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enddeal")
@RequiredArgsConstructor
public class EndDealController {

    private final EndDealService endDealService;

    @PostMapping
    public ResponseEntity<Void> recordEndDeal(
            @RequestParam String type,          // used or auction
            @RequestParam int itemId,
            @RequestParam int userNo,
            @RequestParam String title,
            @RequestParam(required = false) Integer finalPrice,
            @RequestParam(required = false) String reason,
            @RequestParam(defaultValue = "ENDED") String status
    ) {
        endDealService.recordEndDeal(type, itemId, userNo, title, finalPrice, reason, status);
        return ResponseEntity.ok().build();
    }
}
