package app.services;

import app.exceptions.CollectionBoxDoesntExistException;
import app.exceptions.InvalidCurrencyOrAmountException;
import app.models.CollectionBox;
import app.repositories.CollectionBoxRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CollectionBoxService {
    private final CollectionBoxRepository repo;

    public CollectionBoxService(CollectionBoxRepository repo) {
        this.repo = repo;
    }

    public CollectionBox registerBox() {
        return repo.save(new CollectionBox());
    }

    public List<CollectionBox> listAll() {
        return repo.findAll();
    }

    @Transactional
    public CollectionBox putMoney(UUID id, String currency, double amount) throws InvalidCurrencyOrAmountException, CollectionBoxDoesntExistException {
        CollectionBox box = repo.findById(id)
                .orElseThrow(() -> new CollectionBoxDoesntExistException());
        box.putMoney(currency, amount);
        return repo.save(box);
    }

    @Transactional
    public void unregisterBox(UUID id) throws CollectionBoxDoesntExistException {
        if (!repo.existsById(id)) {
            throw new CollectionBoxDoesntExistException();
        }
        repo.deleteById(id);
    }

    @Transactional
    public CollectionBox emptyBox(UUID id) throws CollectionBoxDoesntExistException {
        CollectionBox box = repo.findById(id)
                .orElseThrow(() -> new CollectionBoxDoesntExistException());
        box.emptyBoxFully();
        return repo.save(box);
    }
}
