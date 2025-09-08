package com.hagglemarket.marketweb.search.repository;

import com.hagglemarket.marketweb.post.entity.Post;
import com.hagglemarket.marketweb.search.dto.UnifiedRow;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UnifiedSearchRepository extends JpaRepository<Post, Integer> {
    @Query(
            value = """
        SELECT u.id, u.source, u.title, u.snippet, u.price, u.createdAt, u.hit, u.score,
               u.thumbnailUrl, u.thumbnailId
        FROM (
          /* USED: post_images는 URL 저장형 */
          SELECT
            p.post_id    AS id,
            'USED'       AS source,
            p.title      AS title,
            p.content    AS snippet,
            p.cost       AS price,
            p.created_at AS createdAt,
            p.hit        AS hit,
            MATCH(p.title, p.content) AGAINST (:q IN BOOLEAN MODE) AS score,
            (SELECT img.image_url
               FROM post_images img
              WHERE img.post_id = p.post_id
              ORDER BY img.sort_order, img.image_no
              LIMIT 1) AS thumbnailUrl,
            NULL AS thumbnailId
          FROM posts p
          WHERE (
            (:useFullText = TRUE  AND MATCH(p.title, p.content) AGAINST (:q IN BOOLEAN MODE))
            OR (:useFullText = FALSE AND (p.title LIKE CONCAT('%', :q, '%') OR p.content LIKE CONCAT('%', :q, '%')))
          )

          UNION ALL

          /* AUCTION: auction_post_images는 BLOB 저장형 */
          SELECT
            a.auction_id AS id,
            'AUCTION'    AS source,
            a.title      AS title,
            a.content    AS snippet,
            a.start_cost AS price,
            a.created_at AS createdAt,
            a.hit        AS hit,
            MATCH(a.title, a.content) AGAINST (:q IN BOOLEAN MODE) AS score,
            NULL AS thumbnailUrl,
            (SELECT ai.image_id
               FROM auction_post_images ai
              WHERE ai.auction_id = a.auction_id
              ORDER BY ai.sort_order, ai.image_id
              LIMIT 1) AS thumbnailId
          FROM auction_posts a
          WHERE (
            (:useFullText = TRUE  AND MATCH(a.title, a.content) AGAINST (:q IN BOOLEAN MODE))
            OR (:useFullText = FALSE AND (a.title LIKE CONCAT('%', :q, '%') OR a.content LIKE CONCAT('%', :q, '%')))
          )
        ) u
        ORDER BY u.score DESC, u.createdAt DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM (
          SELECT p.post_id AS id
          FROM posts p
          WHERE (
            (:useFullText = TRUE  AND MATCH(p.title, p.content) AGAINST (:q IN BOOLEAN MODE))
            OR (:useFullText = FALSE AND (p.title LIKE CONCAT('%', :q, '%') OR p.content LIKE CONCAT('%', :q, '%')))
          )
          UNION ALL
          SELECT a.auction_id AS id
          FROM auction_posts a
          WHERE (
            (:useFullText = TRUE  AND MATCH(a.title, a.content) AGAINST (:q IN BOOLEAN MODE))
            OR (:useFullText = FALSE AND (a.title LIKE CONCAT('%', :q, '%') OR a.content LIKE CONCAT('%', :q, '%')))
          )
        ) c
        """,
            nativeQuery = true
    )
    Page<UnifiedRow> unifiedSearch(@Param("q") String q,
                                   @Param("useFullText") boolean useFullText,
                                   Pageable pageable);
}
