package trilodi.ru.free_lanceru.UI;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import trilodi.ru.free_lanceru.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PortfolioActivityFragment extends Fragment {

    public PortfolioActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }
}
