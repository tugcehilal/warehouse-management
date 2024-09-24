package com.fulfilment.application.monolith.mapper;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface WarehouseMapper {
    DbWarehouse toEntity(Warehouse warehouse);
    Warehouse toModel(DbWarehouse warehouse);
    Warehouse toModel(com.warehouse.api.beans.Warehouse warehouse);
}
