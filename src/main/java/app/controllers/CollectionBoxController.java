package app.controllers;

import app.exceptions.arguments.ArgumentsException;
import app.exceptions.collection_box.CollectionBoxException;
import app.models.CollectionBox;
import app.services.CollectionBoxService;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public CollectionBox createBox() {
        return service.registerBox();
    }


    @GetMapping
    public List<CollectionBox> getAll() {
        return service.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) throws CollectionBoxException {
        service.unregisterBox(id);
    }

    @PatchMapping("/{id}/money")
    public CollectionBox putMoney(
            @PathVariable UUID id,
            @RequestParam String currency,
            @RequestParam double amount
    ) throws CollectionBoxException, ArgumentsException {
        return service.putMoney(id, currency, amount);
    }

    @PatchMapping("/{id}/empty")
    public CollectionBox empty(@PathVariable UUID id) throws CollectionBoxException {
        return service.emptyBox(id);
    }
}
