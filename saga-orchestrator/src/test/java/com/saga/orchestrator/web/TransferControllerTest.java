package com.saga.orchestrator.web;

import com.saga.orchestrator.constants.SagaConstants;
import com.saga.orchestrator.constants.TestConstants;
import com.saga.orchestrator.entity.SagaInstance;
import com.saga.orchestrator.service.SagaOrchestratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SagaOrchestratorService orchestratorService;

    private static String transferRequestBody() {
        return String.format(
            "{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}",
            TestConstants.JSON_ORIGIN, TestConstants.ORIGIN_ACCOUNT_ID,
            TestConstants.JSON_DESTINATION, TestConstants.DESTINATION_ACCOUNT_ID,
            TestConstants.JSON_AMOUNT, TestConstants.AMOUNT_100,
            TestConstants.JSON_CURRENCY, TestConstants.CURRENCY_USD
        );
    }

    @Test
    void initiateTransferReturnsAccepted() throws Exception {
        SagaInstance instance = new SagaInstance();
        instance.setId(TestConstants.INSTANCE_ID_1);
        instance.setTransferId(TestConstants.TRANSFER_ID_2);
        when(orchestratorService.startTransfer(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(instance);
        mockMvc.perform(post(TestConstants.API_TRANSFERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequestBody()))
                .andExpect(status().isAccepted());
    }

    @Test
    void getStatusReturns200WhenFound() throws Exception {
        SagaInstance instance = new SagaInstance();
        instance.setTransferId(TestConstants.TRANSFER_ID_1);
        instance.setCurrentState(SagaConstants.STATE_COMPLETED);
        when(orchestratorService.getStatus(TestConstants.TRANSFER_ID_1)).thenReturn(Optional.of(instance));
        String statusPath = TestConstants.API_TRANSFERS + "/" + TestConstants.TRANSFER_ID_1 + TestConstants.PATH_STATUS_SUFFIX;
        mockMvc.perform(get(statusPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(TestConstants.TRANSFER_ID_1))
                .andExpect(jsonPath("$.status").value(SagaConstants.STATE_COMPLETED));
    }

    @Test
    void getStatusReturns404WhenNotFound() throws Exception {
        when(orchestratorService.getStatus(TestConstants.TRANSFER_ID_1)).thenReturn(Optional.empty());
        String statusPath = TestConstants.API_TRANSFERS + "/" + TestConstants.TRANSFER_ID_1 + TestConstants.PATH_STATUS_SUFFIX;
        mockMvc.perform(get(statusPath))
                .andExpect(status().isNotFound());
    }
}
