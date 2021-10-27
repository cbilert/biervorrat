package com.biervorrat.controller;

import com.biervorrat.builder.BierDTOBuilder;
import com.biervorrat.dto.BierDTO;
import com.biervorrat.dto.QuantityDTO;
import com.biervorrat.exception.BierNotFoundException;
import com.biervorrat.exception.BierStockExceededException;
import com.biervorrat.service.BierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static com.biervorrat.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.Collections;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BierControllerTest {

    private static final String BEER_API_URL_PATH = "/api/v1/biere";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2l;
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BierService bierService;

    @InjectMocks
    private BierController bierController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bierController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenABierIsCreated() throws Exception {
        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();

        when(bierService.createBier(bierDTO)).thenReturn(bierDTO);

        mockMvc.perform(post(BEER_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bierDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(bierDTO.getName())))
                .andExpect(jsonPath("$.brand", is(bierDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(bierDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithouRequiredFieldThenAnErrorIsReturned() throws Exception {
        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();
        bierDTO.setName(null);

        mockMvc.perform(post(BEER_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bierDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();

        when(bierService.findByName(bierDTO.getName())).thenReturn(bierDTO);

        mockMvc.perform(get(BEER_API_URL_PATH + "/" + bierDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(bierDTO.getName())))
                .andExpect(jsonPath("$.brand", is(bierDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(bierDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithNotRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();

        when(bierService.findByName(bierDTO.getName())).thenThrow(BierNotFoundException.class);

        mockMvc.perform(get(BEER_API_URL_PATH + "/" + bierDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithBiersIsCalledThenOkStatusIsReturned() throws Exception {
        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();

        when(bierService.listAll()).thenReturn(Collections.singletonList(bierDTO));

        mockMvc.perform(get(BEER_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(bierDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(bierDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(bierDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutBiersIsCalledThenOkStatusIsReturned() throws Exception {
        when(bierService.listAll()).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(get(BEER_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        doNothing().when(bierService).deleteById(VALID_BEER_ID);

        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + VALID_BEER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bierService, times(1)).deleteById(VALID_BEER_ID);
    }

    @Test
    void whenDELETEIsCalledWithoutValidIdThenNotFoundStatusIsReturned() throws Exception {
        doThrow(BierNotFoundException.class).when(bierService).deleteById(INVALID_BEER_ID);

        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();
        bierDTO.setQuantity(bierDTO.getQuantity() + quantityDTO.getQuantity());

        when(bierService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(bierDTO);

        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(bierDTO.getName())))
                .andExpect(jsonPath("$.brand", is(bierDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(bierDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(bierDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();
        bierDTO.setQuantity(bierDTO.getQuantity() + quantityDTO.getQuantity());

        when(bierService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BierStockExceededException.class);

        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidBierIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        when(bierService.increment(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BierNotFoundException.class);
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();
        bierDTO.setQuantity(bierDTO.getQuantity() + quantityDTO.getQuantity());

        when(bierService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(bierDTO);

        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(bierDTO.getName())))
                .andExpect(jsonPath("$.brand", is(bierDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(bierDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(bierDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDEcrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(60)
                .build();

        BierDTO bierDTO = BierDTOBuilder.builder().build().toBierDTO();
        bierDTO.setQuantity(bierDTO.getQuantity() + quantityDTO.getQuantity());

        when(bierService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BierStockExceededException.class);

        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidBierIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        when(bierService.decrement(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BierNotFoundException.class);
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }
}
