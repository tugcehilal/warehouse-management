package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.exceptions.WarehouseException;
import com.fulfilment.application.monolith.mapper.WarehouseMapper;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;

import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import com.fulfilment.application.monolith.exceptions.ErrorRule;

@RequestScoped
@RequiredArgsConstructor
public class WarehouseResourceImpl implements WarehouseResource {

  private final WarehouseRepository warehouseRepository;
  private final WarehouseMapper warehouseMapper;
  private final CreateWarehouseOperation createWarehouseOperation;
  private final ReplaceWarehouseOperation replaceWarehouseOperation;
  private final ArchiveWarehouseUseCase archiveWarehouseUseCase;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  @Transactional
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    requestValidation(data);
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse model = warehouseMapper.toModel(data);
    createWarehouseOperation.create(model);
    return data;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    DbWarehouse entity = warehouseRepository.findById(Long.parseLong(id));
    return toWarehouseResponse(warehouseMapper.toModel(entity));
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
    DbWarehouse entity = warehouseRepository.findById(Long.parseLong(id));
    archiveWarehouseUseCase.archive(warehouseMapper.toModel(entity));
  }

  @Override
  @Transactional
  public Warehouse replaceTheCurrentActiveWarehouse(String businessUnitCode, @NotNull Warehouse data) {
    if (businessUnitCode != null && !businessUnitCode.equals(data.getBusinessUnitCode())) {
      throw new WarehouseException(ErrorRule.BUSINESS_UNIT_CODE_MISMATCH, "Business Unit Code in path and in request body do not match.");
    }
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse newWarehouse = warehouseMapper.toModel(data);
    newWarehouse.setBusinessUnitCode(businessUnitCode);
    replaceWarehouseOperation.replace(newWarehouse);
    return data;
  }

  private Warehouse toWarehouseResponse(
          com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.getBusinessUnitCode());
    response.setLocation(warehouse.getLocation());
    response.setCapacity(warehouse.getCapacity());
    response.setStock(warehouse.getStock());
    return response;
  }

  private void requestValidation(Warehouse data) {
    if (data.getBusinessUnitCode() == null) {
      throw new WarehouseException(ErrorRule.MISSING_FIELD, "Business Unit Code was not set on request.");
    }
    if (data.getLocation() == null) {
      throw new WarehouseException(ErrorRule.MISSING_FIELD, "Location was not set on request.");
    }
    if (data.getCapacity() == null) {
      throw new WarehouseException(ErrorRule.MISSING_FIELD, "Capacity was not set on request.");
    }
    if (data.getStock() == null) {
      throw new WarehouseException(ErrorRule.MISSING_FIELD, "Stock  was not set on request.");
    }
  }
}
