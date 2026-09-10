package backend.transactional_outbox_pattern_task.services;

import backend.transactional_outbox_pattern_task.dto.OrderDTO;
import backend.transactional_outbox_pattern_task.entity.OrderEntity;
import backend.transactional_outbox_pattern_task.entity.OutboxEventEntity;
import backend.transactional_outbox_pattern_task.enums.EventStatus;
import backend.transactional_outbox_pattern_task.enums.OrderStatus;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderEntity createOrder(OrderDTO orderDTO) {
        log.info("Creating new order");

        OrderEntity newOrder = OrderEntity.builder()
                .userId(orderDTO.getUserId())
                .productId(orderDTO.getProductId())
                .quantity(orderDTO.getQuantity())
                .totalAmount(orderDTO.getTotalAmount())
                .status(orderDTO.getStatus() != null ? orderDTO.getStatus() : OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        log.info("Saving new order to Database");
        newOrder = orderRepository.save(newOrder);
        log.info("Order saved with ID: {}", newOrder.getId());

        OrderEvent event = new OrderEvent(
                newOrder.getId(),
                newOrder.getUserId(),
                newOrder.getProductId(),
                newOrder.getQuantity(),
                newOrder.getTotalAmount(),
                newOrder.getCreatedAt()
        );

        String payloadJson;
        try {
            log.info("Serializing OrderEvent to JSON");
            payloadJson = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize OrderEvent to JSON", e);
            throw new RuntimeException("Failed to serialize OrderEvent to JSON", e);
        }

        OutboxEventEntity outboxEventEntity = OutboxEventEntity.builder()
                .aggregateType("ORDER")
                .aggregateId(newOrder.getId().toString())
                .eventType("ORDER_CREATED")
                .payload(payloadJson)
                .status(EventStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        log.info("Saving Outbox Event Entity");
        outboxRepository.save(outboxEventEntity);
        log.info("Outbox Event saved successfully");

        return newOrder;
    }
}