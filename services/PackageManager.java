package services;

import java.util.HashMap;

public class PackageManager {
protected static HashMap<Integer,Package> packageDict;

    public static Package retrievePackagebyReservationID(int id) {
        ReservationsManagers.reservationsDictGenerator();
        Package ret = null;
        for(int i : ReservationsManagers.reservationsDict.keySet()){
            if(i==id){
                ret = ReservationsManagers.reservationsDict.get(i).getRelatedPackage();
                return ret;
            }
        }
        return null;
    }
}
