package backend.transactional_outbox_pattern_task.services;


import backend.transactional_outbox_pattern_task.dto.OrderDTO;
import backend.transactional_outbox_pattern_task.entity.OrderEntity;
import backend.transactional_outbox_pattern_task.entity.OutboxEventEntity;
import backend.transactional_outbox_pattern_task.enums.EventStatus;
import backend.transactional_outbox_pattern_task.events.OrderEvent;
import backend.transactional_outbox_pattern_task.repo.OrderRepository;
import backend.transactional_outbox_pattern_task.repo.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;



    @Transactional
    public OrderEntity createOrder(OrderDTO orderDTO) throws JsonProcessingException {
        log.info("Creating new order");
        UUID orderId = UUID.randomUUID();
        log.info("Order ID: {}", orderId);

        OrderEntity new_order = new OrderEntity(
                orderId,
                orderDTO.getUserId(),
                orderDTO.getProductId(),
                orderDTO.getQuantity(),
                orderDTO.getTotalAmount(),
                orderDTO.getStatus(),
                LocalDateTime.now()
        );
        log.info("Saving new order");
        orderRepository.save(new_order);
        log.info("Order saved");


        log.info("Creating new order event");
        OrderEvent event = new OrderEvent(
                orderId,
                orderDTO.getUserId(),
                orderDTO.getProductId(),
                orderDTO.getQuantity(),
                orderDTO.getTotalAmount(),
                LocalDateTime.now()
        );
        log.info("Order event created");

        String payloadJson;
        try {
            log.info("Serializing order event to JSON");
            payloadJson = objectMapper.writeValueAsString(new_order);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order event to JSON", e);
            throw new RuntimeException("Failed to serialize payout event to JSON", e);
        }


        log.info("Creating new outbox event entity");
        OutboxEventEntity outboxEventEntity = new OutboxEventEntity(
                UUID.randomUUID(),
                "ORDER",
                new_order.getId().toString(),
                "ORDER_CREATED",
                payloadJson,
                EventStatus.PENDING,
                LocalDateTime.now()
        );

        log.info("Outbox event entity created");
        outboxRepository.save(outboxEventEntity);
        log.info("Outbox event saved");
        return new_order;




    }
}
