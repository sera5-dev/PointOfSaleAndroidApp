package louis.app.pointofsale;

import android.content.Context;
import android.widget.Toast;

public class UtilMethods {
    public static void showToast(Context context, String pMessage) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, pMessage, duration);
        toast.show();
    }
}
