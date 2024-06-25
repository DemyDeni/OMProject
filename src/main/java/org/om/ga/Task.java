package org.om.ga;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.om.graph.Item;
import org.om.graph.Storage;
import org.om.graph.StorageType;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class Task implements Cloneable {
    Integer from;
    StorageType fromType;
    Integer to;
    Item item;
    Integer itemNum;
    Double chance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, item, itemNum, chance);
    }

    @SneakyThrows
    public Task clone() {
        Task cloned = (Task) super.clone();
        return cloned;
    }
}
