package fundraising_event_tests;

import app.controllers.FundraisingEventController;
import app.exceptions.arguments.ArgumentsException;
import app.exceptions.collection_box.CollectionBoxDoesntExistException;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FundraisingEventControllerTests {

    @Mock
    private FundraisingEventService service;

    @InjectMocks
    private FundraisingEventController controller;

    private FundraisingEvent sampleEvent;

    private static final String CORRECT_NAME = "Test Event";
    private static final String CORRECT_CURRENCY = "PLN";

    @BeforeEach
    public void setUp() {
        sampleEvent = FundraisingEventFactory.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
    }

    @Test
    public void createFundraisingEvent_ShouldReturnNewEvent() {
        when(service.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY)).thenReturn(sampleEvent);

        FundraisingEvent result = controller.createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);

        assertSame(sampleEvent, result);
        verify(service).createFundraisingEvent(CORRECT_NAME, CORRECT_CURRENCY);
    }

    @Test
    public void listAll_ShouldReturnListOfEvents() {
        List<FundraisingEvent> events = List.of(sampleEvent);
        when(service.listAll()).thenReturn(events);

        List<FundraisingEvent> result = controller.listAll();

        assertEquals(events, result);
        verify(service).listAll();
    }

    @Test
    public void listAll_ShouldReturnEmptyList_WhenNoEventsExist() {
        when(service.listAll()).thenReturn(Collections.emptyList());

        List<FundraisingEvent> result = controller.listAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getFinancialReport_ShouldReturnFinancialReport() {
        FinancialReportProjection projection = new FinancialReportProjection() {
            @Override public String getName() { return CORRECT_NAME; }
            @Override public String getCurrency() { return CORRECT_CURRENCY; }
            @Override public Double getAccountBalance() { return 0.0; }
        };

        List<FinancialReportProjection> report = List.of(projection);
        when(service.getFinancialReport()).thenReturn(report);

        List<FinancialReportProjection> result = controller.getFinancialReport();

        assertSame(report, result);
        verify(service).getFinancialReport();
    }

    @Test
    public void getFundraisingEventById_ShouldReturnEvent() throws FundraisingEventException {
        UUID eventId = sampleEvent.getUuid();
        when(service.getFundraisingEventById(eventId)).thenReturn(sampleEvent);

        FundraisingEvent result = controller.getFundraisingEventById(eventId);

        assertSame(sampleEvent, result);
        verify(service).getFundraisingEventById(eventId);
    }

    @Test
    public void getFundraisingEventById_ShouldThrowException_WhenEventNotFound() throws FundraisingEventException {
        UUID eventId = UUID.randomUUID();
        doThrow(FundraisingEventDoesntExistException.class).when(service).getFundraisingEventById(eventId);

        assertThrows(FundraisingEventDoesntExistException.class, () -> controller.getFundraisingEventById(eventId));

        verify(service).getFundraisingEventById(eventId);
    }

    @Test
    public void deleteFundraisingEventById_ShouldCallService() throws FundraisingEventException {
        UUID eventId = sampleEvent.getUuid();

        controller.deleteFundraisingEventById(eventId);

        verify(service).deleteFundraisingEventById(eventId);
    }

    @Test
    public void deleteFundraisingEventById_ShouldThrowException_WhenEventNotFound() throws FundraisingEventException {
        UUID eventId = UUID.randomUUID();
        doThrow(FundraisingEventDoesntExistException.class).when(service).deleteFundraisingEventById(eventId);

        assertThrows(FundraisingEventDoesntExistException.class, () -> controller.deleteFundraisingEventById(eventId));

        verify(service).deleteFundraisingEventById(eventId);
    }

    @Test
    public void assignCollectionBoxToFundraisingEvent_ShouldCallService() throws FundraisingEventException, CollectionBoxException {
        UUID eventId = sampleEvent.getUuid();
        UUID boxId = UUID.randomUUID();

        controller.assignCollectionBoxToFundraisingEvent(eventId, boxId);

        verify(service).assignCollectionBoxToFundraisingEvent(eventId, boxId);
    }

    @Test
    public void assignCollectionBoxToFundraisingEvent_ShouldThrowException_WhenEventNotFound() throws FundraisingEventException, CollectionBoxException {
        UUID eventId = UUID.randomUUID();
        UUID boxId = UUID.randomUUID();
        doThrow(FundraisingEventDoesntExistException.class).when(service).assignCollectionBoxToFundraisingEvent(eventId, boxId);

        assertThrows(FundraisingEventDoesntExistException.class, () -> controller.assignCollectionBoxToFundraisingEvent(eventId, boxId));

        verify(service).assignCollectionBoxToFundraisingEvent(eventId, boxId);
    }

    @Test
    public void assignCollectionBoxToFundraisingEvent_ShouldThrowException_WhenBoxNotFound() throws FundraisingEventException, CollectionBoxException {
        UUID eventId = sampleEvent.getUuid();
        UUID boxId = UUID.randomUUID();
        doThrow(CollectionBoxDoesntExistException.class).when(service).assignCollectionBoxToFundraisingEvent(eventId, boxId);

        assertThrows(CollectionBoxDoesntExistException.class, () -> controller.assignCollectionBoxToFundraisingEvent(eventId, boxId));

        verify(service).assignCollectionBoxToFundraisingEvent(eventId, boxId);
    }

    @Test
    public void unregisterCollectionBoxFromFundraisingEvent_ShouldCallService() throws FundraisingEventException, CollectionBoxException {
        UUID eventId = sampleEvent.getUuid();

        controller.unregisterCollectionBoxFromFundraisingEvent(eventId);

        verify(service).unregisterCollectionBoxFromFundraisingEvent(eventId);
    }

    @Test
    public void unregisterCollectionBoxFromFundraisingEvent_ShouldThrowException_WhenEventNotFound() throws FundraisingEventException, CollectionBoxException {
        UUID eventId = UUID.randomUUID();
        doThrow(FundraisingEventDoesntExistException.class).when(service).unregisterCollectionBoxFromFundraisingEvent(eventId);

        assertThrows(FundraisingEventDoesntExistException.class, () -> controller.unregisterCollectionBoxFromFundraisingEvent(eventId));

        verify(service).unregisterCollectionBoxFromFundraisingEvent(eventId);
    }

    @Test
    public void transferMoney_ShouldCallService() throws FundraisingEventException, ArgumentsException, CollectionBoxException {
        UUID eventId = sampleEvent.getUuid();

        controller.transferMoney(eventId);

        verify(service).transferMoney(eventId);
    }

    @Test
    public void transferMoney_ShouldThrowException_WhenEventNotFound() throws FundraisingEventException, ArgumentsException, CollectionBoxException {
        UUID eventId = UUID.randomUUID();
        doThrow(FundraisingEventDoesntExistException.class).when(service).transferMoney(eventId);

        assertThrows(FundraisingEventDoesntExistException.class, () -> controller.transferMoney(eventId));

        verify(service).transferMoney(eventId);
    }
}
