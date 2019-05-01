package com.cksolutions.rosary.prayers.rosary;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cksolutions.rosary.R;
import com.cksolutions.rosary.prayers.common.PrayerAlbum;
import com.cksolutions.rosary.prayers.common.PrayerAlbumsAdapter;
import com.cksolutions.rosary.prayers.rosary.normalenglish.NormalEnglishRosary;
import com.cksolutions.rosary.prayers.rosary.normalmalayalam.NormalMalayalamRosary;
import com.cksolutions.rosary.util.PrayerAlbumRecyclerListner;

import java.util.ArrayList;
import java.util.List;

public class NormalRosaryType extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PrayerAlbumsAdapter adapter;
    private List<PrayerAlbum> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_rosary_type);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        adapter = new PrayerAlbumsAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new PrayerAlbumRecyclerListner(getApplicationContext(), recyclerView, new PrayerAlbumRecyclerListner.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                PrayerAlbum album = albumList.get(position);
                switch(album.getName())
                {
                    case "English Rosary":startActivity(new Intent(NormalRosaryType.this, NormalEnglishRosary.class));
                        break;
                    case "മലയാളം ജപമാല" :
                        Toast.makeText(getApplicationContext(), "Under Construction",Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(NormalRosaryType.this, NormalMalayalamRosary.class));
                        break;
               }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        preparePrayerAlbums();

        try {
            Glide.with(this).load(R.drawable.rosaryimage).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Normal Rosary");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Adding few albums for testing
     */
    private void preparePrayerAlbums() {
        int[] covers = new int[]{
                R.drawable.englishrosary,
                R.drawable.malayalamrosary};

        PrayerAlbum a = new PrayerAlbum("English Rosary", 4, covers[0]);
        albumList.add(a);

        a = new PrayerAlbum("മലയാളം ജപമാല", 4, covers[1]);
        albumList.add(a);

        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
