package com.biervorrat.service;

import com.biervorrat.builder.BierDTOBuilder;
import com.biervorrat.dto.BierDTO;
import com.biervorrat.entity.Bier;
import com.biervorrat.exception.BierAlreadyRegisteredException;
import com.biervorrat.exception.BierNotFoundException;
import com.biervorrat.exception.BierStockExceededException;
import com.biervorrat.mapper.BierMapper;
import com.biervorrat.repository.BierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BierServiceTest {

    private static final long INVALID_BEER_ID = 1L;
    private final BierMapper bierMapper = BierMapper.INSTANCE;
    @Mock
    private BierRepository bierRepository;
    @InjectMocks
    private BierService bierService;

    @Test
    void whenNewBierInformedThenShouldBeCreated() throws BierAlreadyRegisteredException {
        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier expectedSavedBier = bierMapper.toModel(bierDTO);

        when(bierRepository.findByName(bierDTO.getName())).thenReturn(Optional.empty());
        when(bierRepository.save(expectedSavedBier)).thenReturn(expectedSavedBier);

        BierDTO createdBierDTO = bierService.createBier(bierDTO);

        assertThat(createdBierDTO.getId(), is(equalTo(bierDTO.getId())));
        assertThat(createdBierDTO.getName(), is(equalTo(bierDTO.getName())));
        assertThat(createdBierDTO.getId(), is(equalTo(bierDTO.getId())));
    }

    @Test
    void whenAlreadyRegisteredBierInformedThenAnExceptionShouldBeThrown() {
        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier duplicatedBier = bierMapper.toModel(bierDTO);

        when(bierRepository.findByName(bierDTO.getName())).thenReturn(Optional.of(duplicatedBier));

        assertThrows(BierAlreadyRegisteredException.class, () -> bierService.createBier(bierDTO));
    }

    @Test
    void whenValidBierNameIsGivenThenReturnABier() throws BierNotFoundException {
        BierDTO expectedBierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier expectedFoundBier = bierMapper.toModel(expectedBierDTO);

        when(bierRepository.findByName(expectedBierDTO.getName())).thenReturn(Optional.of(expectedFoundBier));

        BierDTO foundBierDTO = bierService.findByName(expectedBierDTO.getName());

        assertThat(foundBierDTO, is(equalTo(expectedBierDTO)));
    }

    @Test
    void whenNotRegisteredBierNameIsGivenThenThrowAnException() {
        BierDTO expectedBierDTO = BierDTOBuilder.builder().build().toBierDTO();

        when(bierRepository.findByName(expectedBierDTO.getName())).thenReturn(Optional.empty());

        assertThrows(BierNotFoundException.class, () -> bierService.findByName(expectedBierDTO.getName()));
    }

    @Test
    void whenListBierIsCalledThenReturnAListOfBiers() {
        BierDTO expectedBierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier expectedFoundBier = bierMapper.toModel(expectedBierDTO);

        when(bierRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBier));

        List<BierDTO> foundBierDTO = bierService.listAll();

        assertThat(foundBierDTO, is(not(empty())));
        assertThat(foundBierDTO.get(0), is(equalTo(expectedBierDTO)));
    }

    @Test
    void whenListBierIsCalledThenReturnAnEmptyList() {
        when(bierRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<BierDTO> foundBierDTO = bierService.listAll();

        assertThat(foundBierDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenABierShouldBeDeleted() throws BierNotFoundException {
        BierDTO expectedExcludedBierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier expectedExcludedBier = bierMapper.toModel(expectedExcludedBierDTO);

        when(bierRepository.findById(expectedExcludedBierDTO.getId())).thenReturn(Optional.of(expectedExcludedBier));
        doNothing().when(bierRepository).deleteById(expectedExcludedBier.getId());

        bierService.deleteById(expectedExcludedBierDTO.getId());

        verify(bierRepository, times(1)).findById(expectedExcludedBierDTO.getId());
        verify(bierRepository, times(1)).deleteById(expectedExcludedBierDTO.getId());
    }

    @Test
    void whenExclusionIsCalledWithInvalidIdThenExceptionShouldBeThrown() {
        when(bierRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BierNotFoundException.class, () -> bierService.deleteById(INVALID_BEER_ID));
    }

    @Test
    void whenIncrementIsCalledThenIncrementBierStock() throws BierNotFoundException, BierStockExceededException {
        BierDTO expectedBierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier expectedBier = bierMapper.toModel(expectedBierDTO);

        when(bierRepository.findById(expectedBierDTO.getId())).thenReturn(Optional.of(expectedBier));
        when(bierRepository.save(expectedBier)).thenReturn(expectedBier);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedBierDTO.getQuantity() + quantityToIncrement;
        BierDTO incrementedBierDTO = bierService.increment(expectedBierDTO.getId(), quantityToIncrement);

        assertThat(incrementedBierDTO.getQuantity(), is(equalTo(expectedQuantityAfterIncrement)));
        assertThat(expectedBierDTO.getMax(), is(greaterThan(expectedQuantityAfterIncrement)));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        BierDTO expectedBierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier expectedBier = bierMapper.toModel(expectedBierDTO);

        when(bierRepository.findById(expectedBierDTO.getId())).thenReturn(Optional.of(expectedBier));

        int quantityToIncrement = 80;
        assertThrows(BierStockExceededException.class, () -> bierService.increment(expectedBierDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(bierRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BierNotFoundException.class, () -> bierService.increment(INVALID_BEER_ID, quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledThenDecrementBierStock() throws BierNotFoundException, BierStockExceededException {
        BierDTO expectedBierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier expectedBier = bierMapper.toModel(expectedBierDTO);

        when(bierRepository.findById(expectedBierDTO.getId())).thenReturn(Optional.of(expectedBier));
        when(bierRepository.save(expectedBier)).thenReturn(expectedBier);

        int quantityToDecrement = 5;
        int expectedQuantityAfterDecrement = expectedBierDTO.getQuantity() - quantityToDecrement;
        BierDTO incrementedBierDTO = bierService.decrement(expectedBierDTO.getId(), quantityToDecrement);

        assertThat(incrementedBierDTO.getQuantity(), is(equalTo(expectedQuantityAfterDecrement)));
        assertThat(expectedQuantityAfterDecrement, is(greaterThan(0)));
    }

    @Test
    void whenDecrementIsCalledToEmptyStockThenEmptyBierStock() throws BierNotFoundException, BierStockExceededException {
        BierDTO expectedBierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier expectedBier = bierMapper.toModel(expectedBierDTO);

        when(bierRepository.findById(expectedBierDTO.getId())).thenReturn(Optional.of(expectedBier));
        when(bierRepository.save(expectedBier)).thenReturn(expectedBier);

        int quantityToDecrement = 10;
        int expectedQuantityAfterDecrement = expectedBierDTO.getQuantity() - quantityToDecrement;
        BierDTO incrementedBierDTO = bierService.decrement(expectedBierDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, is(equalTo(0)));
        assertThat(expectedQuantityAfterDecrement, is(equalTo(incrementedBierDTO.getQuantity())));
    }

    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {
        BierDTO expectedBierDTO = BierDTOBuilder.builder().build().toBierDTO();
        Bier expectedBier = bierMapper.toModel(expectedBierDTO);

        when(bierRepository.findById(expectedBierDTO.getId())).thenReturn(Optional.of(expectedBier));

        int quantityToDecrement = 80;
        assertThrows(BierStockExceededException.class, () -> bierService.decrement(expectedBierDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 10;

        when(bierRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BierNotFoundException.class, () -> bierService.decrement(INVALID_BEER_ID, quantityToDecrement));
    }
}
