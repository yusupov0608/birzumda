package uz.md.shopapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import uz.md.shopapp.domain.User;
import uz.md.shopapp.dtos.user.ClientMeDto;
import uz.md.shopapp.dtos.user.ClientRegisterDTO;
import uz.md.shopapp.dtos.user.EmployeeRegisterDTO;
import uz.md.shopapp.dtos.user.UserDTO;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper extends EntityMapper<User, UserDTO> {

    @Override
    @Mapping(target = "permissions", source = "role.permissions")
    UserDTO toDTO(User entity);

    User fromClientAddDTO(ClientRegisterDTO dto);

    User fromEmployeeAddDTO(EmployeeRegisterDTO dto);

    ClientMeDto toClientMeDTO(User user);

}
