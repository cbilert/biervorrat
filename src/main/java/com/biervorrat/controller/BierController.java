package com.biervorrat.controller;

import com.biervorrat.dto.BierDTO;
import com.biervorrat.dto.QuantityDTO;
import com.biervorrat.exception.BierAlreadyRegisteredException;
import com.biervorrat.exception.BierNotFoundException;
import com.biervorrat.exception.BierStockExceededException;
import com.biervorrat.service.BierService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/biere")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BierController implements BierControllerDocs {

    private final BierService bierService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BierDTO createBier(@RequestBody @Valid BierDTO bierDTO) throws BierAlreadyRegisteredException {
        return bierService.createBier(bierDTO);
    }

    @GetMapping("/{name}")
    public BierDTO findByName(@PathVariable String name) throws BierNotFoundException {
        return bierService.findByName(name);
    }

    @GetMapping
    public List<BierDTO> listBiers() {
        return bierService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws BierNotFoundException {
        bierService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public BierDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws BierNotFoundException, BierStockExceededException {
        return bierService.increment(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public BierDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws BierNotFoundException, BierStockExceededException {
        return bierService.decrement(id, quantityDTO.getQuantity());
    }
}
