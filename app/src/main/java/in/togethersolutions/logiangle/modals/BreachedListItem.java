package in.togethersolutions.logiangle.modals;

public class BreachedListItem {

    public static final int PICKUP=0;
    public static final int PD=1;
    public String orderNumber;
    public String orderStatusID;
    public String orderStatusName;
    public String orderTaskTypeID;
    public String OrderTaskTypeName;
    public String totalAmount;
    public String crmID;
    public String firstName;
    public String mobileNumber;
    public String address;
    public String latitude;
    public String longitude;
    public String pDateTime;
    public int Type;
    public String pickUpClientName;
    public String pickUpClientAddress;
    public String pickUpClientMobileNumber;
    public String pickUpLatitude;
    public String pickUpLongitude;
    public String deliveryClientName;
    public String deliveryClientAddress;
    public String deliveryClientMobileNumber;
    public String deliveryLatitude;
    public String deliveryLongitude;
    public String pickUpDateTIme;
    public String deliveryDateTime;
    public String notes;
    public String timeSlot;

    public BreachedListItem(String orderNumber, String orderStatusID, String orderStatusName, String orderTaskTypeID, String OrderTaskTypeName, String totalAmount, String crmID, String firstName, String mobileNumber, String address, String latitude, String longitude, String pDateTime, int type, String pickUpClientName, String pickUpClientAddress, String pickUpClientMobileNumber, String pickUpLatitude, String pickUpLongitude, String deliveryClientName, String deliveryClientAddress, String deliveryClientMobileNumber, String deliveryLatitude, String deliveryLongitude, String pickUpDateTIme, String deliveryDateTime,String notes,String timeSlot) {
        this.orderNumber = orderNumber;
        this.orderStatusID = orderStatusID;
        this.orderStatusName = orderStatusName;
        this.orderTaskTypeID = orderTaskTypeID;
        this.OrderTaskTypeName = OrderTaskTypeName;
        this.totalAmount = totalAmount;
        this.crmID = crmID;
        this.firstName = firstName;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pDateTime = pDateTime;
        Type = type;
        this.pickUpClientName = pickUpClientName;
        this.pickUpClientAddress = pickUpClientAddress;
        this.pickUpClientMobileNumber = pickUpClientMobileNumber;
        this.pickUpLatitude = pickUpLatitude;
        this.pickUpLongitude = pickUpLongitude;
        this.deliveryClientName = deliveryClientName;
        this.deliveryClientAddress = deliveryClientAddress;
        this.deliveryClientMobileNumber = deliveryClientMobileNumber;
        this.deliveryLatitude = deliveryLatitude;
        this.deliveryLongitude = deliveryLongitude;
        this.pickUpDateTIme = pickUpDateTIme;
        this.deliveryDateTime = deliveryDateTime;
        this.notes = notes;
        this.timeSlot = timeSlot;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderStatusID() {
        return orderStatusID;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public String getOrderTaskTypeID() {
        return orderTaskTypeID;
    }

    public String getOrderTaskTypeName() {
        return OrderTaskTypeName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getCrmID() {
        return crmID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getpDateTime() {
        return pDateTime;
    }

    public int getType() {
        return Type;
    }

    public String getPickUpClientName() {
        return pickUpClientName;
    }

    public String getPickUpClientAddress() {
        return pickUpClientAddress;
    }

    public String getPickUpClientMobileNumber() {
        return pickUpClientMobileNumber;
    }

    public String getPickUpLatitude() {
        return pickUpLatitude;
    }

    public String getPickUpLongitude() {
        return pickUpLongitude;
    }

    public String getDeliveryClientName() {
        return deliveryClientName;
    }

    public String getDeliveryClientAddress() {
        return deliveryClientAddress;
    }

    public String getDeliveryClientMobileNumber() {
        return deliveryClientMobileNumber;
    }

    public String getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public String getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public String getPickUpDateTIme() {
        return pickUpDateTIme;
    }

    public String getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public String getTimeSlot() {
        return timeSlot;
    }
}
