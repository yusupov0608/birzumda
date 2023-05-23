package uz.md.shopapp.dtos.address;

import lombok.*;
import uz.md.shopapp.dtos.institution.LocationDto;

@AllArgsConstructor

@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddressDTO {
    private Long id;
    private Integer houseNumber;
    private Integer flat;
    private Integer entrance;
    private Long userId;
    private LocationDto location;
}
