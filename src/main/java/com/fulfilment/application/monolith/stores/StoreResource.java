package com.fulfilment.application.monolith.stores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.annotation.Counted;
import java.util.concurrent.atomic.AtomicReference;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.micrometer.core.instrument.MeterRegistry;


@Path("store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

  @Inject private LegacyStoreManagerGateway legacyStoreManagerGateway;
  @Inject private MeterRegistry registry;

  private static final Logger LOGGER = Logger.getLogger(StoreResource.class.getName());

  @GET
  @Operation(summary = "List all available stores", description = "Provides a sorted list of stores, ordered by their name.")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Stores retrieved and listed in order",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = Store.class)))
  })
  public List<Store> get() {
    return Store.listAll(Sort.by("name"));
  }

  @GET
  @Path("{id}")
  @Operation(summary = "Find store by ID", description = "Retrieves a single store based on the provided store ID.")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Store successfully found",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = Store.class))),
          @APIResponse(responseCode = "404", description = "Store with the given ID not found")
  })
  public Store getSingle(
          @Parameter(description = "Unique ID of the store to retrieve", required = true) Long id) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id " + id + " does not exist.", 404);
    }
    return entity;
  }


  @POST
  @Transactional
  @Timed(value = "store_creation_duration", description = "Time taken to create a store")
  @Counted(value = "store_creation_counter", description = "Counts how many stores have been created")
  @Operation(summary = "Add a new store", description = "Handles the creation of a new store and notifies the legacy system.")
  @APIResponses({
          @APIResponse(responseCode = "201", description = "Store created successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = Store.class))),
          @APIResponse(responseCode = "422", description = "Invalid input: store ID should not be provided.")
  })
  public Response create(
          @Parameter(description = "Store object that needs to be created", required = true) Store store) {

    if (store.id != null) {
      throw new WebApplicationException("ID should not be included in the request.", 422);
    }

    var storeCopy = new AtomicReference<Store>();
    QuarkusTransaction.requiringNew().run(() -> {
      store.persist();
      // Create a detached copy of the store entity for legacy system processing.
      storeCopy.set(new Store(store));
    });

    try {
      // Notify the legacy system after successful persistence.
      legacyStoreManagerGateway.createStoreOnLegacySystem(storeCopy.get());
    } catch (RuntimeException e) {
      registry.counter("store_creation_legacy_errors").increment();
      LOGGER.error("Failed to notify legacy system for store: " + storeCopy.get(), e);
      throw e;
    }

    return Response.ok(storeCopy.get()).status(201).build();
  }


  @PUT
  @Path("{id}")
  @Transactional
  @Timed(value = "update_store_time", description = "Time taken to update store")
  @Counted(value = "update_store_count", description = "Number of stores updated")
  @Operation(summary = "Update an existing store", description = "Updates an existing store in the system by ID.")
  @APIResponses(value = {
          @APIResponse(responseCode = "200", description = "Store updated successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = Store.class))),
          @APIResponse(responseCode = "404", description = "Store not found"),
          @APIResponse(responseCode = "422", description = "Invalid input")
  })
  public Store update(
          @Parameter(description = "ID of the store to update", required = true) Long id,
          @Parameter(description = "Updated store object", required = true) Store updatedStore) {

    if (updatedStore.name == null) {
      throw new WebApplicationException("Store Name was not set on request.", 422);
    }

    var updatedStoreCopy = new AtomicReference<Store>();
    QuarkusTransaction.requiringNew().run(() -> {
      Store entity = Store.findById(id);

      if (entity == null) {
        throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
      }

      entity.name = updatedStore.name;
      entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
      // Copy the updated store to avoid exposing persistence-managed entities
      updatedStoreCopy.set(new Store(entity));
    });

    try {
      // Notify the legacy system after the database transaction is committed
      legacyStoreManagerGateway.updateStoreOnLegacySystem(updatedStoreCopy.get());
    } catch (RuntimeException e) {
      registry.counter("update_legacy_store_errors").count();
      LOGGER.warnv(e, "Failed to update Store in legacy system; Store {0}", updatedStoreCopy.get());
      throw e;
    }

    return updatedStoreCopy.get();
  }

  @PATCH
  @Path("{id}")
  @Transactional
  @Timed(value = "patch_store_duration", description = "Tracks the time taken to patch a store")
  @Counted(value = "patched_store_counter", description = "Counts how many stores have been patched")
  @Operation(summary = "Partially update a store", description = "Patches specific fields of an existing store by its ID.")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Store patched successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = Store.class))),
          @APIResponse(responseCode = "404", description = "Store not found"),
          @APIResponse(responseCode = "422", description = "Invalid input")
  })
  public Store patch(
          @Parameter(description = "ID of the store to patch", required = true) Long id,
          @Parameter(description = "Store object containing fields to patch", required = true) Store updatedStore) {

    if (updatedStore.name == null) {
      throw new WebApplicationException("Store Name was not set on the request.", 422);
    }

    var patchedStore = new AtomicReference<Store>();
    QuarkusTransaction.requiringNew().run(() -> {
      Store entity = Store.findById(id);

      if (entity == null) {
        throw new WebApplicationException("Store with ID " + id + " does not exist.", 404);
      }

      // Patch only the fields that are provided in the updatedStore object
      if (entity.name != null) {
        entity.name = updatedStore.name;
      }

      if (entity.quantityProductsInStock != 0) {
        entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
      }

      entity.persist();
      patchedStore.set(new Store(entity)); // Store a detached copy
    });

    try {
      // Notify the legacy system after successful transaction
      legacyStoreManagerGateway.updateStoreOnLegacySystem(patchedStore.get());
    } catch (RuntimeException e) {
      registry.counter("patch_legacy_store_errors").increment();
      LOGGER.warn("Failed to patch store in legacy system for Store ID: " + patchedStore.get(), e);
      throw e;
    }

    return patchedStore.get();
  }

  @DELETE
  @Path("{id}")
  @Transactional
  @Timed(value = "delete_store_duration", description = "Time taken to delete a store")
  @Counted(value = "deleted_store_counter", description = "Counts the number of stores deleted")
  @Operation(summary = "Delete a store by ID", description = "Deletes a store from the system by its unique ID.")
  @APIResponses({
          @APIResponse(responseCode = "204", description = "Store deleted successfully"),
          @APIResponse(responseCode = "404", description = "Store with the given ID not found")
  })
  public Response delete(
          @Parameter(description = "Unique ID of the store to delete", required = true) Long id) {

    Store entity = Store.findById(id);

    if (entity == null) {
      throw new WebApplicationException("Store with ID " + id + " does not exist.", 404);
    }

    entity.delete();

    return Response.status(204).build();
  }


  @Provider
  public static class ErrorMapper implements ExceptionMapper<Exception> {

    @Inject ObjectMapper objectMapper;

    @Override
    public Response toResponse(Exception exception) {
      LOGGER.error("Failed to handle request", exception);

      int code = 500;
      if (exception instanceof WebApplicationException) {
        code = ((WebApplicationException) exception).getResponse().getStatus();
      }

      ObjectNode exceptionJson = objectMapper.createObjectNode();
      exceptionJson.put("exceptionType", exception.getClass().getName());
      exceptionJson.put("code", code);

      if (exception.getMessage() != null) {
        exceptionJson.put("error", exception.getMessage());
      }

      return Response.status(code).entity(exceptionJson).build();
    }
  }
}
