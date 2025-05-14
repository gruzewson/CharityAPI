package fundraising_event_tests;

import app.controllers.FundraisingEventController;
import app.exceptions.arguments.ArgumentsException;
import app.exceptions.collection_box.CollectionBoxException;
import app.exceptions.fundraising_event.FundraisingEventDoesntExistException;
import app.exceptions.fundraising_event.FundraisingEventException;
import app.factories.FundraisingEventFactory;
import app.models.FinancialReportProjection;
import app.models.FundraisingEvent;
import app.services.FundraisingEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FundraisingEventControllerTests {
    @Mock
    private FundraisingEventService service;

    @InjectMocks
    private FundraisingEventController controller;

    private FundraisingEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = FundraisingEventFactory.createFundraisingEvent("Test Event", "PLN");
    }

    @Test
    void createFundraisingEvent_ShouldReturnNewEvent() {
        String name = "Test Event";
        String currency = "PLN";
        when(service.createFundraisingEvent(name, currency)).thenReturn(sampleEvent);

        FundraisingEvent result = controller.createFundraisingEvent(name, currency);

        assertSame(sampleEvent, result);
        verify(service).createFundraisingEvent(name, currency);
    }

    @Test
    void createFundraisingEventWithParams_ShouldReturnNewEvent() {
        String name = "Test Event";
        String currency = "PLN";
        when(service.createFundraisingEvent(name, currency)).thenReturn(sampleEvent);

        FundraisingEvent result = controller.createFundraisingEvent(name, currency);

        assertSame(sampleEvent, result);
        verify(service).createFundraisingEvent(name, currency);
    }

    @Test
    void listAll_ShouldReturnListOfEvents() {
        List<FundraisingEvent> events = List.of(sampleEvent);
        when(service.listAll()).thenReturn(events);

        List<FundraisingEvent> result = controller.listAll();

        assertSame(events, result);
        verify(service).listAll();
    }

    @Test
    void getFinancialReport_ShouldReturnFinancialReport() {
        String name       = "Test Event";
        String currency   = "PLN";
        FinancialReportProjection p1 = new FinancialReportProjection() {
            @Override public String getName()            { return name; }
            @Override public String getCurrency()        { return currency; }
            @Override public Double getAccountBalance()  { return 0.0; }
        };
        List<FinancialReportProjection> report = List.of(p1);
        when(service.getFinancialReport()).thenReturn(report);

        List<FinancialReportProjection> result = controller.getFinancialReport();

        assertSame(report, result);
        verify(service).getFinancialReport();
    }

    @Test
    void deleteFundraisingEventById_ShouldCallServiceToDeleteEvent() throws FundraisingEventDoesntExistException {
        UUID eventId = sampleEvent.getUuid();

        controller.deleteFundraisingEventById(eventId);

        verify(service).deleteFundraisingEventById(eventId);
    }

    @Test
    void assignCollectionBoxToFundraisingEvent_ShouldCallService()
            throws FundraisingEventException, CollectionBoxException {
        UUID eventId = sampleEvent.getUuid();
        UUID boxId = UUID.randomUUID();

        controller.assignCollectionBoxToFundraisingEvent(eventId, boxId);

        verify(service).assignCollectionBoxToFundraisingEvent(eventId, boxId);
    }

    @Test
    void unregisterCollectionBoxFromFundraisingEvent_ShouldCallService()
            throws FundraisingEventException, CollectionBoxException {
        UUID eventId = sampleEvent.getUuid();

        controller.unregisterCollectionBoxFromFundraisingEvent(eventId);

        verify(service).unregisterCollectionBoxFromFundraisingEvent(eventId);
    }

    @Test
    void transferMoney_ShouldCallService()
            throws FundraisingEventDoesntExistException,
            CollectionBoxException, ArgumentsException {
        UUID eventId = sampleEvent.getUuid();

        controller.transferMoney(eventId);

        verify(service).transferMoney(eventId);
    }
}
