package backend.test_containers_luqiud_example;

import backend.BaseIntegrationTest;
import backend.transactional_outbox_pattern_task.entity.OrderEntity;
import backend.transactional_outbox_pattern_task.enums.OrderStatus;
import backend.transactional_outbox_pattern_task.repo.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Liquibase должен успешно применить SQL-миграцию и создать индекс в Postgres")
    void shouldVerifyPostgresSpecificSchemaCreatedByLiquibase() {
        String indexName = jdbcTemplate.queryForObject(
                "SELECT indexname FROM pg_indexes WHERE indexname = 'idx_orders_status_created'",
                String.class
        );
        assertThat(indexName).isEqualTo("idx_orders_status_created");
    }

    @Test
    @DisplayName("Должен сохранять и извлекать заказ из реальной базы PostgreSQL")
    void shouldSaveAndFindOrderInRealPostgres() {
        OrderEntity order = OrderEntity.builder()
                .userId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .quantity(2)
                .totalAmount(new BigDecimal("250.00"))
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        OrderEntity savedOrder = orderRepository.save(order);

        assertThat(savedOrder.getId()).isNotNull();

        Optional<OrderEntity> found = orderRepository.findById(savedOrder.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(order.getUserId());
        assertThat(found.get().getTotalAmount()).isEqualByComparingTo("250.00");
    }
}