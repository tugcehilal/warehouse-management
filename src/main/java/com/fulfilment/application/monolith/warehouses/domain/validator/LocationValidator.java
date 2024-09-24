package com.fulfilment.application.monolith.warehouses.domain.validator;


import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class LocationValidator  implements CreateUpdateWareHouseValidator {
    private final LocationResolver locationResolver;

    @Override
    public boolean validate(Warehouse warehouse) {
        locationResolver.resolveByIdentifier(warehouse.getLocation());
        return true;
    }
}