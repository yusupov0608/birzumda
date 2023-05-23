package uz.md.shopapp.dtos.order;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import uz.md.shopapp.dtos.address.AddressAddDTO;
import uz.md.shopapp.dtos.institution.LocationDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderAddDTO {

    @NotNull
    private AddressAddDTO address;

    private LocalDateTime deliveryTime = LocalDateTime.now();

    @NotNull(message = "ordered products must not be null")
    private List<OrderProductAddDTO> orderProducts;
}
