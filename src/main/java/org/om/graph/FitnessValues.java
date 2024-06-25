package org.om.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FitnessValues {
    private Integer moveItemNum = 10;
    private Double moveItemChance = 0.15d;
    private Double itemPriceMod = 1d;
    private Double manufacturerStorageCost = 1d;
    private Double distributorStorageCost = 2d;
    private Double retailerStorageCost = 3d;
    private Double moveItemMod = 0.01d;
    private Double noAvailableItemsToTakeMod = 0.01d;
    private Double availableItemsToTakeMod = 1d;
}
