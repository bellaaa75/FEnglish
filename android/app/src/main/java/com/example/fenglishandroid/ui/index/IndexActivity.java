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
import com.example.fenglishandroid.fragment.LearningPlazaFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class IndexActivity extends AppCompatActivity {

    private ViewPager2 pager;
    private BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_index);

    /* ===== 状态栏文字黑色（API 21+） ===== */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    pager = findViewById(R.id.content_pager);
    nav = findViewById(R.id.bottom_nav);

    /* ---------- 1. ViewPager2：设置4个Fragment ---------- */
        pager.setAdapter(new FragmentStateAdapter(this) {
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new LearningPlazaFragment(); // 首页/广场
                case 1:
                    /*return new BookFragment();*/     // 词书
                case 2:
                    /*return new FavoriteFragment();*/ // 收藏
                case 3:
                    return new MeFragment();       // 我的
                default:
                    return new LearningPlazaFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4; // 4个页面
        }
    });

    // 设置预加载页面数量，提升体验
        pager.setOffscreenPageLimit(3);

    /* ---------- 2. 底部导航点击事件 ---------- */
        nav.setOnItemSelectedListener(item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_plaza) {
            pager.setCurrentItem(0, false);
            return true;
        }
        if (itemId == R.id.nav_book) {
            pager.setCurrentItem(1, false);
            return true;
        }
        if (itemId == R.id.nav_fav) {
            pager.setCurrentItem(2, false);
            return true;
        }
        if (itemId == R.id.nav_me) {
            pager.setCurrentItem(3, false);
            return true;
        }
        return false;
    });

    /* ---------- 3. 滑动联动 ---------- */
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    nav.setSelectedItemId(R.id.nav_plaza);
                    break;
                case 1:
                    nav.setSelectedItemId(R.id.nav_book);
                    break;
                case 2:
                    nav.setSelectedItemId(R.id.nav_fav);
                    break;
                case 3:
                    nav.setSelectedItemId(R.id.nav_me);
                    break;
            }
        }
    });

    /* ---------- 4. 替换底部导航字体为 simhei.ttf ---------- */
    setBottomNavFont(nav);
}

/* ====================== 字体替换工具 ====================== */
private void setBottomNavFont(BottomNavigationView nav) {
    new Thread(() -> {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/simhei.ttf");
        runOnUiThread(() -> iterateSetTypeface(nav, tf));
    }).start();
}

private void iterateSetTypeface(ViewGroup parent, Typeface tf) {
    for (int i = 0; i < parent.getChildCount(); i++) {
        View v = parent.getChildAt(i);
        if (v instanceof TextView) {
            ((TextView) v).setTypeface(tf, Typeface.BOLD);
        } else if (v instanceof ViewGroup) {
            iterateSetTypeface((ViewGroup) v, tf);
        }
    }
}

/* ====================== 创建其他Fragment的占位类 ====================== */

  /*  // 词书Fragment
    public static class BookFragment extends Fragment {
        public BookFragment() {
            super(R.layout.fragment_book); // 请创建对应的布局文件
        }
    }

    // 收藏Fragment
    public static class FavoriteFragment extends Fragment {
        public FavoriteFragment() {
            super(R.layout.fragment_favorite); // 请创建对应的布局文件
        }
    }*/

// 我的Fragment
public static class MeFragment extends Fragment {
    public MeFragment() {
        super(R.layout.fragment_me); // 请创建对应的布局文件
    }
}
}

