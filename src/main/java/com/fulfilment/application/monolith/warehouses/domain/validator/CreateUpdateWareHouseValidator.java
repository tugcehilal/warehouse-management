package com.fulfilment.application.monolith.warehouses.domain.validator;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;


public interface CreateUpdateWareHouseValidator {
    boolean validate(Warehouse warehouse);
}
