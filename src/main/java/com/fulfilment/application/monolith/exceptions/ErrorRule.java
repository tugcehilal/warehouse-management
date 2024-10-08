package com.fulfilment.application.monolith.exceptions;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter

public enum ErrorRule {
    // 400
    MISSING_FIELD(Response.Status.BAD_REQUEST, "Required field is missing or empty", ErrorCode.FIELDISREQUIRED),
    MISSING_BODY(Response.Status.BAD_REQUEST, "Request body is not provided", ErrorCode.BODYMISSING),

    // 404
    WAREHOUSE_NOT_LOCATED(Response.Status.NOT_FOUND, "No warehouse found with the given BusinessUnitCode", ErrorCode.WAREHOUSENOTFOUND),


    // 422

    MAX_WAREHOUSES_LIMIT_REACHED(Response.Status.CONFLICT, "The maximum number of allowable warehouses has been reached", ErrorCode.MAXWAREHOUSENUMBERREACHED),
    BUSINESS_UNIT_CODE_MISMATCH(Response.Status.CONFLICT, "The provided BusinessUnitCode does not match the expected value", ErrorCode.BUSINESSUNITCODENOTMATCH),
    LOCATION_CAPACITY_EXCEEDED(Response.Status.CONFLICT, "The warehouse at the specified location has exceeded its maximum capacity", ErrorCode.WAREHOUSELOCATIONEXCEEDEDMAXCAPACITY),
    WAREHOUSE_PREVIOUSLY_ARCHIVED(Response.Status.CONFLICT, "The warehouse associated with the provided BusinessUnitCode has been archived earlier", ErrorCode.WAREHOUSEPREVIOUSLYARCHIVED),
    INSUFFICIENT_WAREHOUSE_CAPACITY(Response.Status.CONFLICT, "The capacity of the new warehouse is insufficient to store the stock from the previous warehouse", ErrorCode.INSUFFICIENTWAREHOUSECAPACITY),
    WAREHOUSE_STOCK_MISMATCH(Response.Status.CONFLICT, "The inventory in the new warehouse does not align with the stock in the previous warehouse", ErrorCode.WAREHOUSESTOCKMISMATCH);






    private final Response.Status httpStatus;
    private final String description;
    private final ErrorCode code;

    ErrorRule(Response.Status httpStatus, String description, ErrorCode code) {
        this.httpStatus = httpStatus;
        this.description = description;
        this.code = code;
    }
}
