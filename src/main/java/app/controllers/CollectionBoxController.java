package app.controllers;

import app.exceptions.CollectionBoxDoesntExistException;
import app.exceptions.InvalidCurrencyOrAmountException;
import app.models.CollectionBox;
import app.services.CollectionBoxService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/boxes")
public class CollectionBoxController {
    private final CollectionBoxService service;

    public CollectionBoxController(CollectionBoxService service) {
        this.service = service;
    }

    @PostMapping
    public CollectionBox createBox() {
        return service.registerBox();
    }

    @GetMapping
    public List<CollectionBox> getAll() {
        return service.listAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) throws CollectionBoxDoesntExistException {
        service.unregisterBox(id);
    }

    @PatchMapping("/{id}/money")
    public CollectionBox putMoney(
            @PathVariable UUID id,
            @RequestParam String currency,
            @RequestParam double amount) throws CollectionBoxDoesntExistException, InvalidCurrencyOrAmountException {
            return service.putMoney(id, currency, amount);
    }

    @PatchMapping("/{id}/empty")
    public CollectionBox empty(@PathVariable UUID id) throws CollectionBoxDoesntExistException {
        return service.emptyBox(id);
    }
}
