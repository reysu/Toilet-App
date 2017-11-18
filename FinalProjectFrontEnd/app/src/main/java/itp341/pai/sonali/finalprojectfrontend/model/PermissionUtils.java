package itp341.pai.sonali.finalprojectfrontend.model;

/**
 * Created by Chuan on 11/17/2017.
 */



        import android.app.Activity;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.widget.Toast;
        import android.Manifest;
        import android.app.Dialog;
        import android.content.DialogInterface;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.DialogFragment;
        import android.support.v7.app.AppCompatActivity;
        import android.widget.Toast;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

        import itp341.pai.sonali.finalprojectfrontend.R;


/**
 * Created by Jaison on 02/05/17.
 */

public class PermissionUtils
{

    Context context;
    Activity current_activity;

    PermissionResultCallback permissionResultCallback;


    ArrayList<String> permission_list=new ArrayList<>();
    ArrayList<String> listPermissionsNeeded=new ArrayList<>();

    String dialog_content="";
    int req_code;

    public PermissionUtils(Context context)
    {
        this.context=context;
        this.current_activity= (Activity) context;

        permissionResultCallback= (PermissionResultCallback) context;


    }

    public PermissionUtils(Context context, PermissionResultCallback callback)
    {
        this.context=context;
        this.current_activity= (Activity) context;

        permissionResultCallback= callback;


    }


    /**
     * Check the API Level & Permission
     *
     * @param permissions
     * @param dialog_content
     * @param request_code
     */
    public static void requestPermission(AppCompatActivity activity, int requestId,
                                         String permission, boolean finishActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog.newInstance(requestId, finishActivity)
                    .show(activity.getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);

        }
    }

    public void check_permission(ArrayList<String> permissions, String dialog_content, int request_code)
    {
        this.permission_list=permissions;
        this.dialog_content=dialog_content;
        this.req_code=request_code;

        if(Build.VERSION.SDK_INT >= 23)
        {
            if (checkAndRequestPermissions(permissions, request_code))
            {
                permissionResultCallback.PermissionGranted(request_code);
                Log.i("all permissions", "granted");
                Log.i("proceed", "to callback");
            }
        }
        else
        {
            permissionResultCallback.PermissionGranted(request_code);

            Log.i("all permissions", "granted");
            Log.i("proceed", "to callback");
        }

    }


    /**
     * Check and request the Permissions
     *
     * @param permissions
     * @param request_code
     * @return
     */

    private  boolean checkAndRequestPermissions(ArrayList<String> permissions,int request_code) {

        if(permissions.size()>0)
        {
            listPermissionsNeeded = new ArrayList<>();

            for(int i=0;i<permissions.size();i++)
            {
                int hasPermission = ContextCompat.checkSelfPermission(current_activity,permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }

            }

            if (!listPermissionsNeeded.isEmpty())
            {
                ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),request_code);
                return false;
            }
        }

        return true;
    }

    /**
     *
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0)
                {
                    Map<String, Integer> perms = new HashMap<>();

                    for (int i = 0; i < permissions.length; i++)
                    {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    final ArrayList<String> pending_permissions=new ArrayList<>();

                    for (int i = 0; i < listPermissionsNeeded.size(); i++)
                    {
                        if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED)
                        {
                            if(ActivityCompat.shouldShowRequestPermissionRationale(current_activity,listPermissionsNeeded.get(i)))
                                pending_permissions.add(listPermissionsNeeded.get(i));
                            else
                            {
                                Log.i("Go to settings","and enable permissions");
                                permissionResultCallback.NeverAskAgain(req_code);
                                Toast.makeText(current_activity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    }

                    if(pending_permissions.size()>0)
                    {
                        showMessageOKCancel(dialog_content,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                check_permission(permission_list,dialog_content,req_code);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                Log.i("permisson","not fully given");
                                                if(permission_list.size()==pending_permissions.size())
                                                    permissionResultCallback.PermissionDenied(req_code);
                                                else
                                                    permissionResultCallback.PartialPermissionGranted(req_code,pending_permissions);
                                                break;
                                        }


                                    }
                                });

                    }
                    else
                    {
                        Log.i("all","permissions granted");
                        Log.i("proceed","to next step");
                        permissionResultCallback.PermissionGranted(req_code);

                    }



                }
                break;
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(current_activity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public interface PermissionResultCallback
    {
        void PermissionGranted(int request_code);
        void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
        void PermissionDenied(int request_code);
        void NeverAskAgain(int request_code);
    }

    /**
     * A dialog that displays a permission denied message.
     */
    public static class PermissionDeniedDialog extends DialogFragment {

        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

        private boolean mFinishActivity = false;

        /**
         * Creates a new instance of this dialog and optionally finishes the calling Activity
         * when the 'Ok' button is clicked.
         */
        public static PermissionDeniedDialog newInstance(boolean finishActivity) {
            Bundle arguments = new Bundle();
            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);

            PermissionDeniedDialog dialog = new PermissionDeniedDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mFinishActivity = getArguments().getBoolean(ARGUMENT_FINISH_ACTIVITY);

            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.location_permission_denied)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (mFinishActivity) {
                Toast.makeText(getActivity(), R.string.permission_required_toast,
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    /**
     * A dialog that explains the use of the location permission and requests the necessary
     * permission.
     * <p>
     * The activity should implement
     * {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
     * to handle permit or denial of this permission request.
     */
    public static class RationaleDialog extends DialogFragment {

        private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";

        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

        private boolean mFinishActivity = false;

        /**
         * Creates a new instance of a dialog displaying the rationale for the use of the location
         * permission.
         * <p>
         * The permission is requested after clicking 'ok'.
         *
         * @param requestCode    Id of the request that is used to request the permission. It is
         *                       returned to the
         *                       {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
         * @param finishActivity Whether the calling Activity should be finished if the dialog is
         *                       cancelled.
         */
        public static RationaleDialog newInstance(int requestCode, boolean finishActivity) {
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
            RationaleDialog dialog = new RationaleDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);
            mFinishActivity = arguments.getBoolean(ARGUMENT_FINISH_ACTIVITY);

            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_rationale_location)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // After click on Ok, request the permission.
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    requestCode);
                            // Do not finish the Activity while requesting permission.
                            mFinishActivity = false;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (mFinishActivity) {
                Toast.makeText(getActivity(),
                        R.string.permission_required_toast,
                        Toast.LENGTH_SHORT)
                        .show();
                getActivity().finish();
            }
        }
    }
}
