package com.geosoftware.victor.flipview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;


public class MainActivity extends ActionBarActivity {

    ViewPager viewPager;
    LinearLayout carouselLinearLayout;
    PagerAdapter pagerAdapter;

    String URI_1 = "https://simplyrec.m-shop.mobi/mshop/services/image/catalog/056/056_P_1.PNG";
    String URI_2 = "https://simplyrec.m-shop.mobi/mshop/services/image/catalog/056/056_P_2.PNG";
    String URI_3 = "https://simplyrec.m-shop.mobi/mshop/services/image/catalog/056/056_P_3.PNG";
    String URI_4 = "https://simplyrec.m-shop.mobi/mshop/services/image/catalog/056/056_P_4.PNG";
    ArrayList<String> uriArrayList = new ArrayList<>();

    ArrayList<Drawable> drawableArrayList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        carouselLinearLayout = (LinearLayout) findViewById(R.id.carousel_linear_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new DepthPageTransformer());



        // ----- Load carousel
        uriArrayList.add(URI_1);
        uriArrayList.add(URI_2);
        uriArrayList.add(URI_3);
        uriArrayList.add(URI_4);

        for(int i = 0; i < uriArrayList.size(); i++){
            addViewToCarousel(uriArrayList.get(i));
        }


        // ----- Services execution
        new LoadDrawableTask().execute();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.zoom:
                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                break;
            case R.id.depth:
                viewPager.setPageTransformer(true, new DepthPageTransformer());
                break;
        }

        return super.onOptionsItemSelected(item);
    }








//----- FRAGMENTS ------------------------------------------------------------
    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{

        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Log.i("", "Pasa por getItem: " + i);
//            return new ScreenSlidePageFragment().newInstance(drawableArrayList.get(i));

            switch (i){
                case 0:
                    return new FirstScreenSlidePageFragment().newInstance(drawableArrayList.get(0));

                default:

                    return new ScreenSlidePageFragment().newInstance(drawableArrayList.get(i));
            }
        }

        @Override
        public int getCount() {
            return drawableArrayList.size();
        }

    }


    public static class FirstScreenSlidePageFragment extends Fragment{

        View rootView;
        ImageView imageView;
        PhotoViewAttacher photoViewAttacher;


        static Drawable drawable;

        static FirstScreenSlidePageFragment newInstance(Drawable receivedDrawable){
            FirstScreenSlidePageFragment fragment = new FirstScreenSlidePageFragment();
            drawable = receivedDrawable;

            return  fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.imageView);


            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            imageView.setImageDrawable(drawable);
            photoViewAttacher = new PhotoViewAttacher(imageView);
        }
    }


    public static class ScreenSlidePageFragment extends Fragment{

        View rootView;
        ImageView imageView;
        PhotoViewAttacher photoViewAttacher;


        static Drawable drawable;

        static ScreenSlidePageFragment newInstance(Drawable receivedDrawable){
            ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
            drawable = receivedDrawable;

            return  fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.imageView);


            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            imageView.setImageDrawable(drawable);
            photoViewAttacher = new PhotoViewAttacher(imageView);
            photoViewAttacher.setScale(1f);
        }
    }


    //----- Pager animations ------------------------------------------------------------
    public class ZoomOutPageTransformer implements ViewPager.PageTransformer{

        private static final float MIN_SCALE = 0.7f;
        private static final float MIN_ALPHA = 0.5f;


        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if(position < -1){ //Left page
                view.setAlpha(0);
            }else if(position <= 1){ // Current page
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;

                if(position < 0){
                    view.setTranslationX(horzMargin - vertMargin / 2);
                }else{
                  view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);


                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                        (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }else{ // Right page
                view.setAlpha(0);
            }
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer{

        private static final float MIN_SCALE = 0.5f;

        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if(position < -1){ // Left page
                view.setAlpha(0);

            }else if(position <= 0){ // Current page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            }else if(position <= 1){ // Right page
                view.setAlpha(1 - position);
                view.setTranslationX(pageWidth * (-position));

                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            }else{
                view.setAlpha(0);
            }
        }
    }


//----- SERVICES ------------------------------------------------------------
    private class LoadDrawableTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            for(int i = 0; i < uriArrayList.size(); i++){
                try{
                    InputStream is = (InputStream) new URL(uriArrayList.get(i)).getContent();
                    Drawable drawable = Drawable.createFromStream(is, "drawable");
                    drawableArrayList.add(drawable);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pagerAdapter.notifyDataSetChanged();

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return null;
        }
    }





//----- METHODS ------------------------------------------------------------
    public void addViewToCarousel(String url){
        View rootView = View.inflate(getApplicationContext(), R.layout.fragment_screen_slide_page, null);
        rootView.setPadding(0, 0, 10, 0);

        ImageView imgFoto = (ImageView) rootView.findViewById(R.id.imageView);
        Picasso.with(getApplicationContext()).load(url).resize(100, 100).into(imgFoto);

        carouselLinearLayout.addView(rootView);
    }

    static Bitmap decodeSampledBitmapFromResource(Resources res, String filePath, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
