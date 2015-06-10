package in.ac.iitb.cse.cartsbusboarding.controllers;

import in.ac.iitb.cse.cartsbusboarding.data.AccDisplayData;
import in.ac.iitb.cse.cartsbusboarding.data.GsmDisplayData;

public interface AccDisplayController {
    void displayAcc(AccDisplayData data);
    void displayGsm(GsmDisplayData data);
}
