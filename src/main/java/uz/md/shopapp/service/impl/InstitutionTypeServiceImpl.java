package uz.md.shopapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.md.shopapp.domain.InstitutionType;
import uz.md.shopapp.dtos.ApiResult;
import uz.md.shopapp.dtos.institution_type.InstitutionTypeAddDTO;
import uz.md.shopapp.dtos.institution_type.InstitutionTypeDTO;
import uz.md.shopapp.dtos.institution_type.InstitutionTypeEditDTO;
import uz.md.shopapp.exceptions.AlreadyExistsException;
import uz.md.shopapp.exceptions.BadRequestException;
import uz.md.shopapp.exceptions.NotFoundException;
import uz.md.shopapp.file_storage.FilesStorageService;
import uz.md.shopapp.mapper.InstitutionTypeMapper;
import uz.md.shopapp.repository.InstitutionTypeRepository;
import uz.md.shopapp.service.contract.InstitutionTypeService;

import java.util.List;

import static uz.md.shopapp.utils.MessageConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstitutionTypeServiceImpl implements InstitutionTypeService {

    private final InstitutionTypeRepository institutionTypeRepository;
    private final InstitutionTypeMapper institutionTypeMapper;
    private final FilesStorageService filesStorageService;

    @Override
    public ApiResult<InstitutionTypeDTO> add(InstitutionTypeAddDTO dto) {

        log.info("Adding new InstitutionType");

        if (dto == null
                || dto.getNameUz() == null
                || dto.getNameRu() == null)
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();

        if (institutionTypeRepository
                .existsByNameUzOrNameRu(dto.getNameUz(), dto.getNameRu()))
            throw AlreadyExistsException.builder()
                    .messageUz("Muassasa turi allaqachon mavjud")
                    .messageRu("Тип объекта уже существует")
                    .build();

        InstitutionType institutionType = institutionTypeMapper
                .fromAddDTO(dto);
        institutionTypeRepository.save(institutionType);

        return ApiResult
                .successResponse(institutionTypeMapper.toDTO(institutionType));
    }

    @Override
    public ApiResult<InstitutionTypeDTO> findById(Long id) {

        log.info("findById called");

        if (id == null)
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();

        return ApiResult.successResponse(institutionTypeMapper
                .toDTO(institutionTypeRepository
                        .findById(id)
                        .orElseThrow(() -> NotFoundException.builder()
                                .messageUz(INSTITUTION_NOT_FOUND_UZ)
                                .messageRu("")
                                .build())));
    }

    @Override
    public ApiResult<InstitutionTypeDTO> edit(InstitutionTypeEditDTO editDTO) {

        log.info("Editing InstitutionType");


        if (editDTO == null || editDTO.getId() == null
                || editDTO.getNameUz() == null
                || editDTO.getNameRu() == null)
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();

        InstitutionType editing = institutionTypeRepository
                .findById(editDTO.getId())
                .orElseThrow(() -> NotFoundException.builder()
                        .messageUz("INSTITUTION_TYPE_NOT_FOUND")
                        .messageRu("")
                        .build());

        if (institutionTypeRepository.existsByNameUzOrNameRuAndIdIsNot(editDTO.getNameUz(), editDTO.getNameRu(), editing.getId()))
            throw AlreadyExistsException.builder()
                    .messageUz("INSTITUTION_NAME_ALREADY_EXISTS")
                    .messageRu("")
                    .build();

        InstitutionType institutionType = institutionTypeMapper.fromEditDTO(editDTO, editing);

        return ApiResult.successResponse(institutionTypeMapper
                .toDTO(institutionTypeRepository.save(institutionType)));
    }

    @Override
    public ApiResult<List<InstitutionTypeDTO>> getAll() {

        log.info("Getting all institutionTypes...");

        return ApiResult.successResponse(
                institutionTypeMapper.toDTOList(
                        institutionTypeRepository.findAll()
                )
        );
    }

    @Override
    public ApiResult<List<InstitutionTypeDTO>> getAllByPage(String page) {

        log.info("getAllByPage method");

        if (page == null)
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();
        int[] paged = new int[]{Integer.parseInt(page.split("-")[0]),
                Integer.parseInt(page.split("-")[1])};
        return ApiResult.successResponse(
                institutionTypeMapper.toDTOList(institutionTypeRepository
                        .findAll(PageRequest.of(paged[0], paged[1]))
                        .getContent())
        );
    }

    @Override
    public ApiResult<Void> setImage(Long typeId, MultipartFile image) {
        if (typeId == null || image == null)
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();

        InstitutionType institutionType = institutionTypeRepository
                .findById(typeId)
                .orElseThrow(() -> NotFoundException.builder()
                        .messageUz("Muassasa turi topilmadi")
                        .messageRu("Тип institution не найден")
                        .build());

        String savedFileURL = filesStorageService
                .saveOrUpdate(image, institutionType.getImageUrl());

        institutionType.setImageUrl(savedFileURL);
        institutionTypeRepository.save(institutionType);
        return ApiResult.successResponse();
    }

    @Override
    public ApiResult<Void> delete(Long id) {

        log.info("deleting institutionType with id " + id);

        if (id == null)
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();
        if (!institutionTypeRepository.existsById(id))
            throw NotFoundException.builder()
                    .messageUz(INSTITUTION_NOT_FOUND_UZ)
                    .messageRu("")
                    .build();

        institutionTypeRepository.deleteById(id);
        return ApiResult.successResponse();
    }
}