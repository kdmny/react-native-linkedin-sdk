# Android Setup: react-native-linkedin-sdk

**Assumptions**
- You have a linkedin app setup (Key Hash, App Id, etc)
- You installed this module via npm

#### Step 1 - Update Gradle Settings

```gradle
// file: android/settings.gradle
...

include ':react-native-linkedin-sdk'
project(':react-native-linkedin-sdk').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-linkedin-sdk/android')
```

#### Step 2 - Update Gradle Build

```gradle
// file: android/app/build.gradle
...

dependencies {
    ...
    compile project(':react-native-linkedin-sdk')
}
```

#### Step 3 - Register React Package and Handle onActivityResult

```java
...
import android.content.Intent; // import
import com.kdmny.linkedinsdk.LinkedInLoginPackage; // import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {

    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;

    // declare package
    private LinkedInSDKPackage mLinkedInSDKPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReactRootView = new ReactRootView(this);

        // instantiate package
        mLinkedInSDKPackage = new LinkedInSDKPackage(this);

        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())

                // register package here
                .addPackage(mLinkedInSDKPackage)

                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        mReactRootView.startReactApplication(mReactInstanceManager, "AwesomeProject", null);
        setContentView(mReactRootView);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // handle onActivityResult
        mLinkedInSDKPackage.handleActivityResult(requestCode, resultCode, data);
    }
...

```

