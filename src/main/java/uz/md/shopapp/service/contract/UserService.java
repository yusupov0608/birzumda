package uz.md.shopapp.service.contract;

import uz.md.shopapp.dtos.ApiResult;
import uz.md.shopapp.dtos.user.ClientEditDTO;
import uz.md.shopapp.dtos.user.ClientMeDto;

public interface UserService {
    ApiResult<Void> delete(Long id);
}
