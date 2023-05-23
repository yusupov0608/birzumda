package uz.md.shopapp.dtos.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import uz.md.shopapp.dtos.institution.LocationDto;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddressAddDTO {

    @NotNull(message = "Uy raqami kiritilishi shart")
    private Integer houseNumber;

    @NotNull(message = "Etaj kiritilishi shart")
    private Integer flat;

    @NotBlank(message = "Podyezd kiritilishi shart")
    private Integer entrance;

    private LocationDto location;

    @NotNull(message = "Foydalanuvchi idsi kiritilishi shart")
    private Long userId;
}
