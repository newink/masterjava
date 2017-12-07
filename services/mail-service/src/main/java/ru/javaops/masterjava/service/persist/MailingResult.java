package ru.javaops.masterjava.service.persist;

import lombok.*;
import ru.javaops.masterjava.persist.model.BaseEntity;
import ru.javaops.masterjava.persist.model.type.Result;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailingResult extends BaseEntity {

    @NonNull
    private String email;
    @NonNull
    private Result result;
    private String reason;
}
