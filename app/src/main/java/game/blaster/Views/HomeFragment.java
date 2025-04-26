package game.blaster.Views;

import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import game.blaster.R;

public class HomeFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
		View inflate = inflater.inflate(R.layout.fragment_home, viewGroup, false);
		return inflate;
	}

}