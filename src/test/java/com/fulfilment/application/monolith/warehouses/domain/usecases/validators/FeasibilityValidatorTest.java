package com.fulfilment.application.monolith.warehouses.domain.usecases.validators;
import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validator.WarehouseCapacityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class FeasibilityValidatorTest {

    @Mock
    private LocationResolver locationResolver;

    @Mock
    private WarehouseStore warehouseStore;

    private WarehouseCapacityValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new WarehouseCapacityValidator(locationResolver, warehouseStore);
    }

    @Test
    public void shouldReturnTrue_WhenWarehouseCreationIsFeasible() {

        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("AMSTERDAM");

        Location location = new Location();
        location.setMaxNumberOfWarehouses(2);

        when(locationResolver.resolveByIdentifier(warehouse.getLocation())).thenReturn(location);
        when(warehouseStore.getAll()).thenReturn(Collections.emptyList());

        boolean result = validator.validate(warehouse);
        assertTrue(result);
    }



}
