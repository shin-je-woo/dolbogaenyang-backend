package com.whatpl.global.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WhatplClaim {

    NICKNAME("nick");

    private final String key;
}
