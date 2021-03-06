package com.github.mpawlucz.bitforex.domain.response;

import com.github.mpawlucz.bitforex.domain.response.items.BalanceEntry;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BalanceResponse {

    @SerializedName("success")
    private Boolean success;

    @SerializedName("code")
    private String code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<BalanceEntry> data;

}
