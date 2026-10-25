package backend.luquid_ci_cd_example.repos;


import backend.luquid_ci_cd_example.entity.OutboxEventEntity;
import backend.luquid_ci_cd_example.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface OutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
