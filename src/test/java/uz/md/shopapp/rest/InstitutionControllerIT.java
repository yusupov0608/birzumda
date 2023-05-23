package uz.md.shopapp.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uz.md.shopapp.IntegrationTest;
import uz.md.shopapp.controller.InstitutionController;
import uz.md.shopapp.domain.Institution;
import uz.md.shopapp.domain.InstitutionType;
import uz.md.shopapp.domain.User;
import uz.md.shopapp.repository.InstitutionRepository;
import uz.md.shopapp.repository.InstitutionTypeRepository;
import uz.md.shopapp.util.MockDataGenerator;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InstitutionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockDataGenerator mockDataGenerator;

    public static final String BASE_URL = InstitutionController.BASE_URL;
    @Autowired
    private InstitutionTypeRepository institutionTypeRepository;
    @Autowired
    private InstitutionRepository institutionRepository;

    @Test
    void shouldGetAllInstitutions() throws Exception {
        InstitutionType type = mockDataGenerator.getInstitutionType();
        institutionTypeRepository.save(type);
        User mockEmployee = mockDataGenerator.getMockEmployee();
        List<Institution> institutions = mockDataGenerator.getInstitutionsSaved(10, type, mockEmployee);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_URL));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.length()").value(10));
        checkForResult(institutions, perform);
    }

    @Test
    void shouldGetAllInstitutionsByType() throws Exception {
        InstitutionType type = mockDataGenerator.getInstitutionType();
        institutionTypeRepository.save(type);
        User mockEmployee = mockDataGenerator.getMockEmployee();
        List<Institution> institutions = mockDataGenerator.getInstitutionsSaved(10, type, mockEmployee);
        InstitutionType type2 = mockDataGenerator.getInstitutionType();
        institutionTypeRepository.save(type2);
        mockDataGenerator.getInstitutionsSaved(10, 11, type2, mockEmployee);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/all/by-type/" + type.getId()));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.length()").value(10));
        checkForResult(institutions, perform);
    }

    @Test
    void shouldGetInstitutionById() throws Exception {
        InstitutionType type = mockDataGenerator.getInstitutionType();
        institutionTypeRepository.save(type);
        User mockEmployee = mockDataGenerator.getMockEmployee();
        Institution institution = mockDataGenerator.getInstitution(1, type, mockEmployee);
        institutionRepository.save(institution);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/" + institution.getId()));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nameUz").value(institution.getNameUz()))
                .andExpect(jsonPath("$.data.nameRu").value(institution.getNameRu()))
                .andExpect(jsonPath("$.data.descriptionUz").value(institution.getDescriptionUz()))
                .andExpect(jsonPath("$.data.descriptionRu").value(institution.getDescriptionRu()))
                .andExpect(jsonPath("$.data.institutionTypeId").value(institution.getType().getId()));
    }

    @Test
    void shouldDeleteInstitution() throws Exception {
        InstitutionType type = mockDataGenerator.getInstitutionType();
        institutionTypeRepository.save(type);
        User mockEmployee = mockDataGenerator.getMockEmployee();
        Institution institution = mockDataGenerator.getInstitution(1, type, mockEmployee);
        institutionRepository.save(institution);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/" + institution.getId()));
    }

    private void checkForResult(List<Institution> institutions, ResultActions perform) throws Exception {
        for (int i = 0; i < institutions.size(); i++) {
            perform.andExpect(jsonPath("$.data[" + i + "].id").value(institutions.get(i).getId()))
                    .andExpect(jsonPath("$.data[" + i + "].nameUz").value(institutions.get(i).getNameUz()))
                    .andExpect(jsonPath("$.data[" + i + "].nameRu").value(institutions.get(i).getNameRu()))
                    .andExpect(jsonPath("$.data[" + i + "].descriptionUz").value(institutions.get(i).getDescriptionUz()))
                    .andExpect(jsonPath("$.data[" + i + "].descriptionRu").value(institutions.get(i).getDescriptionRu()))
                    .andExpect(jsonPath("$.data[" + i + "].institutionTypeId").value(institutions.get(i).getType().getId()));
        }
    }


}
