package uz.md.shopapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.md.shopapp.domain.Category;
import uz.md.shopapp.domain.Institution;
import uz.md.shopapp.domain.Product;
import uz.md.shopapp.dtos.ApiResult;
import uz.md.shopapp.dtos.category.CategoryDTO;
import uz.md.shopapp.dtos.institution.InstitutionDTO;
import uz.md.shopapp.dtos.institution.InstitutionInfoDTO;
import uz.md.shopapp.mapper.CategoryMapper;
import uz.md.shopapp.mapper.InstitutionMapper;
import uz.md.shopapp.repository.InstitutionRepository;
import uz.md.shopapp.service.QueryService;
import uz.md.shopapp.service.contract.SearchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final QueryService queryService;
    private final InstitutionMapper institutionMapper;
    private final CategoryMapper categoryMapper;
    private final InstitutionRepository institutionRepository;

    @Override
    public ApiResult<List<InstitutionDTO>> getBySearch(String value) {
        List<Institution> institutions = institutionRepository.findByProductName("%" + value + "%");
        return ApiResult.successResponse(institutionMapper.toDTOList(institutions));
    }

    private List<InstitutionDTO> groupCategoriesByInstitution(List<CategoryDTO> categories) {
        Map<Long, List<CategoryDTO>> map = new HashMap<>();
        for (CategoryDTO category : categories) {
            List<CategoryDTO> orDefault = map.getOrDefault(category.getInstitutionId(), new ArrayList<>());
            orDefault.add(category);
            map.put(category.getInstitutionId(), orDefault);
        }

        List<InstitutionDTO> result = new ArrayList<>();
        map.forEach((institutionId, categoryDTOS) -> {
            InstitutionInfoDTO info = institutionRepository.findForInfoById(institutionId);
            result.add(new InstitutionDTO(
                    info.getId(),
                    info.getNameUz(),
                    info.getNameRu(),
                    info.getImageUrl(),
                    info.getDescriptionUz(),
                    info.getDescriptionRu(),
                    categoryDTOS
            ));
        });
        return result;
    }

    private List<CategoryDTO> groupProductsByCategory(List<Product> productList) {
        Map<Category, List<Product>> map = new HashMap<>();
        for (Product product : productList) {
            List<Product> orDefault = map.getOrDefault(product.getCategory(), new ArrayList<>());
            orDefault.add(product);
            map.put(product.getCategory(), orDefault);
        }

        List<CategoryDTO> result = new ArrayList<>();
        map.forEach((category, products) -> result.add(categoryMapper.toDTO(category)));
        return result;
    }

}
