package top.defaults.gradientdrawabletuner;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ShapeXmlGenerator.init(this);
    }
}
