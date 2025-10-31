package com.hagglemarket.marketweb.user.service;

import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BotUserSupport {

    private final UserRepository userRepository;

    //username/userId는 서비스 내에서 절대 중복 안 나게 고정값 사용
    private static final String BOT_USER_ID = "hagglebot";
    private static final String BOT_NICK = "해글봇";
    public static final String BOT_IMAGE_URL = "/images/bot-avatar.png";

    @Transactional
    public User getOrCreateBotUser() {
        return userRepository.findByUserId(BOT_USER_ID)
                .orElseGet(() -> userRepository.save(
                        User.createBot(BOT_USER_ID, BOT_NICK, BOT_IMAGE_URL)
                ));
    }
}
