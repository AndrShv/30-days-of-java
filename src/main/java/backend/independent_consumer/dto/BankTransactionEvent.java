package backend.independent_consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankTransactionEvent implements Serializable {
    private String transactionId;
    private String accountFrom;
    private String accountTo;
    private BigDecimal amount;
    private String currency;
}