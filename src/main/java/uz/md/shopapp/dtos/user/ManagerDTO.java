package uz.md.shopapp.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManagerDTO {

    private String firstName;

    private String lastName;

    private Long chatId;

    private String phoneNumber;

}
