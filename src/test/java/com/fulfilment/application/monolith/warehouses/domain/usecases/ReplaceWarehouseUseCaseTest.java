package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReplaceWarehouseUseCaseTest {


    @Mock
    private WarehouseStore warehouseStore;

    @Mock
    private ArchiveWarehouseOperation archiveWarehouseOperation;

    @InjectMocks
    private ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    @DisplayName("Test warehouse replacement with valid conditions")
    public void testWarehouseReplacementWithValidConditions() {
        Warehouse existingWarehouse = new Warehouse("WH100", "EINDHOVEN001", 200, 80, LocalDateTime.now(), null);
        Warehouse newWarehouse = new Warehouse("WH100", "EINDHOVEN001", 250, 80, null, null);

        when(warehouseStore.findByBusinessUnitCode("WH100")).thenReturn(existingWarehouse);

        replaceWarehouseUseCase.replace(newWarehouse);

        verify(warehouseStore, times(1)).create(newWarehouse);
        verify(archiveWarehouseOperation, times(1)).archive(existingWarehouse);
    }

    @Test
    @DisplayName("Throw exception when warehouse is not found")
    public void throwExceptionWhenWarehouseNotFound() {
        Warehouse newWarehouse = new Warehouse("WH101", "ROTTERDAM001", 300, 100, null, null);

        when(warehouseStore.findByBusinessUnitCode("WH101")).thenThrow(WarehouseException.class);

        assertThrows(WarehouseException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }

    @Test
    @DisplayName("Throw exception when attempting to replace archived warehouse")
    public void throwExceptionWhenReplacingArchivedWarehouse() {
        Warehouse existingWarehouse = new Warehouse("WH102", "UTRECHT001", 150, 60, LocalDateTime.now(), LocalDateTime.now());
        Warehouse newWarehouse = new Warehouse("WH102", "UTRECHT001", 180, 60, null, null);
        when(warehouseStore.findByBusinessUnitCode("WH102")).thenReturn(existingWarehouse);
        assertThrows(WarehouseException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }

    @Test
    @DisplayName("Throw exception when new warehouse capacity is insufficient")
    public void throwExceptionWhenNewWarehouseCapacityIsInsufficient() {
        Warehouse existingWarehouse = new Warehouse("WH103", "GRONINGEN001", 50, 30, LocalDateTime.now(), null);
        Warehouse newWarehouse = new Warehouse("WH103", "GRONINGEN001", 80, 20, null, null);

        when(warehouseStore.findByBusinessUnitCode("WH103")).thenReturn(existingWarehouse);

        assertThrows(WarehouseException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }

    @Test
    @DisplayName("Throw exception when new warehouse stock does not match existing stock")
    public void throwExceptionWhenStockMismatch() {
        Warehouse existingWarehouse = new Warehouse("WH104", "DENHAAG001", 130, 70, LocalDateTime.now(), null);
        Warehouse newWarehouse = new Warehouse("WH104", "DENHAAG001", 160, 60, null, null);

        when(warehouseStore.findByBusinessUnitCode("WH104")).thenReturn(existingWarehouse);

        assertThrows(WarehouseException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }


}
