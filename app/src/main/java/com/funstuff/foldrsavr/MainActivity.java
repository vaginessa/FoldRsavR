package com.funstuff.foldrsavr;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.gc.materialdesign.views.*;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.SnackBar;

import ar.com.daidalos.afiledialog.FileChooserActivity;
import ar.com.daidalos.afiledialog.FileChooserDialog;


public class MainActivity extends ActionBarActivity {

    final static private String APP_KEY = "xiz0zq5cc7njedo";
    final static private String APP_SECRET = "jwiq8sig2gglkm1";
    final static private String setting = "APP_SETTINGS";
    final static private String accessTK = "AccessToken";
    static private String token = null;
    SharedPreferences settings;
    SharedPreferences.Editor PrefEditor;

    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;

    // And later in some initialization function:
    AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
    ButtonRectangle start;
    AndroidAuthSession session = new AndroidAuthSession(appKeys);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences(setting, MODE_PRIVATE);
        PrefEditor = settings.edit();
        token = settings.getString(accessTK,null);
        if (token == null) {
            setContentView(R.layout.activity_main_auth);

            start = (ButtonRectangle) findViewById(R.id.startAuth);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDBApi.getSession().startOAuth2Authentication(MainActivity.this);

                }
            });
        }
        else{
            setContentView(R.layout.activity_main_work);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            ButtonFloat fab = (ButtonFloat) findViewById(R.id.addL);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileChooserDialog dialog = new FileChooserDialog(MainActivity.this);
                    dialog.setFolderMode(true);
                    dialog.show();
                }
            });


        }

    }

    protected void onResume() {
        super.onResume();

        if(mDBApi != null){
            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                    // Required to complete auth, sets the access token on the session
                    mDBApi.getSession().finishAuthentication();

                    String accessToken = mDBApi.getSession().getOAuth2AccessToken();

                    PrefEditor.putString(accessTK, accessToken);
                    PrefEditor.commit();

                    Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(i);
                } catch (IllegalStateException e) {
                    Log.i("DbAuthLog", "Error authenticating", e);
                    Toast.makeText(MainActivity.this, "Falied to authenticate", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
