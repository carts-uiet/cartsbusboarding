package in.ac.iitb.cse.cartsbusboarding.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(suppressConstructorProperties = true)
@Getter
public class AccDisplayData {
    private final double mean;
    private final double std;
    private final double dcComp;
    private final double energy;
    private final double entropy;
    private final double avg;
}
