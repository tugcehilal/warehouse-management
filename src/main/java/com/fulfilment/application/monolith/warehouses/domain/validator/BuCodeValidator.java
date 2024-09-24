package com.fulfilment.application.monolith.warehouses.domain.validator;

import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import lombok.RequiredArgsConstructor;

import static com.fulfilment.application.monolith.exceptions.ErrorRule.WAREHOUSE_NOT_LOCATED;

@ApplicationScoped
@RequiredArgsConstructor
public class BuCodeValidator implements CreateUpdateWareHouseValidator {

    private final WarehouseStore warehouseStore;

    @Override
    public boolean validate(Warehouse warehouse) {
        try {
            Warehouse record = warehouseStore.findByBusinessUnitCode(warehouse.getBusinessUnitCode());
            if (record != null) {
                throw new WebApplicationException("Warehouse with Business Unit Code " + warehouse.getBusinessUnitCode() + " already exists", 409);
            }
        } catch (WarehouseException warehouseException) {
            if (warehouseException.getErrorRule().equals(WAREHOUSE_NOT_LOCATED)) {
                return true;
            }else {
                throw warehouseException;
            }
        }
        return false;
    }
}
