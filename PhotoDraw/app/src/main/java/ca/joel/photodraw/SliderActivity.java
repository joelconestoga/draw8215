package ca.joel.photodraw;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class SliderActivity extends AppCompatActivity {

    ViewPager vpgSlider;
    SwipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        setupSlider();
        loadDrawings();
    }

    private void setupSlider() {
        vpgSlider = (ViewPager) findViewById(R.id.vpgSlider);
        adapter = new SwipeAdapter(this);
        vpgSlider.setAdapter(adapter);
    }

    private void loadDrawings() {

        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();

        File targetDirector = new File(path);

        File[] files = targetDirector.listFiles();

        for (File file : files){
            Uri photoURI = ImageUtils.getUri(this, file);

            adapter.add(photoURI);
            adapter.notifyDataSetChanged();
            vpgSlider.setCurrentItem(adapter.getCount());
        }

    }
}
