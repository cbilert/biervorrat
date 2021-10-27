package com.biervorrat.controller;

import com.biervorrat.dto.BierDTO;
import com.biervorrat.dto.QuantityDTO;
import com.biervorrat.exception.BierAlreadyRegisteredException;
import com.biervorrat.exception.BierNotFoundException;
import com.biervorrat.exception.BierStockExceededException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api("Manages bier stock")
public interface BierControllerDocs {

    @ApiOperation(value = "Bier creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success bier creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    BierDTO createBier(BierDTO bierDTO) throws BierAlreadyRegisteredException;

    @ApiOperation(value = "Returns bier found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success bier found in the system"),
            @ApiResponse(code = 404, message = "Bier with given name not found.")
    })
    BierDTO findByName(@PathVariable String name) throws BierNotFoundException;

    @ApiOperation(value = "Returns a list of all biers registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all biers registered in the system"),
    })
    List<BierDTO> listBiers();

    @ApiOperation(value = "Delete a bier found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success bier deleted in the system"),
            @ApiResponse(code = 404, message = "Bier with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws BierNotFoundException;


    @ApiOperation(value = "Decrement bier by a given id quantity in a stock")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success bier decremented in stock"),
            @ApiResponse(code = 400, message = "Bier not successfully increment in stock"),
            @ApiResponse(code = 404, message = "Bier with given id not found.")
    })
    BierDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws BierNotFoundException, BierStockExceededException;
}
