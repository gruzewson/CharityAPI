package app.services;


import app.exceptions.arguments.ArgumentsException;
import app.exceptions.collection_box.CollectionBoxAlreadyAssignedException;
import app.exceptions.collection_box.CollectionBoxDoesntExistException;
import app.exceptions.collection_box.CollectionBoxException;
import app.exceptions.collection_box.InvalidCollectionBoxException;
import app.exceptions.fundraising_event.FundraisingEventDoesntExistException;
import app.exceptions.fundraising_event.FundraisingEventException;
import app.exceptions.fundraising_event.InvalidEventAssignmentException;
import app.exceptions.fundraising_event.InvalidFundraisingEventException;
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
    public FundraisingEvent createFundraisingEvent() {
        return repo.save(FundraisingEventFactory.createFundraisingEvent());
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
    public void deleteFundraisingEventById(UUID id) throws FundraisingEventDoesntExistException {
        if (!repo.existsById(id)) {
            throw new FundraisingEventDoesntExistException();
        }
        repo.deleteById(id);
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
            throws FundraisingEventException, InvalidCollectionBoxException {
        FundraisingEvent event = repo.findById(eventId)
                .orElseThrow(() -> new FundraisingEventDoesntExistException());
        event.unregisterCollectionBox();
        repo.save(event);
    }

    @Transactional
    public CollectionBox getCollectionBoxByFundraisingEventId(UUID eventId)
            throws FundraisingEventDoesntExistException {
        FundraisingEvent event = repo.findById(eventId)
                .orElseThrow(() -> new FundraisingEventDoesntExistException());
        return event.getCollectionBox();
    }

    @Transactional
    public FundraisingEvent getFundraisingEventById(UUID id) throws FundraisingEventDoesntExistException {
        return repo.findById(id)
                .orElseThrow(() -> new FundraisingEventDoesntExistException());
    }

    @Transactional
    public void transferMoney(UUID eventId)
            throws FundraisingEventDoesntExistException, ArgumentsException, CollectionBoxDoesntExistException {
        FundraisingEvent event = repo.findById(eventId)
                .orElseThrow(() -> new FundraisingEventDoesntExistException());
        event.transferMoney();
        repo.save(event);
    }

}
