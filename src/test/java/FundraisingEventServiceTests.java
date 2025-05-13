import app.exceptions.*;
import app.factories.CollectionBoxFactory;
import app.factories.FundraisingEventFactory;
import app.models.CollectionBox;
import app.models.FinancialReportProjection;
import app.models.FundraisingEvent;
import app.repositories.CollectionBoxRepository;
import app.repositories.FundraisingEventRepository;
import app.services.CollectionBoxService;
import app.services.FundraisingEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FundraisingEventServiceTests {
    @Mock
    private FundraisingEventRepository eventRepository;

    @Mock
    private CollectionBoxRepository boxRepository;

    @InjectMocks
    private FundraisingEventService fundraisingEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void crateFundraisingEventWithoutArguments_ShouldReturnNewFundraisingEvent() {
        FundraisingEvent newEvent = FundraisingEventFactory.createFundraisingEvent();
        when(eventRepository.save(any(FundraisingEvent.class))).thenReturn(newEvent);

        FundraisingEvent result = fundraisingEventService.createFundraisingEvent();

        assertNotNull(result);
        assertEquals(newEvent, result);
    }

    @Test
    public void createFundraisingEventWithArguments_ShouldReturnNewFundraisingEvent() {
        String name = "Test Event";
        String currency = "USD";
        FundraisingEvent newEvent = FundraisingEventFactory.createFundraisingEvent(name, currency);
        when(eventRepository.save(any(FundraisingEvent.class))).thenReturn(newEvent);

        FundraisingEvent result = fundraisingEventService.createFundraisingEvent(name, currency);

        assertNotNull(result);
        assertEquals(newEvent, result);
    }

    @Test
    public void listAll_ShouldReturnListOfFundraisingEvents() {
        FundraisingEvent event1 = FundraisingEventFactory.createFundraisingEvent();
        FundraisingEvent event2 = FundraisingEventFactory.createFundraisingEvent();
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<FundraisingEvent> result = fundraisingEventService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(event1, result.get(0));
        assertEquals(event2, result.get(1));
    }

    @Test
    public void getFinancialReport_ShouldReturnFinancialReport() {
        String name       = "Test Event";
        String currency   = "PLN";
        FundraisingEvent e1 = FundraisingEventFactory.createFundraisingEvent(name, currency);
        FundraisingEvent e2 = FundraisingEventFactory.createFundraisingEvent(name, currency);

        FinancialReportProjection p1 = new FinancialReportProjection() {
            @Override public String getName()            { return name; }
            @Override public String getCurrency()        { return currency; }
            @Override public Double getAccountBalance()  { return 0.0; }
        };
        FinancialReportProjection p2 = new FinancialReportProjection() {
            @Override public String getName()            { return name; }
            @Override public String getCurrency()        { return currency; }
            @Override public Double getAccountBalance()  { return 0.0; }
        };

       when(eventRepository.findAllProjectedBy())
                .thenReturn(List.of(p1, p2));

        List<FinancialReportProjection> report =
                fundraisingEventService.getFinancialReport();

        assertNotNull(report);
        assertEquals(2, report.size());

        for (FinancialReportProjection row : report) {
            assertEquals(name, row.getName());
            assertEquals(currency, row.getCurrency());
            assertEquals(0.0, row.getAccountBalance());
        }

    }

    @Test
    public void deleteFundraisingEventById_ShouldDeleteEvent()
            throws FundraisingEventDoesntExistException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        when(eventRepository.existsById(event.getUuid())).thenReturn(true);

        fundraisingEventService.deleteFundraisingEventById(event.getUuid());

        when(eventRepository.existsById(event.getUuid())).thenReturn(false);
        assertFalse(eventRepository.existsById(event.getUuid()));
    }

    @Test
    public void deleteFundraisingEventById_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        when(eventRepository.existsById(event.getUuid())).thenReturn(false);

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.deleteFundraisingEventById(event.getUuid());
        });
    }

    @Test
    public void assignCollectionBoxToFundraisingEvent_ShouldAssignBoxToEvent()
            throws FundraisingEventDoesntExistException, InvalidCollectionBoxException,
            InvalidEventAssignmentException, InvalidFundraisingEventUUIDException,
            CollectionBoxAlreadyAssignedException, CollectionBoxDoesntExistException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.of(event));
        when(eventRepository.save(any(FundraisingEvent.class))).thenReturn(event);
        when(boxRepository.findById(box.getUuid())).thenReturn(java.util.Optional.of(box));

        fundraisingEventService.assignCollectionBoxToFundraisingEvent(event.getUuid(), box.getUuid());

        assertEquals(box, event.getCollectionBox());
    }

    @Test
    public void assignCollectionBoxToFundraisingEvent_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.empty());
        when(boxRepository.findById(box.getUuid())).thenReturn(java.util.Optional.of(box));

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.assignCollectionBoxToFundraisingEvent(event.getUuid(), box.getUuid());
        });
    }

    @Test
    public void assignCollectionBoxToFundraisingEvent_ShouldThrowException_WhenBoxDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.of(event));
        when(boxRepository.findById(box.getUuid())).thenReturn(java.util.Optional.empty());

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            fundraisingEventService.assignCollectionBoxToFundraisingEvent(event.getUuid(), box.getUuid());
        });
    }

    @Test
    public void unregisterCollectionBoxFromFundraisingEvent_ShouldUnassignBoxFromEvent()
            throws FundraisingEventDoesntExistException, InvalidCollectionBoxException,
            InvalidFundraisingEventUUIDException, InvalidEventAssignmentException, CollectionBoxAlreadyAssignedException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        event.assignCollectionBox(CollectionBoxFactory.createCollectionBox());
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.of(event));
        when(eventRepository.save(any(FundraisingEvent.class))).thenReturn(event);

        fundraisingEventService.unregisterCollectionBoxFromFundraisingEvent(event.getUuid());

        assertNull(event.getCollectionBox());
    }

    @Test
    public void unregisterCollectionBoxFromFundraisingEvent_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.empty());

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.unregisterCollectionBoxFromFundraisingEvent(event.getUuid());
        });
    }

    @Test
    public void getCollectionBoxByFundraisingEventId_ShouldReturnCollectionBox()
            throws FundraisingEventDoesntExistException, InvalidCollectionBoxException, InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(box);
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.of(event));

        CollectionBox result = fundraisingEventService.getCollectionBoxByFundraisingEventId(event.getUuid());

        assertNotNull(result);
        assertEquals(box, result);
    }

    @Test
    public void getCollectionBoxByFundraisingEventId_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.empty());

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.getCollectionBoxByFundraisingEventId(event.getUuid());
        });
    }

    @Test
    public void getFundraisingEventById_ShouldReturnFundraisingEvent()
            throws FundraisingEventDoesntExistException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.of(event));

        FundraisingEvent result = fundraisingEventService.getFundraisingEventById(event.getUuid());

        assertNotNull(result);
        assertEquals(event, result);
    }

    @Test
    public void getFundraisingEventById_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.empty());

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.getFundraisingEventById(event.getUuid());
        });
    }

    @Test
    public void transferMoney_ShouldTransfer() throws InvalidCollectionBoxException, InvalidEventAssignmentException, InvalidFundraisingEventUUIDException, CollectionBoxAlreadyAssignedException, InvalidCurrencyOrAmountException, CollectionBoxDoesntExistException, FundraisingEventDoesntExistException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent("Test Event", "PLN");
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(box);
        box.putMoney("PLN", 100.0);
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.of(event));
        when(boxRepository.findById(box.getUuid())).thenReturn(java.util.Optional.of(box));

        fundraisingEventService.transferMoney(event.getUuid());

        assertEquals(100.0, event.getAccountBalance());
    }

    @Test
    public void transferMoney_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent();
        when(eventRepository.findById(event.getUuid())).thenReturn(java.util.Optional.empty());

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.transferMoney(event.getUuid());
        });
    }
}
