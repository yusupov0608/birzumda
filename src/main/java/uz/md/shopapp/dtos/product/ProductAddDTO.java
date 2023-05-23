package uz.md.shopapp.dtos.product;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductAddDTO {

    @NotBlank(message = "Product name must not be empty")
    private String nameUz;

    @NotBlank(message = "Product name must not be empty")
    private String nameRu;
    private String descriptionUz;
    private String descriptionRu;

    @NotNull(message = "Product price must not be null")
    private Long price;

    @NotNull(message = "Product category must not be null")
    private Long categoryId;
}
