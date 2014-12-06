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

package in.ac.iitb.cse.cartsbusboarding.gsm;

import android.location.Location;

/**
 * Created by chaudhary on 10/23/14.
 */
public class GsmData {
    /*
     * Data Encapsulation
     */
    public Location location;
    /** Get the latitude, in degrees. */
    public double gsmLat;
    /** Get the longitude, in degrees. */
    public double gsmLong;
    /** Get the estimated accuracy of this location, in meters. */
    public float gsmAccuracy;

    @Override
    public String toString() {
        String base = "GSM Data: (";
        base += gsmLat + ", ";
        base += gsmLong + ", ";
        base += gsmAccuracy;
        base += ")";
        return base;
    }

    /* Getters */
    public double getGsmLat() {
        return gsmLat;
    }

    public double getGsmLong() {
        return gsmLong;
    }

    public float getGsmAccuracy() {
        return gsmAccuracy;
    }

}
