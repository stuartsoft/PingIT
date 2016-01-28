package edu.gcc.whiletrue.pingit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FAQPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FAQPageFragment extends Fragment {

    public FAQPageFragment() {
        // Required empty public constructor
    }

    public static FAQPageFragment newInstance() {
        FAQPageFragment fragment = new FAQPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_faqpage, container, false);
    }

}
