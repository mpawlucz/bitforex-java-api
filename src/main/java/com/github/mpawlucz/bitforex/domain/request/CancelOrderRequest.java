package com.github.mpawlucz.bitforex.domain.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelOrderRequest {
    private Long orderId;
    private String base;
    private String quote;
}
