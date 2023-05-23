package uz.md.shopapp.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import uz.md.shopapp.domain.template.AbsLongEntity;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
@Builder
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE institution_type SET deleted = true where id = ?")
public class InstitutionType extends AbsLongEntity {

    @Column(unique = true, nullable = false)
    private String nameUz;

    @Column(unique = true, nullable = false)
    private String nameRu;

    private String imageUrl;

    private String descriptionUz;

    private String descriptionRu;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<Institution> institutions;

    public InstitutionType(String nameUz, String nameRu, String descriptionUz, String descriptionRu) {
        this.nameUz = nameUz;
        this.nameRu = nameRu;
        this.descriptionUz = descriptionUz;
        this.descriptionRu = descriptionRu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstitutionType)) {
            return false;
        }
        return super.getId() != null && super.getId().equals(((InstitutionType) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
