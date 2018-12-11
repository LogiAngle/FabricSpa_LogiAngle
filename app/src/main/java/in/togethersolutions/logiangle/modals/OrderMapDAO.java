package in.togethersolutions.logiangle.modals;

public class OrderMapDAO {

    String orderNumber;
    String customerName;
    String orderStatus;
    String orderScheduleDateTime;
    String latitude;
    String longitude;

    public OrderMapDAO(String orderNumber, String customerName, String latitude, String longitude, String orderStatus, String orderScheduleDateTime) {
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orderStatus = orderStatus;
        this.orderScheduleDateTime = orderScheduleDateTime;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderScheduleDateTime() {
        return orderScheduleDateTime;
    }

}
