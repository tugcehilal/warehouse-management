package com.fulfilment.application.monolith.warehouses.domain.usecases.validatiors;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.validator.LocationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class LocationValidatorTest {

    @Mock
    private LocationResolver locationResolver;

    private LocationValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new LocationValidator(locationResolver);
    }

    @Test
    public void shouldReturnTrue_WhenLocationExists() {
        // Arrange
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("Amsterdam");

        when(locationResolver.resolveByIdentifier(warehouse.getLocation())).thenReturn(new Location());

        // Act
        boolean result = validator.validate(warehouse);

        // Assert
        assertTrue(result);
    }
}
