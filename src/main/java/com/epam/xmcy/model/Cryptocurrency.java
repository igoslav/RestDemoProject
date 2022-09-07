package com.epam.xmcy.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Class represents basic info of particular cryptocurrency.
 */
@Getter
@Setter
public class Cryptocurrency {

    private String name;

    private Double min;

    private Double max;

    private Double normal;

    private LocalDateTime oldest;

    private LocalDateTime newest;
}
