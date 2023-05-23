package uz.md.shopapp.service.contract;

import org.springframework.security.core.userdetails.UserDetailsService;
import uz.md.shopapp.dtos.ApiResult;
import uz.md.shopapp.dtos.TokenDTO;
import uz.md.shopapp.dtos.user.ClientLoginDTO;
import uz.md.shopapp.dtos.user.EmployeeLoginDTO;
import uz.md.shopapp.dtos.user.EmployeeRegisterDTO;

public interface AuthService extends UserDetailsService {

    ApiResult<Void> registerClient(String phoneNumber);

    ApiResult<String> getSMSCode(String phoneNumber);

    ApiResult<TokenDTO> loginClient(ClientLoginDTO loginDTO);

    ApiResult<TokenDTO> loginEmployee(EmployeeLoginDTO loginDTO);

    ApiResult<Void> registerEmployee(EmployeeRegisterDTO dto);
}
