package app.services;


import app.exceptions.arguments.*;
import app.exceptions.collection_box.*;
import app.exceptions.fundraising_event.*;
import app.factories.FundraisingEventFactory;
import app.models.CollectionBox;
import app.models.FinancialReportProjection;
import app.models.FundraisingEvent;
import app.repositories.CollectionBoxRepository;
import app.repositories.FundraisingEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FundraisingEventService {
    private final FundraisingEventRepository repo;
    private final CollectionBoxRepository boxRepo;

    public FundraisingEventService(FundraisingEventRepository repo,
                                   CollectionBoxRepository boxRepo) {
        this.repo    = repo;
        this.boxRepo = boxRepo;
    }

    @Transactional
    public FundraisingEvent createFundraisingEvent(String name, String currency) {
        return repo.save(FundraisingEventFactory.createFundraisingEvent(name, currency));
    }

    @Transactional
    public List<FundraisingEvent> listAll() {
        return repo.findAll();
    }

    @Transactional
    public List<FinancialReportProjection> getFinancialReport() {
        return repo.findAllProjectedBy();
    }

    @Transactional
    public void deleteFundraisingEventById(UUID id) throws FundraisingEventException {
        FundraisingEvent event = repo.findById(id)
                .orElseThrow(FundraisingEventDoesntExistException::new);
        repo.delete(event);
    }

    @Transactional
    public void assignCollectionBoxToFundraisingEvent(UUID eventId, UUID boxId)
            throws FundraisingEventException, CollectionBoxException {
        FundraisingEvent event = repo.findById(eventId)
                .orElseThrow(() -> new FundraisingEventDoesntExistException());
        CollectionBox box = boxRepo.findById(boxId)
                .orElseThrow(() -> new CollectionBoxDoesntExistException());
        event.assignCollectionBox(box);
        repo.save(event);
    }

    @Transactional
    public void unregisterCollectionBoxFromFundraisingEvent(UUID eventId)
            throws FundraisingEventException, CollectionBoxException {
        FundraisingEvent event = repo.findById(eventId)
                .orElseThrow(() -> new FundraisingEventDoesntExistException());
        event.unregisterCollectionBox();
        repo.save(event);
    }

    @Transactional
    public CollectionBox getCollectionBoxByFundraisingEventId(UUID eventId)
            throws FundraisingEventException {
        FundraisingEvent event = repo.findById(eventId)
                .orElseThrow(() -> new FundraisingEventDoesntExistException());
        return event.getCollectionBox();
    }

    @Transactional
    public FundraisingEvent getFundraisingEventById(UUID id) throws FundraisingEventException {
        return repo.findById(id)
                .orElseThrow(() -> new FundraisingEventDoesntExistException());
    }

    @Transactional
    public void transferMoney(UUID eventId)
            throws FundraisingEventException, ArgumentsException, CollectionBoxException {
        FundraisingEvent event = repo.findById(eventId)
                .orElseThrow(() -> new FundraisingEventDoesntExistException());
        event.transferMoney();
        repo.save(event);
    }

}
