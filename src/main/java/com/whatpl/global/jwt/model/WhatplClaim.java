package com.whatpl.global.jwt.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WhatplClaim {

    NICKNAME("nick"),
    HAS_PROFILE("hpf"),
    JOB("job"),
    CAREER("car");

    private final String key;
}
