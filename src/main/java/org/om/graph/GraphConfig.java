package org.om.graph;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GraphConfig {
    private Integer itemsNum;
    private Integer itemMinPrice;
    private Integer itemMaxPrice;
    private Double itemMinMultiplier;
    private Double itemMaxMultiplier;
    private Integer personsNum;
    private Integer personsMinOrders;
    private Integer personsMaxOrders;
    private Integer retailersNum;
    private Integer distributorsNum;
    private Integer manufacturerItemsPerDay;
}
