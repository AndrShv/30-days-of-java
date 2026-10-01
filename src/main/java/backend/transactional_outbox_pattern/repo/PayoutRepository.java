package backend.transactional_outbox_pattern.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import backend.transactional_outbox_pattern.entity.PayoutEntity;

import java.util.UUID;

@Repository
public interface PayoutRepository extends JpaRepository<PayoutEntity, UUID> {
}
