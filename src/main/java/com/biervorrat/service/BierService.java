package com.biervorrat.service;

import com.biervorrat.dto.BierDTO;
import com.biervorrat.entity.Bier;
import com.biervorrat.exception.BierAlreadyRegisteredException;
import com.biervorrat.exception.BierNotFoundException;
import com.biervorrat.exception.BierStockExceededException;
import com.biervorrat.mapper.BierMapper;
import com.biervorrat.repository.BierRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BierService {

    private final BierRepository bierRepository;
    private final BierMapper bierMapper = BierMapper.INSTANCE;

    public BierDTO createBier(BierDTO bierDTO) throws BierAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(bierDTO.getName());
        Bier bier = bierMapper.toModel(bierDTO);
        Bier savedBier = bierRepository.save(bier);
        return bierMapper.toDTO(savedBier);
    }

    public BierDTO findByName(String name) throws BierNotFoundException {
        Bier foundBier = bierRepository.findByName(name)
                .orElseThrow(() -> new BierNotFoundException(name));
        return bierMapper.toDTO(foundBier);
    }

    public List<BierDTO> listAll() {
        return bierRepository.findAll()
                .stream()
                .map(bierMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BierNotFoundException {
        verifyIfExists(id);
        bierRepository.deleteById(id);
    }

    public void verifyIfIsAlreadyRegistered(String name) throws BierAlreadyRegisteredException {
        Optional<Bier> optSavedBier = bierRepository.findByName(name);
        if(optSavedBier.isPresent()) {
            throw new BierAlreadyRegisteredException(name);
        }
    }

    private Bier verifyIfExists(Long id) throws BierNotFoundException {
        return bierRepository.findById(id)
                .orElseThrow(() ->new BierNotFoundException(id));
    }

    public BierDTO increment(Long id, int quantityToIncrement) throws BierNotFoundException, BierStockExceededException {
        Bier bierToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + bierToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= bierToIncrementStock.getMax()) {
            bierToIncrementStock.setQuantity(bierToIncrementStock.getQuantity() + quantityToIncrement);
            Bier incrementedBeerStock = bierRepository.save(bierToIncrementStock);
            return bierMapper.toDTO(incrementedBeerStock);
        }
        throw new BierStockExceededException(id, quantityToIncrement);
    }

    public BierDTO decrement(Long id, int quantityToDecrement) throws BierNotFoundException, BierStockExceededException {
        Bier beerToDecrementStock = verifyIfExists(id);
        int beerStockAfterDecremented = beerToDecrementStock.getQuantity() - quantityToDecrement;
        if (beerStockAfterDecremented >= 0) {
            beerToDecrementStock.setQuantity(beerStockAfterDecremented);
            Bier decrementedBeerStock = bierRepository.save(beerToDecrementStock);
            return bierMapper.toDTO(decrementedBeerStock);
        }
        throw new BierStockExceededException(id, quantityToDecrement);
    }
}
