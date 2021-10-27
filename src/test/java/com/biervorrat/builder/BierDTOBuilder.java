package com.biervorrat.builder;

import com.biervorrat.dto.BierDTO;
import com.biervorrat.enums.BierType;
import lombok.Builder;

@Builder
public class BierDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Brahma";

    @Builder.Default
    private String brand = "Ambev";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private BierType type = BierType.LAGER;

    public BierDTO toBierDTO() {
        return new BierDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
