package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import jakarta.inject.Inject;
import com.fulfilment.application.monolith.mapper.WarehouseMapper;
import com.fulfilment.application.monolith.exceptions.ErrorRule;
import com.fulfilment.application.monolith.exceptions.WarehouseException;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Inject
  private WarehouseMapper warehouseMapper;

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(entity -> warehouseMapper.toModel(entity)).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    var warehouseEntity = warehouseMapper.toEntity(warehouse);
    this.persist(warehouseEntity);
  }

  @Override
  public void update(Warehouse warehouse) {
    DbWarehouse warehouseEntity = find("businessUnitCode", warehouse.getBusinessUnitCode()).firstResult();
    if (warehouseEntity == null) {
      throw new WarehouseException(ErrorRule.WAREHOUSE_NOT_LOCATED, "No warehouse found with the provided businessUnitCode");
    }

    warehouseEntity.setLocation(warehouse.getLocation());
    warehouseEntity.setCapacity(warehouse.getCapacity());
    warehouseEntity.setStock(warehouse.getStock());
    this.persist(warehouseEntity);
  }

  @Override
  public void remove(Warehouse warehouse) {
    DbWarehouse warehouseEntity = find("businessUnitCode", warehouse.getBusinessUnitCode()).firstResult();
    if (warehouseEntity == null) {
      throw new WarehouseException(ErrorRule.WAREHOUSE_NOT_LOCATED, "No warehouse found with the provided businessUnitCode");
    }
    this.delete(warehouseEntity);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse warehouseEntity = find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
    if (warehouseEntity == null) {
      throw new WarehouseException(ErrorRule.WAREHOUSE_NOT_LOCATED, "No warehouse found with the provided businessUnitCode");
    }
    return warehouseMapper.toModel(warehouseEntity);
  }

  @Override
  public DbWarehouse findById(Long id) {
    DbWarehouse warehouseEntity = find("id", id).firstResult();
    if (warehouseEntity == null) {
      throw new WarehouseException(ErrorRule.WAREHOUSE_NOT_LOCATED, "No warehouse found with the provided id");
    }
    return warehouseEntity;
  }
}
