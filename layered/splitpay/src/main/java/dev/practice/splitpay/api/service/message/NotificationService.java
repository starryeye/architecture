package dev.practice.splitpay.api.service.message;

import dev.practice.splitpay.api.service.message.dto.NotificationDto;
import dev.practice.splitpay.domain.history.kakaotalk.KakaotalkTalkSendHistory;
import dev.practice.splitpay.domain.history.kakaotalk.KakaotalkTalkSendHistoryRepository;
import dev.practice.splitpay.domain.history.kakaotalk.KakaotalkTalkSendResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationSendClient notificationSendClient;
    private final KakaotalkTalkSendHistoryRepository kakaotalkTalkSendHistoryRepository;

    public boolean sendNotification(NotificationDto dto) {
        boolean result = notificationSendClient.sendNotification(
                dto.from(), dto.to(), dto.requestId(), dto.amount(), dto.content()
        );

        kakaotalkTalkSendHistoryRepository.save(
                KakaotalkTalkSendHistory.builder()
                        .fromId(dto.from())
                        .toId(dto.to())
                        .requestId(dto.requestId())
                        .amount(dto.amount())
                        .content(dto.content())
                        .sendResult(result ? KakaotalkTalkSendResult.SUCCESS : KakaotalkTalkSendResult.FAIL)
                        .build()
        );

        return result;
    }

    public void sendNotificationBulk(List<NotificationDto> notificationDtos) {
        boolean result = notificationDtos.stream()
                .allMatch(this::sendNotification);

        if (!result) {
            throw new RuntimeException("1/N 정산하기 요청 리마인드 알림 전송에 실패하였습니다. requestId: " + notificationDtos.get(0).requestId());
        }
    }
}
