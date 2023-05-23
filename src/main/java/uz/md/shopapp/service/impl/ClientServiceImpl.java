package uz.md.shopapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.md.shopapp.domain.Address;
import uz.md.shopapp.domain.User;
import uz.md.shopapp.dtos.ApiResult;
import uz.md.shopapp.dtos.address.AddressAddDTO;
import uz.md.shopapp.dtos.address.AddressDTO;
import uz.md.shopapp.dtos.order.OrderDTO;
import uz.md.shopapp.dtos.user.ClientEditDTO;
import uz.md.shopapp.dtos.user.ClientMeDto;
import uz.md.shopapp.exceptions.BadRequestException;
import uz.md.shopapp.exceptions.NotAllowedException;
import uz.md.shopapp.exceptions.NotFoundException;
import uz.md.shopapp.mapper.AddressMapper;
import uz.md.shopapp.mapper.OrderMapper;
import uz.md.shopapp.mapper.UserMapper;
import uz.md.shopapp.repository.AddressRepository;
import uz.md.shopapp.repository.OrderRepository;
import uz.md.shopapp.repository.UserRepository;
import uz.md.shopapp.service.contract.ClientService;
import uz.md.shopapp.utils.CommonUtils;
import uz.md.shopapp.utils.MessageConstants;

import java.util.List;

import static uz.md.shopapp.utils.MessageConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClientServiceImpl implements ClientService {

    // region Beans
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    // endregion

    @Override
    public ApiResult<ClientMeDto> getMe() {

        log.info("getMe");

        User user = getCurrentUser();

        if (!user.getRole().getName().equals("CLIENT")) {
            log.error("User is not a client");
            throw NotAllowedException.builder()
                    .messageUz(MessageConstants.YOU_RE_NOT_CLIENT_UZ)
                    .messageRu(MessageConstants.YOU_RE_NOT_CLIENT_RU)
                    .build();
        }

        ClientMeDto clientMeDto = userMapper.toClientMeDTO(user);
        Long aLong = orderRepository
                .countAllByUser_IdAndDeletedIsFalse(clientMeDto.getId());
        clientMeDto.setNumberOfOrders(aLong);
        return ApiResult.successResponse(clientMeDto);
    }

    @Override
    public ApiResult<List<OrderDTO>> getMyOrders() {
        log.info("getMyOrders method called");
        User user = getCurrentUser();

        if (!user.getRole().getName().equals("CLIENT"))
            throw NotAllowedException.builder()
                    .messageUz(MessageConstants.YOU_RE_NOT_CLIENT_UZ)
                    .messageRu(MessageConstants.YOU_RE_NOT_CLIENT_RU)
                    .build();
        return ApiResult
                .successResponse(orderMapper
                        .toDTOList(orderRepository
                                .findAllByUserId(user.getId())));
    }

    @Override
    public ApiResult<Void> deleteMyOrders() {
        log.info("deleteMyOrders method called");
        User user = getCurrentUser();

        if (!user.getRole().getName().equals("CLIENT"))
            throw NotAllowedException.builder()
                    .messageUz(MessageConstants.YOU_RE_NOT_CLIENT_UZ)
                    .messageRu(MessageConstants.YOU_RE_NOT_CLIENT_RU)
                    .build();

        orderRepository.deleteAllByUserId(user.getId());
        return ApiResult.successResponse();
    }

    @Override
    public ApiResult<List<OrderDTO>> getMyOrders(String page) {

        log.info("getMyOrders by pagination");

        if (page == null)
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();

        User user = getCurrentUser();

        if (!user.getRole().getName().equals("CLIENT")) {
            log.info("You are not allowed coz you are not client");
            throw NotAllowedException.builder()
                    .messageUz(MessageConstants.YOU_RE_NOT_CLIENT_UZ)
                    .messageRu(MessageConstants.YOU_RE_NOT_CLIENT_RU)
                    .build();
        }

        int[] pagination = CommonUtils.getPagination(page);
        return ApiResult.successResponse(orderMapper
                .toDTOList(orderRepository
                        .findAllByUserId(
                                user.getId(),
                                PageRequest.of(pagination[0], pagination[1]))
                        .getContent()));
    }

    @Override
    public ApiResult<List<AddressDTO>> getMyAddresses() {

        log.info("getMyAddresses called");
        User user = getCurrentUser();

        if (!user.getRole().getName().equals("CLIENT"))
            throw NotAllowedException.builder()
                    .messageUz(MessageConstants.YOU_RE_NOT_CLIENT_UZ)
                    .messageRu(MessageConstants.YOU_RE_NOT_CLIENT_RU)
                    .build();

        return ApiResult.successResponse(addressMapper
                .toDTOList(addressRepository
                        .findAllByUser_Id(user.getId())));
    }

    @Override
    public ApiResult<AddressDTO> addAddress(AddressAddDTO addressAddDTO) {
        log.info("addAddress called");
        if (addressAddDTO == null
                || addressAddDTO.getLocation() == null) {
            log.error("Bad request");
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();
        }

        User user = userRepository
                .findById(addressAddDTO.getUserId())
                .orElseThrow(() -> NotFoundException
                        .builder()
                        .messageUz(USER_NOT_FOUND_UZ)
                        .messageRu(USER_NOT_FOUND_RU)
                        .build());

        Address address = addressMapper.fromAddDTO(addressAddDTO);

        address.setUser(user);
        addressRepository.save(address);

        return ApiResult.successResponse(addressMapper
                .toDTO(address));
    }

    private User getCurrentUser() {

        log.info("Getting current user");

        String phoneNumber = CommonUtils.getCurrentUserPhoneNumber();

        if (phoneNumber == null)
            throw NotFoundException.builder()
                    .messageUz(USER_NOT_FOUND_UZ)
                    .messageRu(USER_NOT_FOUND_RU)
                    .build();

        return userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> NotFoundException.builder()
                        .messageUz(USER_NOT_FOUND_UZ)
                        .messageRu(USER_NOT_FOUND_RU)
                        .build());
    }

    @Override
    public ApiResult<Void> delete(Long id) {

        log.info("Delete user " + id);

        if (id == null) {
            log.info("Bad request");
            throw BadRequestException.builder()
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .build();
        }
        addressRepository.deleteById(id);
        return ApiResult.successResponse();
    }

    @Override
    public ApiResult<ClientMeDto> edit(ClientEditDTO editDTO) {

        log.info("client is editing");

        User user = getCurrentUser();

        if (!user.getRole().getName().equals("CLIENT")) {
            log.error("User is not a client");
            throw NotAllowedException.builder()
                    .messageUz(MessageConstants.YOU_RE_NOT_CLIENT_UZ)
                    .messageRu(MessageConstants.YOU_RE_NOT_CLIENT_RU)
                    .build();
        }

        user.setFirstName(editDTO.getFirstName());
        user.setLastName(editDTO.getLastName());

        userRepository.save(user);

        ClientMeDto clientMeDto = userMapper.toClientMeDTO(user);
        Long numberOfOrders = orderRepository
                .countAllByUser_IdAndDeletedIsFalse(user.getId());
        clientMeDto.setNumberOfOrders(numberOfOrders);
        return ApiResult.successResponse(clientMeDto);
    }
}
