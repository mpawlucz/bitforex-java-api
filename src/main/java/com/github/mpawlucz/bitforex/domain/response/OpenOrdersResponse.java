package com.github.mpawlucz.bitforex.domain.response;

import com.github.mpawlucz.bitforex.domain.response.items.OpenOrderEntry;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpenOrdersResponse {

    @SerializedName("success")
    private Boolean success;

    @SerializedName("code")
    private String code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<OpenOrderEntry> data;

}
