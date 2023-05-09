package dev.practice.pay.account.adapter.in.web;

import dev.practice.pay.account.application.port.in.SendMoneyCommand;
import dev.practice.pay.account.application.port.in.SendMoneyUseCase;
import dev.practice.pay.account.domain.Account;
import dev.practice.pay.account.domain.Money;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = SendMoneyController.class)
class SendMoneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SendMoneyUseCase sendMoneyUseCase;

    @Test
    void sendMoney() throws Exception {
        //웹 요청 검증 (응답 검증은 포함X)
        mockMvc.perform(
                MockMvcRequestBuilders.post(
                        "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                        41L, 42L, 500
                        )
                        .header("Content-Type", "application/json")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        //SendMoneyUseCase.sendMoney() 호출할 때 파라미터 검증
        BDDMockito.then(sendMoneyUseCase).should()
                .sendMoney(
                        ArgumentMatchers.eq(
                                new SendMoneyCommand(
                                        new Account.AccountId(41L),
                                        new Account.AccountId(42L),
                                        Money.of(500L)
                                )
                        )
                );
    }
}
