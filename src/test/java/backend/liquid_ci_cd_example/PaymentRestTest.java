package backend.liquid_ci_cd_example;


import backend.liquid_ci_cd_example.dto.PaymentDto;
import backend.liquid_ci_cd_example.entity.Payment;
import backend.liquid_ci_cd_example.enums.PaymentStatus;
import backend.liquid_ci_cd_example.interfaces.PaymentService;
import backend.liquid_ci_cd_example.rest.PaymentRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentRest.class)
class PaymentRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void createPaymentEndpoint_ReturnsCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        PaymentDto dto = PaymentDto.builder()
                .userId(userId)
                .amount(BigDecimal.ZERO)
                .status(PaymentStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();

        Payment savedPayment = Payment.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .amount(BigDecimal.ZERO)
                .status(PaymentStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.createPayment(any(PaymentDto.class))).thenReturn(savedPayment);

        mockMvc.perform(post("/api/payment/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedPayment.getId().toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        verify(paymentService, times(1)).createPayment(any(PaymentDto.class));
    }

    @Test
    void cancelPaymentEndpoint_Success() throws Exception {
        UUID paymentId = UUID.randomUUID();
        doNothing().when(paymentService).deletePayment(paymentId);

        mockMvc.perform(delete("/api/payment/cancel")
                        .param("paymentId", paymentId.toString()))
                .andExpect(status().isOk());

        verify(paymentService, times(1)).deletePayment(paymentId);
    }
}