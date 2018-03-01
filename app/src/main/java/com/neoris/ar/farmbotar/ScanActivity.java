package com.neoris.ar.farmbotar;

import com.neoris.ar.farmbotar.util.SampleData;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import com.neoris.ar.farmbotar.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {

    public static final String INTENT_EXTRAS_KEY_SAMPLE = "sampleData";

    private static final String TAG = ScanActivity.class.getSimpleName();

    /** Root directory of the AR-Experiences in the assets dir. */
    private static final String EXPER_ROOT = "exper/";

    /**
     * The ArchitectView is the core of the AR functionality, it is the main
     * interface to the Wikitude SDK.
     * The ArchitectView has its own lifecycle which is very similar to the
     * Activity lifecycle.
     * To ensure that the ArchitectView is functioning properly the following
     * methods have to be called:
     *      - onCreate(ArchitectStartupConfiguration)
     *      - onPostCreate()
     *      - onResume()
     *      - onPause()
     *      - onDestroy()
     * Those methods are preferably called in the corresponding Activity lifecycle callbacks.
     */
    protected ArchitectView architectView;

    /** The path to the AR-Experience. This is usually the path to its index.html. */
    private String arExperience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Used to enabled remote debugging of the ArExperience with google chrome https://developers.google.com/web/tools/chrome-devtools/remote-debugging
        WebView.setWebContentsDebuggingEnabled(true);

        final Intent intent = getIntent();
        final SampleData sampleData = (SampleData) intent.getSerializableExtra(INTENT_EXTRAS_KEY_SAMPLE);
        arExperience = sampleData.getPath();

        /*
         * The ArchitectStartupConfiguration is required to call architectView.onCreate.
         * It controls the startup of the ArchitectView which includes camera settings,
         * the required device features to run the ArchitectView and the LicenseKey which
         * has to be set to enable an AR-Experience.
         */
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration(); // Creates a config with its default values.
        config.setLicenseKey(getString(R.string.wikitude_license_key)); // Has to be set, to get a trial license key visit http://www.wikitude.com/developer/licenses.
        config.setCameraPosition(sampleData.getCameraPosition());       // The default camera is the first camera available for the system.
        config.setCameraResolution(sampleData.getCameraResolution());   // The default resolution is 640x480.
        config.setCameraFocusMode(sampleData.getCameraFocusMode());     // The default focus mode is continuous focusing.
        config.setCamera2Enabled(sampleData.isCamera2Enabled());        // The camera2 api is disabled by default (old camera api is used).
        config.setFeatures(sampleData.getArFeatures());                 // This tells the ArchitectView which AR-features it is going to use, the default is all of them.

        architectView = new ArchitectView(this);
        architectView.onCreate(config); // create ArchitectView with configuration

        setContentView(architectView);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();

        try {
            /*
             * Loads the AR-Experience, it may be a relative path from assets,
             * an absolute path (file://) or a server url.
             *
             * To get notified once the AR-Experience is fully loaded,
             * an ArchitectWorldLoadedListener can be registered.
             */
            architectView.load(EXPER_ROOT + arExperience);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_loading_ar_experience), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception while loading arExperience " + arExperience + ".", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        architectView.onResume(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
         * Deletes all cached files of this instance of the ArchitectView.
         * This guarantees that internal storage for this instance of the ArchitectView
         * is cleaned and app-memory does not grow each session.
         *
         * This should be called before architectView.onDestroy
         */
        architectView.clearCache();
        architectView.onDestroy(); // Mandatory ArchitectView lifecycle call
    }

}
