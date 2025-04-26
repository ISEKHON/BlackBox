package xyz.black.box;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import top.niunaijun.bcore.BlackBoxCore;
import xyz.black.box.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    String BGMI = "com.pubg.imobile";
    String X = "com.twitter.android";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BlackBoxCore.get().doCreate();

        binding.btnBgmi.setText(BlackBoxCore.get().isInstalled(BGMI, 0) ? "Open" : "Clone");
        binding.btnBgmi.setOnClickListener(view -> {
            if (!BlackBoxCore.get().isInstalled(BGMI, 0)) {
                BlackBoxCore.get().installPackageAsUser(BGMI, 0);
                binding.btnBgmi.setText(BlackBoxCore.get().isInstalled(BGMI, 0) ? "Open" : "Clone");

            }
            BlackBoxCore.get().launchApk("com.pubg.imobile", 0);
        });

        binding.btnX.setText(BlackBoxCore.get().isInstalled(X, 0) ? "Open" : "Clone");
        binding.btnX.setOnClickListener(view -> {
            if (!BlackBoxCore.get().isInstalled(X, 0)) {
                BlackBoxCore.get().installPackageAsUser(X, 0);
                binding.btnX.setText(BlackBoxCore.get().isInstalled(X, 0) ? "Open" : "Clone");

            }
            BlackBoxCore.get().launchApk(X, 0);
        });
    }


}