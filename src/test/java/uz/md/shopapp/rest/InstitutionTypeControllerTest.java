package uz.md.shopapp.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uz.md.shopapp.IntegrationTest;
import uz.md.shopapp.controller.InstitutionTypeController;
import uz.md.shopapp.domain.InstitutionType;
import uz.md.shopapp.domain.User;
import uz.md.shopapp.dtos.institution_type.InstitutionTypeAddDTO;
import uz.md.shopapp.dtos.institution_type.InstitutionTypeEditDTO;
import uz.md.shopapp.repository.InstitutionTypeRepository;
import uz.md.shopapp.util.MockDataGenerator;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InstitutionTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockDataGenerator mockDataGenerator;

    public static final String BASE_URL = InstitutionTypeController.BASE_URL;
    @Autowired
    private InstitutionTypeRepository institutionTypeRepository;

    @Test
    void shouldGetAllTypes() throws Exception {

        List<InstitutionType> types = mockDataGenerator.getInstitutionTypesSaved(10);
        long beforeCall = institutionTypeRepository.count();
        Assertions.assertEquals(beforeCall, 10);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.length()").value(10))
                .andExpect(jsonPath("$.data[0].id").value(types.get(0).getId()))
                .andReturn();
        Assertions.assertNotNull(result);
        long afterCall = institutionTypeRepository.count();
        Assertions.assertEquals(afterCall, beforeCall);
    }

    @Test
    void shouldGetAllByPage() throws Exception {
        List<InstitutionType> types = mockDataGenerator.getInstitutionTypesSaved(20);
        long beforeCall = institutionTypeRepository.count();
        Assertions.assertEquals(beforeCall, 20);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/by-page/0-10"));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.length()").value(10));

        testForEquality(perform, types.subList(0, 10));

        long afterCall = institutionTypeRepository.count();
        Assertions.assertEquals(afterCall, beforeCall);
    }

    @Test
    void shouldGetAllByPage2() throws Exception {
        List<InstitutionType> types = mockDataGenerator.getInstitutionTypesSaved(20);
        long beforeCall = institutionTypeRepository.count();
        Assertions.assertEquals(beforeCall, 20);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/by-page/1-10"));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.length()").value(10));

        testForEquality(perform, types.subList(10, 20));

        long afterCall = institutionTypeRepository.count();
        Assertions.assertEquals(afterCall, beforeCall);
    }

    @WithMockUser(username = "+998931668648", password = "123")
    @Test
    void shouldAdd() throws Exception {
        User mockEmployee = mockDataGenerator.getMockEmployee();
        mockEmployee.setPhoneNumber("+998931668648");
        mockEmployee.setPassword(passwordEncoder.encode("123"));
        InstitutionTypeAddDTO addDTO = new InstitutionTypeAddDTO();
        addDTO.setNameUz("nameU");
        addDTO.setNameRu("nameR");
        addDTO.setDescriptionUz("description");
        addDTO.setDescriptionRu("description");
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addDTO)));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.nameUz").value(addDTO.getNameUz()))
                .andExpect(jsonPath("$.data.nameRu").value(addDTO.getNameRu()))
                .andExpect(jsonPath("$.data.descriptionUz").value(addDTO.getDescriptionUz()))
                .andExpect(jsonPath("$.data.descriptionRu").value(addDTO.getDescriptionRu()));
    }

    @WithMockUser(username = "+998931668648", password = "123")
    @Test
    void shouldEdit() throws Exception {
        User mockEmployee = mockDataGenerator.getMockEmployee();
        mockEmployee.setPhoneNumber("+998931668648");
        mockEmployee.setPassword(passwordEncoder.encode("123"));

        InstitutionType type = mockDataGenerator.getInstitutionType();
        institutionTypeRepository.save(type);

        InstitutionTypeEditDTO editDTO = new InstitutionTypeEditDTO();
        editDTO.setId(type.getId());
        editDTO.setNameUz("nameU");
        editDTO.setNameRu("nameR");
        editDTO.setDescriptionUz("description");
        editDTO.setDescriptionRu("description");
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO)));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.nameUz").value(editDTO.getNameUz()))
                .andExpect(jsonPath("$.data.nameRu").value(editDTO.getNameRu()))
                .andExpect(jsonPath("$.data.descriptionUz").value(editDTO.getDescriptionUz()))
                .andExpect(jsonPath("$.data.descriptionRu").value(editDTO.getDescriptionRu()));
    }

    @WithMockUser(username = "+998931668648", password = "123")
    @Test
    void shouldDelete() throws Exception {
        User mockEmployee = mockDataGenerator.getMockEmployee();
        mockEmployee.setPhoneNumber("+998931668648");
        mockEmployee.setPassword(passwordEncoder.encode("123"));

        InstitutionType type = mockDataGenerator.getInstitutionType();
        institutionTypeRepository.save(type);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_URL + "/delete/"+type.getId()));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200));

        institutionTypeRepository.count();

    }

    private void testForEquality(ResultActions perform, List<InstitutionType> types) {
        for (int i = 0; i < types.size(); i++) {
            try {
                perform.andExpect(jsonPath("$.data[" + i + "].id").value(types.get(i).getId()));
                perform.andExpect(jsonPath("$.data[" + i + "].nameUz").value(types.get(i).getNameUz()));
                perform.andExpect(jsonPath("$.data[" + i + "].nameRu").value(types.get(i).getNameRu()));
                perform.andExpect(jsonPath("$.data[" + i + "].descriptionRu").value(types.get(i).getDescriptionRu()));
                perform.andExpect(jsonPath("$.data[" + i + "].descriptionUz").value(types.get(i).getDescriptionUz()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
