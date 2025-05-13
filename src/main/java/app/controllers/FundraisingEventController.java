package app.controllers;

import app.exceptions.*;
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
            throws FundraisingEventDoesntExistException, CollectionBoxDoesntExistException,
            InvalidCollectionBoxException, InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException {
        service.assignCollectionBoxToFundraisingEvent(eventId, boxId);
    }

    @DeleteMapping("/{eventId}/unregister")
    public void unregisterCollectionBoxFromFundraisingEvent(
            @PathVariable("eventId") UUID eventId)
            throws FundraisingEventDoesntExistException, InvalidCollectionBoxException, InvalidFundraisingEventUUIDException {
        service.unregisterCollectionBoxFromFundraisingEvent(eventId);
    }

    @PostMapping("/{eventId}/transfer")
    public void transferMoney(
            @PathVariable("eventId") UUID eventId)
            throws FundraisingEventDoesntExistException, InvalidCurrencyOrAmountException, CollectionBoxDoesntExistException {
        service.transferMoney(eventId);
    }
}
