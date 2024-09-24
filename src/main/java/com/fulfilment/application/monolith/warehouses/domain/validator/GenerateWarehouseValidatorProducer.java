package com.fulfilment.application.monolith.warehouses.domain.validator;

import jakarta.enterprise.inject.Produces;

import java.util.List;

public interface GenerateWarehouseValidatorProducer {
    @Produces
    public List<CreateUpdateWareHouseValidator> produceCreateWarehouseValidators(ExistsByBuCodeValidator existsByBuCodeValidator,
                                                                           LocationValidator locationValidator,
                                                                           FeasibilityValidator feasibilityValidator,
                                                                           CapacityByLocationValidator capacityAndStockValidator) {
        return List.of(existsByBuCodeValidator, locationValidator, feasibilityValidator, capacityAndStockValidator);
    }
}
