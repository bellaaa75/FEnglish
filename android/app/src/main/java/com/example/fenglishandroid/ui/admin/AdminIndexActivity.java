package com.example.fenglishandroid.ui.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.fragment.admin.AdminAccountFragment;
import com.example.fenglishandroid.fragment.admin.AdminSettingFragment;
import com.example.fenglishandroid.fragment.admin.AdminWordFragment;
import com.example.fenglishandroid.fragment.admin.AdminWordBookFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.ArrayList;
import java.util.List;

public class AdminIndexActivity extends AppCompatActivity {

    private ViewPager2 adminViewPager;
    private BottomNavigationView adminBottomNav;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_index);

        // 初始化控件（和普通用户一致）
        initView();
        // 初始化Fragment（替换为管理员的3个空白Fragment）
        initFragment();
        // 绑定ViewPager2和底部导航（交互和普通用户一致）
        bindViewPagerAndNav();
    }

    private void initView() {
        adminViewPager = findViewById(R.id.admin_view_pager);
        adminBottomNav = findViewById(R.id.admin_bottom_nav);
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        // 顺序：用户账号管理（默认）→ 单词书管理 → 单词管理-管理员个人账号管理
        fragmentList.add(new AdminSettingFragment());
        fragmentList.add(new AdminWordBookFragment());
        fragmentList.add(new AdminWordFragment());
        fragmentList.add(new AdminAccountFragment());
    }

    private void bindViewPagerAndNav() {
        // 设置ViewPager2适配器（和普通用户一致）
        adminViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return fragmentList.size();
            }
        });

        // 禁止ViewPager2滑动（和普通用户一致）
        adminViewPager.setUserInputEnabled(false);

        // 底部导航点击切换Fragment（和普通用户一致的交互）
        adminBottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_admin_account) {
                    adminViewPager.setCurrentItem(0, false);
                    return true;
                } else if (itemId == R.id.menu_admin_wordbook) {
                    adminViewPager.setCurrentItem(1, false);
                    return true;
                } else if (itemId == R.id.menu_admin_word) {
                    adminViewPager.setCurrentItem(2, false);
                    return true;
                } else if (itemId == R.id.menu_admin_setting) { // 新增：第4个选项
                    adminViewPager.setCurrentItem(3, false);
                    return true;
                }
                return false;
            }
        });

        // ViewPager2切换同步底部导航（和普通用户一致）
        adminViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        adminBottomNav.setSelectedItemId(R.id.menu_admin_account);
                        break;
                    case 1:
                        adminBottomNav.setSelectedItemId(R.id.menu_admin_wordbook);
                        break;
                    case 2:
                        adminBottomNav.setSelectedItemId(R.id.menu_admin_word);
                        break;
                    case 3:
                        adminBottomNav.setSelectedItemId(R.id.menu_admin_setting);
                        break;
                }
            }
        });

        // 默认选中第一个（账号管理）
        adminBottomNav.setSelectedItemId(R.id.menu_admin_account);
    }
}