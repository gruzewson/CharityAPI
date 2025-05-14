package fundraising_event_tests;

import app.exceptions.arguments.*;
import app.exceptions.collection_box.*;
import app.exceptions.fundraising_event.*;
import app.factories.CollectionBoxFactory;
import app.factories.FundraisingEventFactory;
import app.models.CollectionBox;
import app.models.FinancialReportProjection;
import app.models.FundraisingEvent;
import app.repositories.CollectionBoxRepository;
import app.repositories.FundraisingEventRepository;
import app.services.FundraisingEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
    
    private static final String CORRECT_CURRENCY = "PLN";
    private static final String CORRECT_NAME = "Test Event";
    private static final double CORRECT_AMOUNT = 100.0;

    @Test
    public void createFundraisingEventWithArguments_ShouldReturnNewFundraisingEvent() {
        FundraisingEvent newEvent = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        when(eventRepository.save(any(FundraisingEvent.class))).thenReturn(newEvent);

        FundraisingEvent result = fundraisingEventService.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);

        assertNotNull(result);
        assertEquals(newEvent, result);
    }

    @Test
    public void listAll_ShouldReturnListOfFundraisingEvents() {
        FundraisingEvent event1 = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        FundraisingEvent event2 = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<FundraisingEvent> result = fundraisingEventService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(event1, result.get(0));
        assertEquals(event2, result.get(1));
    }

    @Test
    public void getFinancialReport_ShouldReturnFinancialReport() {
        FinancialReportProjection p1 = new FinancialReportProjection() {
            @Override public String getName()            { return CORRECT_NAME; }
            @Override public String getCurrency()        { return CORRECT_CURRENCY; }
            @Override public Double getAccountBalance()  { return 0.0; }
        };
        FinancialReportProjection p2 = new FinancialReportProjection() {
            @Override public String getName()            { return CORRECT_NAME; }
            @Override public String getCurrency()        { return CORRECT_CURRENCY; }
            @Override public Double getAccountBalance()  { return 0.0; }
        };

       when(eventRepository.findAllProjectedBy())
                .thenReturn(List.of(p1, p2));

        List<FinancialReportProjection> report =
                fundraisingEventService.getFinancialReport();

        assertNotNull(report);
        assertEquals(2, report.size());

        for (FinancialReportProjection row : report) {
            assertEquals(CORRECT_NAME, row.getName());
            assertEquals(CORRECT_CURRENCY, row.getCurrency());
            assertEquals(0.0, row.getAccountBalance());
        }

    }

    @Test
    public void deleteFundraisingEventById_ShouldDeleteEvent()
            throws FundraisingEventException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.of(event));

        fundraisingEventService.deleteFundraisingEventById(event.getUuid());

        verify(eventRepository).delete(event);
    }

    @Test
    public void deleteFundraisingEventById_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        when(eventRepository.existsById(event.getUuid())).thenReturn(false);

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.deleteFundraisingEventById(event.getUuid());
        });
    }

    @Test
    public void assignCollectionBoxToFundraisingEvent_ShouldAssignBoxToEvent()
            throws FundraisingEventException, CollectionBoxException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(FundraisingEvent.class))).thenReturn(event);
        when(boxRepository.findById(box.getUuid())).thenReturn(Optional.of(box));

        fundraisingEventService.assignCollectionBoxToFundraisingEvent(event.getUuid(), box.getUuid());

        assertEquals(box, event.getCollectionBox());
    }

    @Test
    public void assignCollectionBoxToFundraisingEvent_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.empty());
        when(boxRepository.findById(box.getUuid())).thenReturn(Optional.of(box));

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.assignCollectionBoxToFundraisingEvent(event.getUuid(), box.getUuid());
        });
    }

    @Test
    public void assignCollectionBoxToFundraisingEvent_ShouldThrowException_WhenBoxDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.of(event));
        when(boxRepository.findById(box.getUuid())).thenReturn(Optional.empty());

        assertThrows(CollectionBoxDoesntExistException.class, () -> {
            fundraisingEventService.assignCollectionBoxToFundraisingEvent(event.getUuid(), box.getUuid());
        });
    }

    @Test
    public void unregisterCollectionBoxFromFundraisingEvent_ShouldUnassignBoxFromEvent()
            throws FundraisingEventException, CollectionBoxException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        event.assignCollectionBox(CollectionBoxFactory.createCollectionBox());
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(FundraisingEvent.class))).thenReturn(event);

        fundraisingEventService.unregisterCollectionBoxFromFundraisingEvent(event.getUuid());

        assertNull(event.getCollectionBox());
    }

    @Test
    public void unregisterCollectionBoxFromFundraisingEvent_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.empty());

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.unregisterCollectionBoxFromFundraisingEvent(event.getUuid());
        });
    }

    @Test
    public void getCollectionBoxByFundraisingEventId_ShouldReturnCollectionBox()
            throws FundraisingEventException, CollectionBoxException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(box);
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.of(event));

        CollectionBox result = fundraisingEventService.getCollectionBoxByFundraisingEventId(event.getUuid());

        assertNotNull(result);
        assertEquals(box, result);
    }

    @Test
    public void getCollectionBoxByFundraisingEventId_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.empty());

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.getCollectionBoxByFundraisingEventId(event.getUuid());
        });
    }

    @Test
    public void getFundraisingEventById_ShouldReturnFundraisingEvent()
            throws FundraisingEventException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.of(event));

        FundraisingEvent result = fundraisingEventService.getFundraisingEventById(event.getUuid());

        assertNotNull(result);
        assertEquals(event, result);
    }

    @Test
    public void getFundraisingEventById_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.empty());

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.getFundraisingEventById(event.getUuid());
        });
    }

    @Test
    public void transferMoney_ShouldTransfer() throws CollectionBoxException, FundraisingEventException, ArgumentsException {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        CollectionBox box = CollectionBoxFactory.createCollectionBox();
        event.assignCollectionBox(box);
        box.putMoney(CORRECT_CURRENCY, CORRECT_AMOUNT);
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.of(event));
        when(boxRepository.findById(box.getUuid())).thenReturn(Optional.of(box));

        fundraisingEventService.transferMoney(event.getUuid());

        assertEquals(CORRECT_AMOUNT, event.getAccountBalance());
    }

    @Test
    public void transferMoney_ShouldThrowException_WhenEventDoesNotExist() {
        FundraisingEvent event = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
        when(eventRepository.findById(event.getUuid())).thenReturn(Optional.empty());

        assertThrows(FundraisingEventDoesntExistException.class, () -> {
            fundraisingEventService.transferMoney(event.getUuid());
        });
    }
}
