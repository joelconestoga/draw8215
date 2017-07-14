package ca.joel.photodraw;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SwipeAdapter extends PagerAdapter {

    private List<Uri> images = new ArrayList<>();
    private Context context;

    public SwipeAdapter(Context context) {
        this.context = context;
    }

    public void add(Uri uri) {
        images.add(uri);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.swipe_layout, container, false);

        ImageView imvImage = (ImageView) item.findViewById(R.id.imvImage);
        TextView txvTitle = (TextView) item.findViewById(R.id.txvTitle);

        imvImage.setImageURI(images.get(position));
        int index = position + 1;
        txvTitle.setText("Drawing: " + index);

        container.addView(item);

        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
