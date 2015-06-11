package in.ac.iitb.cse.cartsbusboarding.models;

import in.ac.iitb.cse.cartsbusboarding.gsm.GsmData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(suppressConstructorProperties = true)
@Getter
public class GsmDisplayData {
    float speed;
    double mySpeed;
    GsmData gsmData;
}
