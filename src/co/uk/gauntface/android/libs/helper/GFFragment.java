package co.uk.gauntface.android.libs.helper;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: mattgaunt
 * Date: 11/1/12
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GFFragment extends Fragment {

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure the app uses the correct context
        mContext = getActivity().getApplicationContext();
    }

    /**
     * Prevent us from having to remember this boiler plate code
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getFragmentLayoutId(), container, false);

        initViews(v, container, savedInstanceState);

        return v;
    }

    public Context getApplicationContext() {
        return mContext;
    }

    public abstract int getFragmentLayoutId();
    public abstract void initViews(View view, ViewGroup container,
                                   Bundle savedInstanceState);

}
