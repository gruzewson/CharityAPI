package app.repositories;

import app.models.FinancialReportProjection;
import app.models.FundraisingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, UUID> {
    // SELECT e.name AS name,
    //        e.accountBalance AS accountBalance,
    //        e.currency AS currency
    //   FROM FundraisingEvent e
    List<FinancialReportProjection> findAllProjectedBy();
}
