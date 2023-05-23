package uz.md.shopapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.md.shopapp.domain.Category;
import uz.md.shopapp.domain.Institution;
import uz.md.shopapp.dtos.ApiResult;
import uz.md.shopapp.dtos.category.CategoryAddDTO;
import uz.md.shopapp.dtos.category.CategoryDTO;
import uz.md.shopapp.dtos.category.CategoryEditDTO;
import uz.md.shopapp.dtos.category.CategoryInfoDTO;
import uz.md.shopapp.exceptions.AlreadyExistsException;
import uz.md.shopapp.exceptions.BadRequestException;
import uz.md.shopapp.exceptions.NotFoundException;
import uz.md.shopapp.mapper.CategoryMapper;
import uz.md.shopapp.repository.CategoryRepository;
import uz.md.shopapp.repository.InstitutionRepository;
import uz.md.shopapp.service.contract.CategoryService;

import java.util.List;

import static uz.md.shopapp.utils.MessageConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final InstitutionRepository institutionRepository;


    @Override
    public ApiResult<CategoryDTO> add(CategoryAddDTO dto) {

        log.info("add category called");

        if (dto == null || dto.getNameUz() == null
                || dto.getNameRu() == null
                || dto.getInstitutionId() == null) {
            log.info("Bad Request in CategoryService add method");
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();
        }

        Institution institution = institutionRepository
                .findById(dto.getInstitutionId())
                .orElseThrow(() -> NotFoundException.builder()
                        .messageUz("Muassasa topilmadi")
                        .messageRu("Объект не найден")
                        .build());

        if (categoryRepository.existsByNameUzOrNameRu(dto.getNameUz(), dto.getNameRu())) {
            log.error("Category name already existed");
            throw AlreadyExistsException.builder()
                    .messageUz("Kategoriya nomi allaqachon mavjud")
                    .messageRu("Название категории уже существует")
                    .build();
        }

        Category category = categoryMapper
                .fromAddDTO(dto);

        if (category == null) {
            log.error("Added Category is null");
            throw BadRequestException.builder()
                    .messageUz(CATEGORY_IS_NULL_UZ)
                    .messageRu(CATEGORY_IS_NULL_RU)
                    .build();
        }

        category.setInstitution(institution);

        return ApiResult
                .successResponse(categoryMapper
                        .toDTO(categoryRepository
                                .save(categoryRepository.save(category))));
    }

    @Override
    public ApiResult<CategoryDTO> findById(Long id) {

        log.info("findById method called");

        if (id == null) {
            log.info("Bad request in findById of CategoryService");
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();
        }

        return ApiResult.successResponse(categoryMapper
                .toDTO(categoryRepository
                        .findById(id)
                        .orElseThrow(() -> NotFoundException.builder()
                                .messageUz("Kategoriya topilmadi")
                                .messageRu("Категория не найдена")
                                .build())));
    }

    @Override
    public ApiResult<CategoryDTO> edit(CategoryEditDTO editDTO) {

        log.info("edit category method called");

        if (editDTO == null || editDTO.getNameRu() == null
                || editDTO.getNameUz() == null
                || editDTO.getInstitutionId() == null || editDTO.getId() == null) {
            log.error("bad request in edit category");
            throw BadRequestException.builder()
                    .messageUz(ERROR_IN_REQUEST_UZ)
                    .messageRu(ERROR_IN_REQUEST_RU)
                    .build();
        }


        Institution institution = institutionRepository
                .findById(editDTO.getInstitutionId())
                .orElseThrow(() -> NotFoundException.builder()
                        .messageUz(INSTITUTION_NOT_FOUND_UZ)
                        .messageRu(INSTITUTION_NOT_FOUND_RU)
                        .build());


        Category editing = categoryRepository
                .findById(editDTO.getId())
                .orElseThrow(() -> NotFoundException.builder()
                        .messageUz(CATEGORY_NOT_FOUND_UZ)
                        .messageRu(CATEGORY_NOT_FOUND_RU)
                        .build());

        if (categoryRepository
                .existsByNameUzOrNameRuAndIdIsNot(editDTO.getNameUz(), editDTO.getNameRu(), editing.getId())) {
            log.error("Category name already exists");
            throw AlreadyExistsException.builder()
                    .messageUz("Kategoriya nomi allaqachon mavjud")
                    .messageRu("Название категории уже существует")
                    .build();
        }
        Category category = categoryMapper.fromEditDTO(editDTO, editing);
        category.setInstitution(institution);
        return ApiResult.successResponse(categoryMapper
                .toDTO(categoryRepository.save(category)));
    }

    @Override
    public ApiResult<List<CategoryDTO>> getAll() {
        log.info("getAll called");
        return ApiResult.successResponse(
                categoryMapper.toDTOList(
                        categoryRepository.findAll()
                )
        );
    }

    @Override
    public ApiResult<List<CategoryInfoDTO>> getAllForInfo() {
        log.info("getAllForInfo called");
        return ApiResult.successResponse(categoryRepository
                .findAllForInfo());
    }

    @Override
    public ApiResult<List<CategoryInfoDTO>> getAllByInstitutionId(Long id) {
        log.info("getAllByInstitutionId method called");
        if (id == null)
            throw BadRequestException.builder()
                    .messageUz("Id bo'sh bo'lishi mumkin emas")
                    .messageRu("Идентификатор не может быть пустым")
                    .build();

        return ApiResult.successResponse(categoryRepository
                .findAllForInfoByInstitutionId(id));
    }

    @Override
    public ApiResult<List<CategoryInfoDTO>> getAllByInstitutionIdAndPage(Long id, String page) {
        log.info("getAllByInstitutionIdAndPage called");
        if (page == null || id == null)
            throw BadRequestException.builder()
                    .messageUz("Id yoki sahifa bo'sh bo'lishi mumkin emas")
                    .messageRu("Идентификатор или страница не могут быть пустыми")
                    .build();

        int[] paged = {Integer.parseInt(page.split("-")[0]),
                Integer.parseInt(page.split("-")[1])};
        return ApiResult.successResponse(categoryMapper
                .toInfoDTOList(categoryRepository
                        .findAllForInfoByInstitutionId(id, PageRequest.of(paged[0], paged[1]))
                        .getContent()));
    }

    @Override
    public ApiResult<Void> delete(Long id) {
        log.info("Deleting category with id " + id);
        if (id == null)
            throw BadRequestException.builder()
                    .messageUz("Id bo'sh bo'lishi mumkin emas")
                    .messageRu("Идентификатор не может быть пустым")
                    .build();

        if (!categoryRepository.existsById(id))
            throw NotFoundException.builder()
                    .messageUz("Kategoriya topilmadi")
                    .messageRu("категория не найдена")
                    .build();

        categoryRepository.deleteById(id);
        return ApiResult.successResponse();
    }
}
