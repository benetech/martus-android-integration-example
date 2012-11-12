package org.benetech.android.martusintegration;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity
{

    private static final String MARTUS_PACKAGE = "org.martus.android";
    private static final String MARTUS_COMPONENT = "org.martus.android.BulletinActivity";
    final int ACTIVITY_CHOOSE_ATTACHMENT = 2;
    private String chosenFile = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ACTIVITY_CHOOSE_ATTACHMENT: {
                if (resultCode == RESULT_OK) {
                    if (null != data) {
                        Uri uri = data.getData();
                        chosenFile = uri.getPath();
                    }
                }
                break;
            }
        }
    }

    public void chooseFile(View view) {
        try {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("file/*");
            Intent intent = Intent.createChooser(chooseFile, "Choose an attachment");
            startActivityForResult(intent, ACTIVITY_CHOOSE_ATTACHMENT);
        } catch (Exception e) {
            Log.e("martus", "Failed choosing file", e);
        }
    }

    public void openMartus(View view) {
        if (isMartusInstalled()) {
            launchMartus();
        } else {
            Toast toast = Toast.makeText(this, "Martus not installed. ", 1000);
            toast.show();
            //showInMarket(MARTUS_PACKAGE);
        }
    }


    public boolean isMartusInstalled() {
        try
        {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");

            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);

            for(ResolveInfo info : resolveInfoList) {
                if(info.activityInfo.packageName.equalsIgnoreCase(MARTUS_PACKAGE))
                {
                    return true;
                }
            }
            // No match, so application is not installed
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public void launchMartus()
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(MARTUS_PACKAGE, MARTUS_COMPONENT));
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("org.martus.android.filePath", chosenFile);
        if (null != chosenFile) {
            startActivity(intent);
        }
    }

    private void showInMarket(String packageName)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
