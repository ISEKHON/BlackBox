package game.blaster;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import game.blaster.Views.GamesFragment;
import game.blaster.Views.HomeFragment;
import top.niunaijun.bcore.BlackBoxCore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static BottomNavigationView bottomNavigationView;
    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            BlackBoxCore.get().doCreate();


        MaterialToolbar tbar = findViewById(R.id.toolbar);

        onNavigationItemSelectedListener = item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                tbar.setTitle("Home");
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
                beginTransaction.replace(R.id.pager, homeFragment);
                beginTransaction.commit();
                return true;
            } else {
                tbar.setTitle("Games");
                GamesFragment gamesFragment = new GamesFragment();
                FragmentTransaction beginTransaction2 = MainActivity.this.getSupportFragmentManager()
                        .beginTransaction();
                beginTransaction2.replace(R.id.pager, gamesFragment);
                beginTransaction2.commit();
                return true;
            }
        };

        ((BottomNavigationView) findViewById(R.id.bottom_navigation))
                .setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        loadFragment(new HomeFragment());
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.pager, fragment);
        beginTransaction.addToBackStack(null);
        beginTransaction.commit();
    }
}