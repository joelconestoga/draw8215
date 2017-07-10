package ca.joel.slider;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomSwipeAdapter extends PagerAdapter{

    private int[] image_resources = {R.drawable.bob1, R.drawable.bob2};
    private Context context;
    private LayoutInflater inflater;


    public CustomSwipeAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.swipe_layout, container, false);

        ImageView imvImage = (ImageView) item.findViewById(R.id.imvImage);
        TextView txvTitle = (TextView) item.findViewById(R.id.txvTitle);

        imvImage.setImageResource(image_resources[position]);
        txvTitle.setText("Image: " + position);

        container.addView(item);

        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getCount() {
        return image_resources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout)object;
    }
}
