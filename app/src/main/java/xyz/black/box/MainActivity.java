package xyz.black.box;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import top.niunaijun.blackbox.BlackBoxCore;
import xyz.black.box.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String BGMI = "com.pubg.imobile";
    private static final String X = "com.twitter.android";
    private static final String GMS_PACKAGE = "com.google.android.gms";

    private ActivityMainBinding binding;
    private String currentPackageName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set status bar color to match toolbar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int primaryColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, ContextCompat.getColor(this, android.R.color.black));
        window.setStatusBarColor(primaryColor);

        // Initialize views
        MaterialButton btnBgmi = binding.btnBgmi;
        MaterialButton btnX = binding.btnX;
        MaterialButton btnInstall = binding.btnInstall;
        MaterialButton btnClearData = binding.btnClearData;
        MaterialButton btnUninstall = binding.btnUninstall;
        TextInputEditText etPackageName = binding.etPackageName;
        SwitchMaterial switchGms = binding.switchGms;

        // Update BGMI button state
        updateButtonState(btnBgmi, BGMI);

        // BGMI button click listener
        btnBgmi.setOnClickListener(view -> {
            if (!BlackBoxCore.get().isInstalled(BGMI, 0)) {
                try {
                    BlackBoxCore.get().installPackageAsUser(BGMI, 0);
                    updateButtonState(btnBgmi, BGMI);
                    updateCustomAppState(BGMI);
                    Toast.makeText(this, "BGMI installed successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to install BGMI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                BlackBoxCore.get().launchApk(BGMI, 0);
            }
        });

        // Update X button state
        updateButtonState(btnX, X);

        // X button click listener
        btnX.setOnClickListener(view -> {
            if (!BlackBoxCore.get().isInstalled(X, 0)) {
                try {
                    BlackBoxCore.get().installPackageAsUser(X, 0);
                    updateButtonState(btnX, X);
                    updateCustomAppState(X);
                    Toast.makeText(this, "X (Twitter) installed successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to install X (Twitter): " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                BlackBoxCore.get().launchApk(X, 0);
            }
        });

        // Package name input listener
        etPackageName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentPackageName = s.toString().trim();
                updateCustomAppState(currentPackageName);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Install/Open button click listener
        btnInstall.setOnClickListener(view -> {
            if (currentPackageName.isEmpty()) {
                Toast.makeText(this, "Please enter a package name", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInstalled = BlackBoxCore.get().isInstalled(currentPackageName, 0);
            if (isInstalled) {
                // Launch the app if it's installed
                try {
                    BlackBoxCore.get().launchApk(currentPackageName, 0);
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to launch app: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Install the app if it's not installed
                try {
                    BlackBoxCore.get().installPackageAsUser(currentPackageName, 0);
                    updateCustomAppState(currentPackageName);
                    Toast.makeText(this, "App installed successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to install app: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Clear data button click listener
        btnClearData.setOnClickListener(view -> {
            if (currentPackageName.isEmpty()) {
                Toast.makeText(this, "Please enter a package name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!BlackBoxCore.get().isInstalled(currentPackageName, 0)) {
                Toast.makeText(this, "App is not installed", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                BlackBoxCore.get().clearPackage(currentPackageName, 0);
                Toast.makeText(this, "App data cleared successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to clear app data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Uninstall button click listener
        btnUninstall.setOnClickListener(view -> {
            if (currentPackageName.isEmpty()) {
                Toast.makeText(this, "Please enter a package name", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                BlackBoxCore.get().uninstallPackageAsUser(currentPackageName, 0);
                updateCustomAppState(currentPackageName);

                // Check if BGMI or X was uninstalled and update their buttons
                if (currentPackageName.equals(BGMI)) {
                    updateButtonState(btnBgmi, BGMI);
                }
                if (currentPackageName.equals(X)) {
                    updateButtonState(btnX, X);
                }

                Toast.makeText(this, "App uninstalled successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to uninstall app: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // GMS services switch listener
        switchGms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    BlackBoxCore.get().installGms(0);
                    Toast.makeText(this, "GMS Services installed successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to install GMS Services: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    switchGms.setChecked(false);
                }
            } else {
                try {
                    BlackBoxCore.get().uninstallGms(0);
                    Toast.makeText(this, "GMS Services uninstalled successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to uninstall GMS Services: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    switchGms.setChecked(true);
                }
            }
        });

        // Update GMS switch state
        switchGms.setChecked(BlackBoxCore.get().isInstalled(GMS_PACKAGE, 0));
    }

    private void updateButtonState(MaterialButton button, String packageName) {
        boolean isInstalled = BlackBoxCore.get().isInstalled(packageName, 0);
        button.setText(isInstalled ? "Open" : "Install");
        button.setIconResource(isInstalled ? android.R.drawable.ic_media_play : android.R.drawable.ic_menu_save);
    }

    private void updateCustomAppState( String currentPackageName) {
        if (currentPackageName.isEmpty()) {
            binding.tvAppStatus.setVisibility(View.GONE);
            binding.btnInstall.setText("Install App");
            return;
        }

        boolean isInstalled = BlackBoxCore.get().isInstalled(currentPackageName, 0);
        binding.tvAppStatus.setVisibility(View.VISIBLE);
        binding.tvAppStatus.setText(isInstalled ? "App is installed" : "App not installed");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.tvAppStatus.setTextColor(getResources().getColor(
                isInstalled ? android.R.color.holo_green_dark : android.R.color.holo_red_dark,
                getTheme()
            ));
        }
        binding.btnInstall.setText(isInstalled ? "Open" : "Install");
        binding.btnInstall.setIconResource(isInstalled ? android.R.drawable.ic_media_play : android.R.drawable.ic_menu_save);
    }
}