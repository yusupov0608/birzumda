package uz.md.shopapp.util;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import uz.md.shopapp.domain.*;
import uz.md.shopapp.domain.enums.PermissionEnum;
import uz.md.shopapp.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class MockDataGenerator {

    private final Faker FAKER = new Faker();
    @Autowired
    private InstitutionTypeRepository institutionTypeRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    public InstitutionType getInstitutionTypeSaved() {
        return institutionTypeRepository.save(getInstitutionType());
    }

    public InstitutionType getInstitutionType() {
        long l = RandomUtils.nextLong(1, 100);
        return getInstitutionType(l);
    }

    public InstitutionType getInstitutionType(long index) {
        return new InstitutionType(
                "Restaurant " + index,
                "Restaurant " + index,
                " All restaurants " + index,
                " All restaurants " + index);
    }

    public List<InstitutionType> getInstitutionTypes(int count) {
        return IntStream.range(0, count)
                .mapToObj(operand -> getInstitutionType())
                .collect(Collectors.toList());
    }

    public List<InstitutionType> getInstitutionTypesSaved(int count) {
        return institutionTypeRepository.saveAll(IntStream.range(0, count)
                .mapToObj(this::getInstitutionType)
                .collect(Collectors.toList()));
    }

    public Institution getInstitution(int index, InstitutionType type, User employee) {
        Location location = getLocation();
        locationRepository.save(location);
        return Institution.builder()
                .nameUz("Cafe " + index)
                .nameRu("Cafe ru " + index)
                .descriptionUz("Cafe description " + index)
                .descriptionUz("Cafe description ru" + index)
                .location(location)
                .manager(employee)
                .type(type)
                .build();
    }

    public List<Institution> getInstitutionsSaved(int count, InstitutionType type, User employee) {
        return institutionRepository.saveAll(IntStream.range(0, count)
                .mapToObj(value -> getInstitution(value, type, employee))
                .collect(Collectors.toList()));
    }

    public List<Institution> getInstitutionsSaved(int count, int indexFrom, InstitutionType type, User employee) {
        return institutionRepository.saveAll(IntStream.range(indexFrom, indexFrom + count)
                .mapToObj(value -> getInstitution(value, type, employee))
                .collect(Collectors.toList()));
    }


    public User getUser(Role role) {
        return new User(
                "Ali",
                "Yusupov",
                "+998902002020",
                role);
    }

    public Address getAddress(User manager) {
        return new Address(
                manager,
                15,
                4,
                2
        );
    }

    public Institution getInstitution(Location location, InstitutionType institutionType, User manager) {
        return new Institution(
                "Max Way",
                "Max Way",
                " Cafe ",
                " Cafe ",
                null,
                location,
                institutionType,
                null,
                manager);
    }

    public Category getCategory(Institution institution) {
        Category category = new Category();
        category.setNameUz("Uzbek meals");
        category.setNameRu("Uzbek meals");
        category.setDescriptionUz("uzbek national meals");
        category.setDescriptionRu("uzbek national meals");
        category.setDeleted(false);
        category.setActive(true);
        category.setInstitution(institution);
        return category;
    }

    public Location getLocation() {
        long la = RandomUtils.nextLong(15, 100);
        long lo = RandomUtils.nextLong(15, 100);
        return new Location((double) la, (double) lo);
    }

    public Product getProduct(Category category) {
        Random random = new Random();
        long price = random.nextLong() * 300 + 200;
        int v = random.nextInt(5) + 1;
        Product product = new Product();
        product.setNameUz("Plov " + v);
        product.setNameRu("Plov " + v);
        product.setDescriptionUz(" Andijan Plov ");
        product.setDescriptionRu("Andijan Plov");
        product.setPrice(price);
        product.setDeleted(false);
        product.setActive(true);
        product.setCategory(category);
        return product;
    }

    private Role getAdminRole() {
        return Role.builder()
                .name("ADMIN")
                .description("admin description")
                .permissions(Set.of(PermissionEnum.values()))
                .build();
    }

    public User getMockEmployee() {
        Role admin = roleRepository
                .findByName("ADMIN")
                .orElse(roleRepository.save(getAdminRole()));
        User user = getUser();
        user.setRole(admin);
        return userRepository.save(user);
    }

    private User getUser() {
        Name name = FAKER.name();
        String phoneNumber = "+99893" + RandomStringUtils.random(7, false, true);
        String password = RandomStringUtils.random(5, false, true);
        return User.builder()
                .firstName(name.firstName())
                .lastName(name.lastName())
                .phoneNumber(phoneNumber)
                .password(password)
                .build();
    }

    public User getMockClient() {
        Role clientRole = getClientRole();
        User user = getUser();
        user.setRole(clientRole);
        user.setCodeValidTill(LocalDateTime.now().plusDays(1));
        return user;
    }

    private Role getClientRole() {
        return Role.builder()
                .name("CLIENT")
                .description("client description")
                .permissions(Set.of(PermissionEnum.values()))
                .build();
    }


}
