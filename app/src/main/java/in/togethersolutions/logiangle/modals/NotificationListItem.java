package in.togethersolutions.logiangle.modals;

public class NotificationListItem {

    private String notifiactionMessage;
    private String notificationTitle;
    private int notificationIsSeen;
    private String notificationDateTime;

    public NotificationListItem(String notifiactionMessage, String notificationTitle, int notificationIsSeen, String notificationDateTime) {
        this.notifiactionMessage = notifiactionMessage;
        this.notificationTitle = notificationTitle;
        this.notificationIsSeen = notificationIsSeen;
        this.notificationDateTime = notificationDateTime;
    }

    public String getNotifiactionMessage() {
        return notifiactionMessage;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationDateTime() {
        return notificationDateTime;
    }

    public int getNotificationIsSeen() {
        return notificationIsSeen;
    }
}
