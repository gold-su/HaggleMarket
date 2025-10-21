package com.hagglemarket.marketweb.shop.service;

import com.hagglemarket.marketweb.shop.dto.PageResponse;
import com.hagglemarket.marketweb.shop.dto.PostCardDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    @PersistenceContext
    private EntityManager em;

    public PageResponse<PostCardDto> findBySeller(int userNo, int page, int size, String sort, String type) {
        String orderBy = switch (sort == null ? "latest" : sort) {
            case "popular"   -> "like_count DESC, created_at DESC";
            case "priceAsc"  -> "cost ASC, created_at DESC";
            case "priceDesc" -> "cost DESC, created_at DESC";
            default          -> "created_at DESC";
        };
        int offset = page * size;

        /* ===================== 경매 ===================== */
        if ("auction".equalsIgnoreCase(type)) {
            long total = ((Number) em.createNativeQuery(
                            // ✅ DELETED 제외
                            "SELECT COUNT(*) FROM auction_posts ap WHERE ap.user_no = ?1 AND ap.status <> 'DELETED'")
                    .setParameter(1, userNo)
                    .getSingleResult()).longValue();

            String sql =
                    "SELECT 'auction' AS mode, ap.auction_id AS id, ap.title, ap.current_cost AS cost, " +
                            "       ap.like_count, " +
                            "       (SELECT api.image_id FROM auction_post_images api " +
                            "         WHERE api.auction_id = ap.auction_id ORDER BY api.sort_order ASC LIMIT 1) AS thumbnailUrl, " +
                            "       ap.created_at, ap.end_time AS endsAt, ap.status, ap.hit " +
                            "FROM auction_posts ap " +
                            "WHERE ap.user_no = ?1 AND ap.status <> 'DELETED' " + // ✅ 추가
                            "ORDER BY " + orderBy.replace("cost", "current_cost");

            @SuppressWarnings("unchecked")
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, userNo)
                    .setFirstResult(offset)
                    .setMaxResults(size)
                    .getResultList();

            List<PostCardDto> content = mapAuctionRows(rows);
            int totalPages = (int) Math.ceil((double) total / size);
            return PageResponse.<PostCardDto>builder()
                    .content(content).page(page).size(size).totalElements(total).totalPages(totalPages).build();
        }

        /* ===================== 중고 ===================== */
        long total = ((Number) em.createNativeQuery(
                        // ✅ DELETED 제외
                        "SELECT COUNT(*) FROM posts p WHERE p.user_no = ?1 AND p.status <> 'DELETED'")
                .setParameter(1, userNo)
                .getSingleResult()).longValue();

        String sql =
                "SELECT 'used' AS mode, p.post_id AS id, p.title, p.cost, p.like_count, " +
                        "       (SELECT pi.image_url FROM post_images pi " +
                        "         WHERE pi.post_id = p.post_id ORDER BY pi.sort_order ASC LIMIT 1) AS thumbnailUrl, " +
                        "       p.created_at, NULL AS endsAt, p.status, p.hit " +
                        "FROM posts p " +
                        "WHERE p.user_no = ?1 AND p.status <> 'DELETED' " + // ✅ 추가
                        "ORDER BY " + orderBy;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, userNo)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        List<PostCardDto> content = mapUsedRows(rows);
        int totalPages = (int) Math.ceil((double) total / size);
        return PageResponse.<PostCardDto>builder()
                .content(content).page(page).size(size).totalElements(total).totalPages(totalPages).build();
    }

    private List<PostCardDto> mapUsedRows(List<Object[]> rows) {
        List<PostCardDto> list = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            list.add(PostCardDto.builder()
                    .mode((String) r[0])
                    .id(((Number) r[1]).intValue())
                    .title((String) r[2])
                    .cost(((Number) r[3]).intValue())
                    .likeCount(r[4] == null ? 0 : ((Number) r[4]).intValue())
                    .thumbnailUrl((String) r[5])
                    .createdAt(toLdt(r[6]))
                    .endsAt(null)
                    .status((String) r[8])
                    .hit(r[9] == null ? null : ((Number) r[9]).intValue())
                    .build());
        }
        return list;
    }

    private List<PostCardDto> mapAuctionRows(List<Object[]> rows) {
        List<PostCardDto> list = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            list.add(PostCardDto.builder()
                    .mode((String) r[0])
                    .id(((Number) r[1]).intValue())
                    .title((String) r[2])
                    .cost(((Number) r[3]).intValue())
                    .likeCount(r[4] == null ? 0 : ((Number) r[4]).intValue())
                    .thumbnailUrl(r[5] == null ? null : String.valueOf(r[5])) // image_id는 숫자형 문자열로 변환
                    .createdAt(toLdt(r[6]))
                    .endsAt(toLdt(r[7]))
                    .status((String) r[8])
                    .hit(r[9] == null ? null : ((Number) r[9]).intValue())
                    .build());
        }
        return list;
    }

    private java.time.LocalDateTime toLdt(Object ts) {
        if (ts == null) return null;
        if (ts instanceof Timestamp t) return t.toLocalDateTime();
        return null;
    }
}
