package backend.transactional_outbox_pattern.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import backend.transactional_outbox_pattern.entity.OutboxEventEntity;
import backend.transactional_outbox_pattern.enums.OutboxStatus;

import java.util.List;
import java.util.UUID;



@Repository
public interface OutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
