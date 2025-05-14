package app.controllers;

import app.exceptions.arguments.ArgumentsException;
import app.exceptions.collection_box.CollectionBoxException;
import app.exceptions.fundraising_event.FundraisingEventException;
import app.models.FinancialReportProjection;
import app.models.FundraisingEvent;
import app.services.FundraisingEventService;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FundraisingEvent createFundraisingEvent(
            @RequestParam String name,
            @RequestParam String currency) {
        return service.createFundraisingEvent(name, currency);
    }

    @GetMapping
    public List<FundraisingEvent> listAll() {
        return service.listAll();
    }

    @GetMapping("/financial-report")
    public List<FinancialReportProjection> getFinancialReport() {
        return service.getFinancialReport();
    }

    @GetMapping("/{id}")
    public FundraisingEvent getFundraisingEventById(@PathVariable UUID id)
            throws FundraisingEventException {
        return service.getFundraisingEventById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFundraisingEventById(@PathVariable UUID id)
            throws FundraisingEventException {
        service.deleteFundraisingEventById(id);
    }

    @PatchMapping("/{eventId}/boxes/{boxId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignCollectionBoxToFundraisingEvent(
            @PathVariable UUID eventId,
            @PathVariable UUID boxId)
            throws FundraisingEventException, CollectionBoxException {
        service.assignCollectionBoxToFundraisingEvent(eventId, boxId);
    }

    @DeleteMapping("/{eventId}/collection-box")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterCollectionBoxFromFundraisingEvent(
            @PathVariable UUID eventId)
            throws FundraisingEventException, CollectionBoxException {
        service.unregisterCollectionBoxFromFundraisingEvent(eventId);
    }

    @PostMapping("/{eventId}/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferMoney(
            @PathVariable UUID eventId)
            throws FundraisingEventException, ArgumentsException, CollectionBoxException {
        service.transferMoney(eventId);
    }
}
