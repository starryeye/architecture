package dev.practice.splitpay.domain.settlement;

import dev.practice.splitpay.domain.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
public class SettlementRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Version
    private Integer version;

    private Long requesterId; // User FK

    private int totalAmount;

    private int completedCount;

    @Enumerated(value = EnumType.STRING)
    private SettlementRequestStatus status;


    private LocalDateTime registeredAt;

    @OneToMany(mappedBy = "settlementRequest", cascade = CascadeType.ALL)
    private List<SettlementDetail> settlementDetails = new ArrayList<>();

    @Builder
    private SettlementRequest(Long requesterId, int completedCount, SettlementRequestStatus status, LocalDateTime registeredAt, List<SettlementDetail> settlementDetails) {
        this.requesterId = requesterId;
        this.totalAmount = calculateTotalAmount(settlementDetails);
        this.completedCount = completedCount;
        this.status = status;
        this.registeredAt = registeredAt;
        if(settlementDetails != null) {
            settlementDetails.forEach(this::addSettlementDetail);
        }
    }

    private int calculateTotalAmount(List<SettlementDetail> settlementDetails) {

        if(settlementDetails == null) {
            return 0;
        }

        return settlementDetails.stream()
                .mapToInt(SettlementDetail::getAmount)
                .sum();
    }

    private void addSettlementDetail(SettlementDetail settlementDetail) {
        if (settlementDetails == null) {
            settlementDetails = new ArrayList<>();
        }
        settlementDetails.add(settlementDetail);
        settlementDetail.setSettlementRequest(this);
    }

    private void selfCompleteDetail() {
        Long requesterId = this.getRequesterId();

        this.getSettlementDetails().forEach(detail -> {
            if (detail.getReceiverId().equals(requesterId)) {
                detail.updateStatus(SettlementDetailStatus.COMPLETED);
                completedCount++;
            }
        });
    }

    private boolean hasDuplicationReceiverId() {
        Set<Long> seenReceiverIds = new HashSet<>();
        return settlementDetails.stream()
                .map(SettlementDetail::getReceiverId)
                .anyMatch(receiverId -> !seenReceiverIds.add(receiverId));
    }

    public static SettlementRequest create(Long requesterId, LocalDateTime registeredAt, List<SettlementDetail> settlementDetails) {

        SettlementRequest created = SettlementRequest.builder()
                .requesterId(requesterId)
                .completedCount(0)
                .status(SettlementRequestStatus.PENDING)
                .registeredAt(registeredAt)
                .settlementDetails(settlementDetails)
                .build();

        if(created.hasDuplicationReceiverId()) {
            throw new IllegalArgumentException("하나의 1/N 정산하기 요청에 중복된 요청 대상자가 존재할 수 없습니다.");
        }

        created.selfCompleteDetail();

        return created;
    }

    public void updateDetailsStatus(List<Long> receiverIds, SettlementDetailStatus status) {
        Set<Long> receiverIdSet = new HashSet<>(receiverIds);
        this.getSettlementDetails().forEach(detail -> {
            if (receiverIdSet.contains(detail.getReceiverId())) {
                detail.updateStatus(status);
            }
        });
    }

    public void increaseCompletedCount() {
        this.completedCount++;
    }

    public void checkCompleteStatus() {
        List<SettlementDetail> details = this.getSettlementDetails();

        boolean allCompleted = details.stream()
                .allMatch(detail -> detail.getStatus() == SettlementDetailStatus.COMPLETED);

        if (allCompleted) {
            updateStatus(SettlementRequestStatus.COMPLETED);
        }
    }

    public void updateStatus(SettlementRequestStatus status) {
        this.status = status;
    }

    public List<SettlementDetail> getFilteredWithoutCompletedDetails () {
        return settlementDetails.stream()
                .filter((detail) -> detail.getStatus() != SettlementDetailStatus.COMPLETED)
                .toList();
    }

    public SettlementDetail getDetailByReceiverId(Long receiverId) {
        return getSettlementDetails().stream()
                .filter((detail) -> detail.getReceiverId().equals(receiverId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 정산에서 정산 대상자를 찾을 수 없습니다."));
    }
}
