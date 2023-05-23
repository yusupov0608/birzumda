package uz.md.shopapp.dtos.institution;

import lombok.*;

@Getter
@Setter
@ToString
public class InstitutionInfoDTO {
    private Long id;
    private String nameUz;
    private String nameRu;
    private String imageUrl;
    private String descriptionUz;
    private String descriptionRu;
    private Long institutionTypeId;
    private Long managerId;

    public InstitutionInfoDTO() {}


    public InstitutionInfoDTO(Long id, String nameUz, String nameRu, String imageUrl, String descriptionUz, String descriptionRu, Long institutionTypeId, Long managerId) {
        this.id = id;
        this.nameUz = nameUz;
        this.nameRu = nameRu;
        this.imageUrl = imageUrl;
        this.descriptionUz = descriptionUz;
        this.descriptionRu = descriptionRu;
        this.institutionTypeId = institutionTypeId;
        this.managerId = managerId;
    }
}
