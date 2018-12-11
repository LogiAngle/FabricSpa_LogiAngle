package in.togethersolutions.logiangle.modals;

public class MapListItems {

    String name;
    String orderId;
    double latitude;
    double longitude;

    public MapListItems(String name, String orderId, double latitude, double longitude) {
        this.name = name;
        this.orderId = orderId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
