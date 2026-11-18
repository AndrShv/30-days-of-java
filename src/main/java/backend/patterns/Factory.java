package backend.patterns;

public class Factory {
}
enum DeliveryType{
    TRUCK,
    SHIP
}
class UnKnownTypeOfDeliveryException extends Exception{
    public UnKnownTypeOfDeliveryException(){
        super("Unknown Type Of Delivery");
    }

}

class Logistics{

    public Object getDeliveryInfo(DeliveryType deliveryType){
        if(deliveryType == DeliveryType.TRUCK){
            return new TruckDelivery();
        }
        if(deliveryType == DeliveryType.SHIP){
            return new ShipDelivery();
        }
        else {
            return new UnKnownTypeOfDeliveryException();
        }
    }
}

abstract class DeliveryInfo{
    String delivery_info() {
        return "Info About Delivery";
    }

}


class TruckDelivery extends DeliveryInfo {
    @Override
    public String delivery_info(){
        return "Deliver With Truck";
    }
}

class ShipDelivery extends DeliveryInfo {
    @Override
    public String delivery_info(){
        return "Deliver With Ship";
    }
}