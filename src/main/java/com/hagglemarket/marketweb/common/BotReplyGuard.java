
package com.hagglemarket.marketweb.common;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class BotReplyGuard {

    private final ConcurrentHashMap<String, Long> locks = new ConcurrentHashMap<>();

    // key에 대해 ttlSec 동안 1회만 true를 반환
    public boolean tryLock(String key, long ttlSec) {
        long now = System.currentTimeMillis();
        long expireAt = now + ttlSec * 1000L;

        // 기존 락이 있고 아직 유효하면 false
        Long old = locks.putIfAbsent(key, expireAt);
        if (old != null) {
            if (old > now) return false; // 아직 만료 전
            // 만료된 경우 갱신 시도
            boolean replaced = locks.replace(key, old, expireAt);
            return replaced; // 성공 시 true
        }
        return true; // 새로 넣었음
    }

    public void unlock(String key) {
        locks.remove(key);
    }

    // 선택: 주기적 청소가 필요하면 스케줄러로 만료 키 제거 로직 추가 가능
}
