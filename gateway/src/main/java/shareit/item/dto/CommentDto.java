package shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {

    @NonNull
    private Long id;

    @Length(max = 500)
    @NotBlank
    private String text;

    @Length(max = 50)
    private String authorName;

    @NonNull
    private LocalDateTime created;

    public CommentDto() {
    }
}
