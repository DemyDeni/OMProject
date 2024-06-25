package org.om.ga;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Stats {
    Double soldPrice = 0d;
    Integer ordersFailed = 0;
    Double storageCost = 0d;
    Double deliveryCost = 0d;
}
