package pl.sda.testingadvanced.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.sda.testingadvanced.domain.model.dtos.TransactionDto;
import pl.sda.testingadvanced.domain.model.entity.PaymentType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@WebAppConfiguration
class ClientControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Test
    void checkBalanceEndpoint() throws Exception {
        //given
        //when and  //then
        MvcResult mvcResult = mockMvc.perform(get("/api/balance/ABC123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        Double balance = Double.parseDouble(response.getContentAsString());

        Assertions.assertEquals(1_000_000.0, balance);
    }

    @Test
    void checkClientDataEndpoint() throws Exception {
        //given
        //when and //then
        mockMvc.perform(get("/api/client/ABC123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.currentBankBalance").value(1_000_000.0));
    }

    @Test
    void checkProcessTransaction() throws Exception {
        //given
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(500_000.0);
        transactionDto.setClientNumber("ABC123");
        transactionDto.setPaymentType(PaymentType.EXTERNAL);

        String jsonBody = new ObjectMapper().writeValueAsString(transactionDto);

        //when and //then
        mockMvc.perform(post("/api/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.currentBankBalance").value(500_000.0));
    }
}