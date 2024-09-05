package dev.starryeye.stockranker.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankTag {

    POPULAR,
    RISING,
    FALLING,
    VOLUME
}
