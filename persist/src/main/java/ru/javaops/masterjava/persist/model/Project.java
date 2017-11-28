package ru.javaops.masterjava.persist.model;

import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Project extends BaseEntity {
    private @NonNull
    String description;
    private @NonNull
    Integer groupId;

    public Project(Integer id, String description, Integer groupId) {
        this(description, groupId);
        this.id = id;
    }
}
