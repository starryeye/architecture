package dev.practice.splitpay.client.kakaotalk;

import dev.practice.splitpay.api.service.message.NotificationSendClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KakaotalkTalkSendClient implements NotificationSendClient {

    private static final String SUCCESS_MESSAGE =
            """
            %s 님이 %s 님에게 카카오톡 알림 메시지를 보냈습니다.
            1/N 정산하기 요청 Id : %d,
            정산 요청 금액 : %d,
            메시지 : %s
            """;

    @Override
    public boolean sendNotification(Long from, Long to, Long requestId, int amount, String content) {

        log.info(SUCCESS_MESSAGE.formatted(from, to, requestId, amount, content));

        return true;
    }
}
