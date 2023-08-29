package dev.practice.splitpay.client.kakaopay;

import dev.practice.splitpay.api.service.payment.MoneySendClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KakaopayMoneySendClient implements MoneySendClient {

    private static final String SUCCESS_MESSAGE =
            """
            %s 님이 %s 님에게 카카오페이 송금 성공하였습니다.
            1/N 정산하기 요청 Id : %d,
            정산 완료 금액 : %d,
            """;

    @Override
    public boolean sendMoney(Long from, Long to, int amount, Long requestId) {

        log.info(SUCCESS_MESSAGE.formatted(from, to, requestId, amount));

        return true;
    }
}
