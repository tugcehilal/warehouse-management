package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import static com.fulfilment.application.monolith.exceptions.ErrorRule.WAREHOUSE_PREVIOUSLY_ARCHIVED;
import static com.fulfilment.application.monolith.exceptions.ErrorRule.INSUFFICIENT_WAREHOUSE_CAPACITY;
import static com.fulfilment.application.monolith.exceptions.ErrorRule.WAREHOUSE_STOCK_MISMATCH;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {
  private final WarehouseStore warehouseStore;
  private final ArchiveWarehouseOperation archiveWarehouseOperation;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, ArchiveWarehouseOperation archiveWarehouseOperation) {
    this.warehouseStore = warehouseStore;
    this.archiveWarehouseOperation = archiveWarehouseOperation;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    Warehouse existingWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode());

    // Check if the existing warehouse exists and is not archived
    if (existingWarehouse.getArchivedAt() != null) {
      throw new WarehouseException(WAREHOUSE_PREVIOUSLY_ARCHIVED, "Warehouse with Business Unit Code " + newWarehouse.getBusinessUnitCode() + " is already archived");
    }

    // Validate if the new warehouse's capacity can accommodate the stock from the warehouse being replaced
    if (newWarehouse.getCapacity() < existingWarehouse.getStock()) {
      throw new WarehouseException(INSUFFICIENT_WAREHOUSE_CAPACITY);
    }

    // Confirm that the stock of the new warehouse matches the stock of the previous warehouse
    if (!newWarehouse.getStock().equals(existingWarehouse.getStock())) {
      throw new WarehouseException(WAREHOUSE_STOCK_MISMATCH);
    }

    //archive the existing warehouse.
    archiveWarehouseOperation.archive(existingWarehouse);

    // If all checks pass, replace the existing warehouse with the new one
    newWarehouse.setCreatedAt(existingWarehouse.getCreatedAt());
    newWarehouse.setArchivedAt(null);
    warehouseStore.create(newWarehouse);

  }
}
