package in.togethersolutions.logiangle.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.util.List;

import in.togethersolutions.logiangle.R;
import in.togethersolutions.logiangle.modals.ListPODImages;

public class PODImageAdapter extends RecyclerView.Adapter<PODImageAdapter.ViewHolder> {


    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;
    Bitmap bitmap;

    List<ListPODImages> listPODImage;

    public PODImageAdapter(List<ListPODImages> listPODImage, Context context)
    {
        this.listPODImage = listPODImage;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pod_image_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Getting the particular item from the list
        ListPODImages listPODImages = listPODImage.get(position);
        // getImage(listPODImages.getImageURL());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes = baos.toByteArray();
        imageBytes = Base64.decode(listPODImages.getImageURL(), Base64.DEFAULT);
        // Toast.makeText(context,listPODImages.getImageURL(),Toast.LENGTH_LONG).show();
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
       // System.out.println(listPODImages.getImageURL());
        holder.imageView.setImageBitmap(decodedImage);
        holder.textViewName.setText(listPODImages.getImageName());
    }


    @Override
    public int getItemCount() {
        return listPODImage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView textViewName;
        //Initializing Views
        @SuppressLint("WrongViewCast")
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imagePOD);
            textViewName = (TextView)itemView.findViewById(R.id.textImageName);
        }
    }
}
