package ru.javaops.masterjava.persist.model;

import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Group extends BaseEntity {
    private @NonNull
    GroupType groupType;
    private @NonNull
    String name;

    public Group(Integer id, GroupType groupType, String name) {
        this(groupType, name);
        this.id = id;
    }
}
