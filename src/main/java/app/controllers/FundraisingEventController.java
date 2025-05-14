package app.controllers;

import app.exceptions.arguments.ArgumentsException;
import app.exceptions.collection_box.CollectionBoxAlreadyAssignedException;
import app.exceptions.collection_box.CollectionBoxDoesntExistException;
import app.exceptions.collection_box.CollectionBoxException;
import app.exceptions.collection_box.InvalidCollectionBoxException;
import app.exceptions.fundraising_event.FundraisingEventDoesntExistException;
import app.exceptions.fundraising_event.FundraisingEventException;
import app.exceptions.fundraising_event.InvalidEventAssignmentException;
import app.exceptions.fundraising_event.InvalidFundraisingEventException;
import app.models.FinancialReportProjection;
import app.models.FundraisingEvent;
import app.services.FundraisingEventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class FundraisingEventController {

    private final FundraisingEventService service;

    public FundraisingEventController(FundraisingEventService service) {
        this.service = service;
    }

    @PostMapping("/custom-event")
    public FundraisingEvent createFundraisingEvent(
            @RequestParam("name") String name,
            @RequestParam("currency") String currency) {
        return service.createFundraisingEvent(name, currency);
    }

    @GetMapping("/all")
    public List<FundraisingEvent> listAll() {
        return service.listAll();
    }

    @GetMapping("/financial-report")
    public List<FinancialReportProjection> getFinancialReport() {
        return service.getFinancialReport();
    }

    @DeleteMapping
    public void deleteFundraisingEventById(@RequestParam("id") UUID id)
            throws FundraisingEventDoesntExistException {
        service.deleteFundraisingEventById(id);
    }

    @PatchMapping("/{eventId}/boxes/{boxId}")
    public void assignCollectionBoxToFundraisingEvent(
            @PathVariable("eventId") UUID eventId,
            @PathVariable("boxId") UUID boxId)
            throws FundraisingEventException, CollectionBoxException {
        service.assignCollectionBoxToFundraisingEvent(eventId, boxId);
    }

    @DeleteMapping("/{eventId}/unregister")
    public void unregisterCollectionBoxFromFundraisingEvent(
            @PathVariable("eventId") UUID eventId)
            throws FundraisingEventException, CollectionBoxException {
        service.unregisterCollectionBoxFromFundraisingEvent(eventId);
    }

    @PostMapping("/{eventId}/transfer")
    public void transferMoney(
            @PathVariable("eventId") UUID eventId)
            throws FundraisingEventDoesntExistException, ArgumentsException, CollectionBoxException {
        service.transferMoney(eventId);
    }
}
