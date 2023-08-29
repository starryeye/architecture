package dev.practice.splitpay.api.service.payment;

import dev.practice.splitpay.api.service.payment.dto.PaymentDto;
import dev.practice.splitpay.domain.history.kakaopay.KakaopayMoneySendHistory;
import dev.practice.splitpay.domain.history.kakaopay.KakaopayMoneySendHistoryRepository;
import dev.practice.splitpay.domain.history.kakaopay.KakaopayMoneySendResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final MoneySendClient moneySendClient;
    private final KakaopayMoneySendHistoryRepository kakaopayMoneySendHistoryRepository;

    public void sendMoney(PaymentDto dto) {

        boolean result = moneySendClient.sendMoney(dto.from(), dto.to(), dto.amount(), dto.requestId());


        kakaopayMoneySendHistoryRepository.save(
                KakaopayMoneySendHistory.builder()
                        .fromId(dto.from())
                        .toId(dto.to())
                        .amount(dto.amount())
                        .requestId(dto.requestId())
                        .sendResult(result ? KakaopayMoneySendResult.SUCCESS : KakaopayMoneySendResult.FAIL)
                        .build()
        );

        if (!result) {
            throw new RuntimeException("1/N 정산하기 송금을 실패하였습니다. requestId: " + dto.requestId() + " money sender: " + dto.from()); //TODO
        }
    }
}
