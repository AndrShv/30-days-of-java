package backend.transactional_outbox_pattern_task.repo;


import backend.transactional_outbox_pattern_task.entity.OutboxEventEntity;
import backend.transactional_outbox_pattern_task.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findTop10ByStatusOrderByCreatedAtAsc(EventStatus eventStatus);
}
