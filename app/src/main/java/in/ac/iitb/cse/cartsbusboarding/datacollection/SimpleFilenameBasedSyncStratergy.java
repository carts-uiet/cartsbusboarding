package in.ac.iitb.cse.cartsbusboarding.datacollection;

import android.os.Environment;

import java.io.File;
import java.util.List;

import static in.ac.iitb.cse.cartsbusboarding.utils.LogUtils.*;

public class SimpleFilenameBasedSyncStratergy implements SyncStratergy {
    private static final String TAG = makeLogTag(SimpleFilenameBasedSyncStratergy.class);

    @Override
    public List<String> getFilesToUpload() {
        String folder = DataSyncIntentService.DATA_FOLDER;

        String path = Environment.getExternalStorageDirectory().toString() + folder;
        LOGI(TAG, "Path: " + path);
        File f = new File(path);
        File files[] = f.listFiles();
        LOGI(TAG, "Size: " + files.length);
        for (File aFile : files) {
            LOGD("Files", "FileName:" + aFile.getName());
        }
        return null;
    }
}
