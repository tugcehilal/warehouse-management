package com.fulfilment.application.monolith.warehouses.domain.validator;

import jakarta.enterprise.inject.Produces;

import java.util.List;

public class GenerateWarehouseValidator {
    @Produces
    public List<CreateUpdateWareHouseValidator> produceCreateWarehouseValidators(BuCodeValidator buCodeValidator,
                                                                                 WarehouseCapacityValidator locationValidator,

                                                                           CapacityValidator capacityAndStockValidator) {
        return List.of(buCodeValidator, locationValidator, capacityAndStockValidator);
    }
}
