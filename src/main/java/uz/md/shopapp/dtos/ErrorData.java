package uz.md.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Builder
public class ErrorData {

    private String messageUz;
    private String messageRu;
    private String devMsg;
    private String userMsg;

    public ErrorData(String devMsg, String userMsg) {
        this.devMsg = devMsg;
        this.userMsg = userMsg;
    }
}
