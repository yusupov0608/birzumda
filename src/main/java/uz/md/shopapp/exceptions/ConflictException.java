package uz.md.shopapp.exceptions;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Builder
@Getter
public class ConflictException extends RuntimeException {
    private String messageUz;
    private String messageRu;

    public ConflictException(String messageUz, String messageRu) {
        super(messageUz);
        this.messageUz = messageUz;
        this.messageRu = messageRu;
    }
}
