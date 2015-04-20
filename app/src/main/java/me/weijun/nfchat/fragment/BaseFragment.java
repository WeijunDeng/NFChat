package me.weijun.nfchat.fragment;

import android.support.v4.app.Fragment;

import me.weijun.nfchat.R;

/**
 * Created by WeijunDeng on 2015/4/19.
 *
 */
public class BaseFragment extends Fragment {

    protected void pushFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }
    }
}
