package dev.practice.pay.account.domain;

import dev.practice.pay.common.ActivityTestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class ActivityWindowTest {
    
    @Test
    void getStartTime() {
        //given
        LocalDateTime startTime = LocalDateTime.now().minusDays(10);
        LocalDateTime betweenTime = LocalDateTime.now().minusDays(5);
        LocalDateTime endTime = LocalDateTime.now();

        Activity startActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(startTime)
                .build();
        Activity betweenActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(betweenTime)
                .build();
        Activity endActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(endTime)
                .build();
        
        ActivityWindow window = new ActivityWindow(startActivity, betweenActivity, endActivity);

        //when
        LocalDateTime getStartTime = window.getStartTime();

        //then
        Assertions.assertThat(getStartTime).isEqualTo(startTime);
    }

    @Test
    void getEndTime() {
        //given
        LocalDateTime startTime = LocalDateTime.now().minusDays(10);
        LocalDateTime betweenTime = LocalDateTime.now().minusDays(5);
        LocalDateTime endTime = LocalDateTime.now();

        Activity startActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(startTime)
                .build();
        Activity betweenActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(betweenTime)
                .build();
        Activity endActivity = ActivityTestData.defaultActivity()
                .withCreatedAt(endTime)
                .build();

        ActivityWindow window = new ActivityWindow(startActivity, betweenActivity, endActivity);

        //when
        LocalDateTime getEndTime = window.getEndTime();

        //then
        Assertions.assertThat(getEndTime).isEqualTo(endTime);
    }
}
