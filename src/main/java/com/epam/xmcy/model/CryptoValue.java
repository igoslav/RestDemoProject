package com.epam.xmcy.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Object representation of crypto data for a particular moment of time.
 */
@Getter
@Setter
public class CryptoValue {

    private String name;

    private Double price;

    private LocalDateTime dateTime;
}
