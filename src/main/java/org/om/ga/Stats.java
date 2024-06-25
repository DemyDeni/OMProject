package org.om.ga;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Stats {
    public Double soldPrice = 0d;
    public Integer ordersFailed = 0;
    public Double storageCost = 0d;
    public Double deliveryCost = 0d;
}
