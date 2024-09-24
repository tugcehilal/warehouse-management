package com.fulfilment.application.monolith.warehouses.domain.usecases.validators;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validator.BuCodeValidator;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;



import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class BuCodeValidatorTest {

    @Mock
    private WarehouseStore warehouseStore;

    private BuCodeValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new BuCodeValidator(warehouseStore);
    }

    @Test
    public void shouldThrowWebApplicationException_WhenWarehouseExists() {
        // Arrange
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("001");

        when(warehouseStore.findByBusinessUnitCode(warehouse.getBusinessUnitCode())).thenReturn(new Warehouse());

        // Act & Assert
        assertThrows(WebApplicationException.class, () -> validator.validate(warehouse));
    }


}
