package com.freeder.buclserver.domain.consumerorder.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrackingNumDto {
    @NotEmpty
    private String orderCode;
    @NotEmpty
    private String trakingNum;
    @NotEmpty
    private String shippingCoName;

}
