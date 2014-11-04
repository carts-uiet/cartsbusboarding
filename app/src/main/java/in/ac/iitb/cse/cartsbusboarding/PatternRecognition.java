package in.ac.iitb.cse.cartsbusboarding;

import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;

/**
 * Created by chaudhary on 10/30/14.
 */
public class PatternRecognition {
    private AccEngine accEngine;

    public PatternRecognition(AccEngine accEngine) {
        this.accEngine = accEngine;
    }

    public boolean hasBoardedBus() {
        //if machine.accuracy > threshold
        Machine machine = new Machine(accEngine);
        if(machine.foundStairPattern()){
            return true;
        }
        //else if gsm distance > walking distance

        return false;
    }
}
