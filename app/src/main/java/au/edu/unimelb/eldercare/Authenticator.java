package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public interface Authenticator {
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    void startActivityForResult(Intent intent, int requestCode);
}
