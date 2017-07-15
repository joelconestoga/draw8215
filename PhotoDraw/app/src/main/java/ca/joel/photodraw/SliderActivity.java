package ca.joel.photodraw;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

//Java class for the second activity, which shows all saved drawings
public class SliderActivity extends AppCompatActivity {

    ViewPager vpgSlider;
    SwipeAdapter adapter;

    //Initialization
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        setupSlider();
        loadDrawings();
    }

    //Setup the slider with the swipe adapter
    private void setupSlider() {
        vpgSlider = (ViewPager) findViewById(R.id.vpgSlider);
        adapter = new SwipeAdapter(this);
        vpgSlider.setAdapter(adapter);
    }

    //Load all saved drawings
    private void loadDrawings() {

        //Access the directory and read all drawings
        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File targetDirector = new File(path);
        File[] files = targetDirector.listFiles();

        //Insert each drawing into the adapter, to be presented on the slider
        for (File file : files){
            Uri photoURI = ImageUtils.getUri(this, file);

            adapter.add(photoURI);
            adapter.notifyDataSetChanged();
            vpgSlider.setCurrentItem(adapter.getCount());
        }

    }
}
