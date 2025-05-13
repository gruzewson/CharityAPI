package app.repositories;

import app.models.FundraisingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, UUID> {
    // TODO financial report
}
