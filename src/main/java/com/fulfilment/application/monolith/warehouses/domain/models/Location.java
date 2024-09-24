package com.fulfilment.application.monolith.warehouses.domain.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Location {
  public String identification;

  // maximum number of warehouses that can be created in this location
  public int maxNumberOfWarehouses;

  // maximum capacity of the location summing all the warehouse capacities
  public int maxCapacity;

  public Location(String identification, int maxNumberOfWarehouses, int maxCapacity) {
    this.identification = identification;
    this.maxNumberOfWarehouses = maxNumberOfWarehouses;
    this.maxCapacity = maxCapacity;
  }
}
