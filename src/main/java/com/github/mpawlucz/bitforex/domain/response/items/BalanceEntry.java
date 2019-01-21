package com.github.mpawlucz.bitforex.domain.response.items;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BalanceEntry {

    @SerializedName("currency")
    private String currency;

    @SerializedName("fix")
    private BigDecimal total;

    @SerializedName("frozen")
    private BigDecimal frozen;

    @SerializedName("active")
    private BigDecimal available;

}
