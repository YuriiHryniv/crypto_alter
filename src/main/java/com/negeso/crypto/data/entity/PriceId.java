package com.negeso.crypto.data.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Setter
@EqualsAndHashCode
@ToString
public class PriceId implements Serializable {
    private Long code;

    private LocalDateTime time;
}
