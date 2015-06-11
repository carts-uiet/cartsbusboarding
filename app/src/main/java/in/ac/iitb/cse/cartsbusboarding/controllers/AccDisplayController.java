package in.ac.iitb.cse.cartsbusboarding.controllers;

import in.ac.iitb.cse.cartsbusboarding.models.AccDisplayData;
import in.ac.iitb.cse.cartsbusboarding.models.GsmDisplayData;

public interface AccDisplayController {
    void displayAcc(AccDisplayData data);
    void displayGsm(GsmDisplayData data);
}
