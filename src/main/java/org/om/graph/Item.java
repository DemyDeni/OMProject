package org.om.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class Item implements Cloneable {
    int id;
    int price;
    double storageMultiplier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, storageMultiplier);
    }

    @SneakyThrows
    public Item clone() {
        return (Item) super.clone();
    }
}
