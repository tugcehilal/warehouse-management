package com.fulfilment.application.monolith.warehouses.domain.usecases.validators;
import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.validator.CapacityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class CapacityValidatorTest {


    @Mock
    private LocationResolver locationResolver;

    private CapacityValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new CapacityValidator(locationResolver);
    }

    @Test
    public void shouldReturnTrue_WhenWarehouseCapacityIsLessThanLocationMaxCapacity() {
        // Arrange
        Warehouse warehouse = new Warehouse();
        warehouse.setCapacity(1000);
        warehouse.setLocation("Zaandam");

        Location location = new Location();
        location.setMaxCapacity(1001);

        when(locationResolver.resolveByIdentifier(warehouse.getLocation())).thenReturn(location);


        boolean result = validator.validate(warehouse);

        assertTrue(result);
    }

    @Test
    public void shouldThrowException_WhenWarehouseCapacityIsGreaterThanLocationMaxCapacity() {

        Warehouse warehouse = new Warehouse();
        warehouse.setCapacity(1000);
        warehouse.setLocation("Zaandam");

        Location location = new Location();
        location.setMaxCapacity(999);

        when(locationResolver.resolveByIdentifier(warehouse.getLocation())).thenReturn(location);


        assertThrows(WarehouseException.class, () -> validator.validate(warehouse));
    }
}
