package dev.practice.splitpay;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.splitpay.api.controller.settlement.SettlementController;
import dev.practice.splitpay.api.controller.settlement.SettlementQueryController;
import dev.practice.splitpay.api.facade.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        SettlementController.class,
        SettlementQueryController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected CreateSettlementUseCase createSettlementUseCase;

    @MockBean
    protected GetSettlementDetailsUseCase getSettlementDetailsUseCase;

    @MockBean
    protected GetSettlementRequestAndDetailsUseCase getSettlementRequestAndDetailsUseCase;

    @MockBean
    protected GetSettlementRequestsUseCase getSettlementRequestsUseCase;

    @MockBean
    protected PaySettlementUseCase paySettlementUseCase;

    @MockBean
    protected RemindSettlementUseCase remindSettlementUseCase;
}
