/**
 *   CartsBusBoarding - Bus Boarding Event detection project by
 *                      CARTS in IITB & UIET, Panjab University
 *
 *   Copyright (c) 2014 Shubham Chaudhary <me@shubhamchaudhary.in>
 *   Copyright (c) 2014 Tanjot Kaur <tanjot28@gmail.com>
 *
 *   This file is part of CartsBusBoarding.
 *
 *   CartsBusBoarding is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   CartsBusBoarding is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with CartsBusBoarding.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.ac.iitb.cse.cartsbusboarding;

import android.content.Context;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.utils.LogUtils;

import javax.inject.Inject;

public class PatternRecognition {
    public static final String TAG = LogUtils.makeLogTag(PatternRecognition.class);
    @Inject AccEngine accEngine;
    @Inject Context context;

    @Inject
    public PatternRecognition(AccEngine accEngine, Context context) {
        this.accEngine = accEngine;
        this.context = context;
    }

    public boolean hasBoardedBus() {
        //if machine.accuracy > threshold
        Machine machine = new Machine(accEngine, context);
        if (machine.foundStairPattern()) {
            return true;
        }
        //else if gsm distance > walking distance

        return false;
    }

    public double getAvg() {
        Machine machine = new Machine(accEngine, context);
        return machine.getAvgIdx();
    }
}
