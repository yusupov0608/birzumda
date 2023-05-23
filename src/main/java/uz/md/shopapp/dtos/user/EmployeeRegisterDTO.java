package uz.md.shopapp.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.md.shopapp.utils.AppConstants;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeRegisterDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Phone Number cannot be blank")
    @Pattern(regexp = AppConstants.PHONE_NUMBER_REGEX)
    private String phoneNumber;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotNull(message = "Role cannot be null")
    private Integer roleId;

}
