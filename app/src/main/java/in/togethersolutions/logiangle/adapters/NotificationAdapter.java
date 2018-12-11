package in.togethersolutions.logiangle.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.modals.NotificationListItem;

public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>  {
    private Context context;
    String orderNumber;
    private List<NotificationListItem> listItems;
    public NotificationAdapter(List<NotificationListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_items, parent, false);

        return new NotificationAdapter.ViewHolder(view);



    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        NotificationListItem listItem = listItems.get(position);
        holder.textNotificationMessage.setText(listItem.getNotificationTitle());
        holder.textNotificationTitle.setText(listItem.getNotifiactionMessage());
        holder.textNotificationCreateDate.setText(getDateUTCToDefaultTimeZone(listItem.getNotificationDateTime()));
    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textNotificationMessage;
        public TextView textNotificationTitle;
        public  TextView textNotificationCreateDate;
        public ViewHolder(View view) {

            super(view);

            textNotificationMessage = (TextView)view.findViewById(R.id.textViewNotificationMessage);
            textNotificationTitle = (TextView)view.findViewById(R.id.textViewNotificationTitle);
            textNotificationCreateDate = (TextView)view.findViewById(R.id.textViewNotificationDateTime);
        }
    }

    public String getDateUTCToDefaultTimeZone(String UTCDate) {
        String formattedDate=null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        try {
            date = df.parse(UTCDate);
            df.setTimeZone(TimeZone.getDefault());
            formattedDate = df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
