package uz.md.shopapp.dtos.user;

import lombok.*;
import uz.md.shopapp.domain.enums.PermissionEnum;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<PermissionEnum> permissions;
}
