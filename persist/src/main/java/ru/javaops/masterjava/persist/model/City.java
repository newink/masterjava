package ru.javaops.masterjava.persist.model;

import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class City extends BaseEntity {
    private @NonNull
    String mnemonic;
    private @NonNull
    String name;

    public City(Integer id, String mnemonic, String name) {
        this(mnemonic, name);
        this.id = id;
    }
}
