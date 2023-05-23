package uz.md.shopapp.dtos.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import uz.md.shopapp.utils.AppConstants;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeLoginDTO {

    @NotBlank(message = "PhoneNumber can't be empty")
    @Pattern(regexp = AppConstants.PHONE_NUMBER_REGEX)
    private String phoneNumber;

    @NotBlank(message = "Password can't be empty")
    private String password;

}
