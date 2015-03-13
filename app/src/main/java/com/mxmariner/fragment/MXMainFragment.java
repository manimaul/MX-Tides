package com.mxmariner.fragment;

import android.app.Fragment;


public abstract class MXMainFragment extends Fragment {

    public static final String TAG = MXMainFragment.class.getSimpleName();

    public abstract FragmentId getFragmentId();

    public abstract void invalidate();

    public void setupActionBarTitle() {
    }

}
