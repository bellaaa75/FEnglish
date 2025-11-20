package com.example.fenglishandroid.ui.index;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.fragment.HomeGridFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class IndexActivity extends AppCompatActivity {

    private ViewPager2 pager;
    private BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        /* =====  新增：状态栏文字黑色（API 21+） ===== */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);                      // 背景白色
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);                       // 文字黑色
        }


        pager = findViewById(R.id.content_pager);
        nav   = findViewById(R.id.bottom_nav);

        /* ---------- 1. ViewPager2：目前只放首页 ---------- */
        pager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return new HomeGridFragment();
            }
            @Override
            public int getItemCount() {
                return 1;
            }
        });

        /* ---------- 2. 底部导航点击事件 ---------- */
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_plaza) {
                pager.setCurrentItem(0, true);
                return true;
            }
            if (itemId == R.id.nav_book)  { return true; }
            if (itemId == R.id.nav_fav)   { return true; }
            if (itemId == R.id.nav_me)    { return true; }
            return false;
        });

        /* ---------- 3. 滑动联动 ---------- */
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) nav.setSelectedItemId(R.id.nav_plaza);
            }
        });

        /* ---------- 4. 替换底部导航字体为 simhei.ttf ---------- */
        setBottomNavFont(nav);
    }

    /* ====================== 字体替换工具 ====================== */
    private void setBottomNavFont(BottomNavigationView nav) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/simhei.ttf");
        iterateSetTypeface(nav, tf);
    }

    private void iterateSetTypeface(ViewGroup parent, Typeface tf) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof TextView) {
//                ((TextView) v).setTypeface(tf);
                ((TextView) v).setTypeface(tf, Typeface.BOLD);
            } else if (v instanceof ViewGroup) {
                iterateSetTypeface((ViewGroup) v, tf);
            }
        }
    }
}