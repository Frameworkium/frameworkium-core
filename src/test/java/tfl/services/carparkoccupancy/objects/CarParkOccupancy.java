package tfl.services.carparkoccupancy.objects;

/**
 * Created by robertgates55 on 25/04/2016.
 */
public class CarParkOccupancy {
    public String id;
    public Bay[] bays;
    public String name;

    public class Bay{
        public String bayType;
        public int free;
    }
}
