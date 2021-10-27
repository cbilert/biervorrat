package com.biervorrat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BierType {
    LAGER("lager"),
    MALZBIER("malzbier"),
    WITBIER("witbier"),
    WEISS("weiss"),
    ALE("ale"),
    IPA("ipa"),
    STOUT("stout");

    private final String description;
}
