package testapp.ayannah.jccolumbres.internalstorage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_WRITE = 1001;
    TextView output;
    public static final String FILE_NAME = "lorem_ipsum.csv";
    private boolean permissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = findViewById(R.id.outputText);
        output.setText(R.string.ready_to_code);
    }

    public void onCreateButtonClick(View view) {
        if (!permissionGranted){
            checkPermissions();
            return;
        }

        String content = getString(R.string.lorem_ipsum);
        FileOutputStream fileOutputStream = null;
        File file = getFile(); //new File(FILE_NAME); -- use for internal

        try {
            fileOutputStream = new FileOutputStream(file); //openFileOutput(FILE_NAME, MODE_PRIVATE); -- use for internal
            fileOutputStream.write(content.getBytes());
            Toast.makeText(this, "File created: " + FILE_NAME, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fileOutputStream == null){
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void onReadButtonClick(View view) {
        getPermissions();
    }

    public void onDeleteButtonClick(View view) {
        getPermissions();

        File file = getFile();//new File(getFilesDir(), FILE_NAME); -- use for internal
        if (file.exists()){
            //deleteFile(FILE_NAME); -- use for internal
            file.delete();
            Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "File not existing", Toast.LENGTH_SHORT).show();
        }
    }

    private File getFile(){
        return new File(Environment.getExternalStorageDirectory(), FILE_NAME);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // Initiate request for permissions.
    private boolean checkPermissions() {

        if (!isExternalStorageReadable() || !isExternalStorageWritable()) {
            Toast.makeText(this, "This app only works on devices with usable external storage",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
            return false;
        } else {
            return true;
        }
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    Toast.makeText(this, "External storage permission granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void getPermissions(){

    }
}
