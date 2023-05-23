package uz.md.shopapp.dtos.order;


import lombok.*;
import uz.md.shopapp.domain.enums.OrderStatus;
import uz.md.shopapp.dtos.institution.LocationDto;
import uz.md.shopapp.dtos.orderProduct.OrderProductDTO;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDTO {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private Long overallPrice;
    private Long deliveryPrice;
    private LocalDateTime deliveryTime;
    private LocationDto location;
    private Long institutionId;
    private List<OrderProductDTO> orderProducts;
}
