package in.ac.iitb.cse.cartsbusboarding.datacollection;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.List;

import static in.ac.iitb.cse.cartsbusboarding.datacollection.DriveQuickstart.getDriveService;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DataSyncIntentService extends IntentService {
    public static final String DATA_FOLDER = "carts/bus_boarding_data";

    private static final String ACTION_SYNC_UP = "in.ac.iitb.cse.cartsbusboarding.datacollection.action.SYNC_UP";
    private static final String ACTION_DRIVE_DEMO = "in.ac.iitb.cse.cartsbusboarding.datacollection.action.DRIVE_DEMO";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "in.ac.iitb.cse.cartsbusboarding.datacollection.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "in.ac.iitb.cse.cartsbusboarding.datacollection.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSyncUp(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DataSyncIntentService.class);
        intent.setAction(ACTION_SYNC_UP);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }
    public static void startActionDriveDemo(Context context) {
        Intent intent = new Intent(context, DataSyncIntentService.class);
        intent.setAction(ACTION_DRIVE_DEMO);
        context.startService(intent);
    }

    public DataSyncIntentService() {
        super("DataSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNC_UP.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionSyncUp(param1, param2);
            } else if (ACTION_DRIVE_DEMO.equals(action)) {
                try {
                    handleActionDriveDemo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSyncUp(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionDriveDemo() throws IOException {
        // Build a new authorized API client service.
        Drive service = getDriveService();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
                .setMaxResults(10)
                .execute();
        List<File> files = result.getItems();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getTitle(), file.getId());
            }
        }
    }

}
