package failed.com.realistic.metrolinktimes;

import java.util.Comparator;


class StationListComparator implements Comparator<Station>{

    @Override
    public int compare(Station lhs, Station rhs) {
        return lhs.name.compareTo(rhs.name);
    }
}
class DistanceComparator implements Comparator<Station>{

    @Override
    public int compare(Station lhs, Station rhs) {
        return lhs.distance>rhs.distance?1:lhs.distance==rhs.distance?0:-1;
    }
}

class NorthtoSouthComparator implements Comparator<Station>{

    @Override
    public int compare(Station lhs, Station rhs) {
        return lhs.longitude.compareTo(rhs.longitude);
    }
}

class SouthtoNorthComparator implements Comparator<Station>{

    @Override
    public int compare(Station lhs, Station rhs) {
        return rhs.longitude.compareTo(lhs.longitude);
    }
}