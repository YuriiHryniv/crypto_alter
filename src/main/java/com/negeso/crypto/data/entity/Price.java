package com.negeso.crypto.data.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@IdClass(PriceId.class)
@Getter
@Setter
@NoArgsConstructor
@Table(name = "prices")
public class Price {
    @Id
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "code_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "code_key"))
    private Code code;

    @Id

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "val")
    private String val;

}
