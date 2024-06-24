package org.om.ga;

import lombok.Getter;
import lombok.Setter;
import org.om.graph.Item;
import org.om.graph.Storage;

@Getter
@Setter
public class Task {
    Storage from;
    Storage to;
    Item item;
    Integer itemNum;
}
