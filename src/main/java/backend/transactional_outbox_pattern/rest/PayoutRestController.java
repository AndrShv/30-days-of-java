package backend.transactional_outbox_pattern.rest;


import backend.transactional_outbox_pattern.dto.PayoutRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import backend.transactional_outbox_pattern.entity.PayoutEntity;
import backend.transactional_outbox_pattern.services.PayoutService;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutRestController {

    private final PayoutService payoutService;

    @PostMapping
    public ResponseEntity<PayoutEntity> createPayout(@RequestBody PayoutRequestDto dto) {
        PayoutEntity createdPayout = payoutService.createPayout(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayout);
    }
}