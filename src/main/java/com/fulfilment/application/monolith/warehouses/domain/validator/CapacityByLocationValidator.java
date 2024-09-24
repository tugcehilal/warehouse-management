package com.fulfilment.application.monolith.warehouses.domain.validator;



import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import static com.fulfilment.application.monolith.exceptions.ErrorRule.LOCATION_CAPACITY_EXCEEDED;


@ApplicationScoped
@RequiredArgsConstructor
public class CapacityByLocationValidator implements CreateUpdateWareHouseValidator {
    private final LocationResolver locationResolver;

    @Override
    public boolean validate(Warehouse warehouse) {
        var location = locationResolver.resolveByIdentifier(warehouse.getLocation());
        if( location.getMaxCapacity() < warehouse.getCapacity()){
            throw new WarehouseException(LOCATION_CAPACITY_EXCEEDED);
        }else {
            return true;
        }
    }
}