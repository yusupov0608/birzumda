package uz.md.shopapp.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uz.md.shopapp.IntegrationTest;
import uz.md.shopapp.domain.Role;
import uz.md.shopapp.domain.User;
import uz.md.shopapp.domain.enums.PermissionEnum;
import uz.md.shopapp.dtos.user.ClientLoginDTO;
import uz.md.shopapp.dtos.user.EmployeeLoginDTO;
import uz.md.shopapp.repository.RoleRepository;
import uz.md.shopapp.repository.UserRepository;
import uz.md.shopapp.utils.AppConstants;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@IntegrationTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    public static final String BASE_URL = AppConstants.BASE_URL + "auth";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Role clientRole;
    private Role managerRole;

    @BeforeEach
    void init() throws Exception {
        clientRole = clientRole();
    }

    private Role clientRole() {
        Role role = Role.builder().permissions(Set.of(PermissionEnum.GET_PRODUCT))
                .name("CLIENT")
                .description("The client role")
                .build();
        return roleRepository.save(role);
    }

    @Test
    void shouldGetSmsCode() throws Exception {
        String phoneNumber = "+998995555555";
        long beforeCall = userRepository.count();
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/get/sms-code")
                        .param("phoneNumber", phoneNumber))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isString());
        long afterCall = userRepository.count();
        Assertions.assertEquals(beforeCall + 1, afterCall);

        List<User> all = userRepository.findAll();
        Assertions.assertEquals(all.size(), 1);
        User user = all.get(0);
        Assertions.assertEquals(user.getPhoneNumber(), phoneNumber);
    }

    @Test
    void shouldGetSmsCodeEvenAlreadyRegistered() throws Exception {

        String phoneNumber = "+998995555555";
        User john = User.builder()
                .firstName("John")
                .phoneNumber(phoneNumber)
                .role(clientRole)
                .build();
        john.setDeleted(false);
        userRepository.save(john);
        long beforeCall = userRepository.count();
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/get/sms-code")
                        .param("phoneNumber", phoneNumber))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isString());
        long afterCall = userRepository.count();
        Assertions.assertEquals(beforeCall, afterCall);

        List<User> all = userRepository.findAll();
        Assertions.assertEquals(all.size(), 1);
        User user = all.get(0);
        Assertions.assertEquals(user.getPhoneNumber(), phoneNumber);
    }

    @Test
    void shouldNotGetWithInValidNumber() throws Exception {
        String phoneNumber = "9989955555555";
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/get/sms-code")
                        .param("phoneNumber", phoneNumber))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotGetWithInValidNumber2() throws Exception {
        String phoneNumber = "+9989911122333";
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/get/sms-code")
                        .param("phoneNumber", phoneNumber))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetTokenWithClientLogin() throws Exception {

        String phoneNumber = "+998931234567";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/get/sms-code")
                        .param("phoneNumber", phoneNumber))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isString())
                .andReturn();

        Assertions.assertNotNull(result);
        MockHttpServletResponse response = result.getResponse();
        Assertions.assertNotNull(response);
        JsonNode jsonNode = objectMapper.readValue(response.getContentAsString(), JsonNode.class);
        Assertions.assertNotNull(jsonNode);
        String data = jsonNode.get("data").asText();
        String smsCode = data.substring(data.length() - 5);

        ClientLoginDTO clientLoginDTO = new ClientLoginDTO(phoneNumber, smsCode);

        long beforeCall = userRepository.count();
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/client/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientLoginDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer "));
        long afterCall = userRepository.count();
        Assertions.assertEquals(beforeCall, afterCall);
    }

    @Test
    void shouldGetTokenWithAdminLogin() throws Exception{

        User user = new User("admin",
                "",
                "+998931001010",
                passwordEncoder.encode("admin"),
                adminRole());
        userRepository.save(user);
        EmployeeLoginDTO employeeLoginDTO = new EmployeeLoginDTO();
        employeeLoginDTO.setPassword("admin");
        employeeLoginDTO.setPhoneNumber("+998931001010");
        long beforeCall = userRepository.count();
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/employee/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeLoginDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer "));
        long afterCall = userRepository.count();
        Assertions.assertEquals(beforeCall, afterCall);

    }



    private Role adminRole() {
        return roleRepository.save(new Role("ADMIN","admin role",Set.of(PermissionEnum.values())));
    }

    Role managerRole() {
        Role role = Role.builder()
                .name("MANAGER")
                .description("Managing app")
                .permissions(Set.of(PermissionEnum.GET_USER))
                .build();
        return roleRepository.save(role);
    }



}