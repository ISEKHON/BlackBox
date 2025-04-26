package game.blaster.Views;

import android.hardware.camera2.params.BlackLevelPattern;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import game.blaster.R;
import top.niunaijun.bcore.BlackBoxCore;

public class GamesFragment extends Fragment {

    MaterialButton bgmi, twitter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = inflater.inflate(R.layout.fragment_games, viewGroup, false);
        bgmi = inflate.findViewById(R.id.cloneBGMI);
        twitter = inflate.findViewById(R.id.cloneTwitter);

        bgmi.setText(BlackBoxCore.get().isInstalled("com.pubg.imobile", 0) ? "LAUNCH" : "CLONE");

        bgmi.setOnClickListener(view -> {
            if (!BlackBoxCore.get().isInstalled("com.pubg.imobile", 0)) {
                BlackBoxCore.get().installPackageAsUser("com.pubg.imobile", 0);
                bgmi.setText(BlackBoxCore.get().isInstalled("com.pubg.imobile", 0) ? "LAUNCH" : "CLONE");

            }
            BlackBoxCore.get().launchApk("com.pubg.imobile",0);
        });

        twitter.setText(BlackBoxCore.get().isInstalled("com.twitter.android", 0) ? "LAUNCH" : "CLONE");

        twitter.setOnClickListener(view -> {
            if (!BlackBoxCore.get().isInstalled("com.twitter.android", 0)) {
                BlackBoxCore.get().installPackageAsUser("com.twitter.android", 0);
                twitter.setText(BlackBoxCore.get().isInstalled("com.twitter.android", 0) ? "LAUNCH" : "CLONE");

            }
            BlackBoxCore.get().launchApk("com.twitter.android",0);
        });
        return inflate;
    }

}