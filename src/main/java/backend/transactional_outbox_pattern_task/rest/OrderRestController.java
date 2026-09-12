package backend.transactional_outbox_pattern_task.rest;


import backend.transactional_outbox_pattern_task.dto.OrderDTO;
import backend.transactional_outbox_pattern_task.entity.OrderEntity;
import backend.transactional_outbox_pattern_task.services.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderRestController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderEntity> createOrder(@RequestBody OrderDTO orderDTO) throws JsonProcessingException {
        log.info("Creating new order");
        OrderEntity createdOrder = orderService.createOrder(orderDTO);
        log.info("Order created: {}", createdOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);

    }
}
