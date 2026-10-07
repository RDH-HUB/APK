import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import com.rdhhub.bikinaplikasi.helper.*;

public class ManifestPatcher {
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Usage: ManifestPatcher <input_xml> <output_xml> <package_name> <app_name>");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);
        String packageName = args[2];
        String appName = args[3];

        byte[] fileBytes = FileUtil.readFile(inputFile);
        AXmlEditor aXmlEditor = new AXmlEditor();
        List<String> stringTable = new ArrayList<>();
        aXmlEditor.read(stringTable, fileBytes);

        String replaced = StringUtils.join(stringTable, "\n");

        // 1. Rename package/app name.
        replaced = replaced
                .replace("com.bikinaplikasi.web.mobileadsinitprovider",
                        packageName + ".mobileadsinitprovider")
                .replace("com.bikinaplikasi.web.firebaseinitprovider",
                        packageName + ".firebaseinitprovider")
                .replace("com.bikinaplikasi.web.permission.C2D_MESSAGE",
                        packageName + ".permission.C2D_MESSAGE")
                .replace("com.bikinaplikasi.web", packageName)
                .replace("Nama Aplikasi", appName)
                .replace("android:debuggable=\"true\"", "android:debuggable=\"false\"");

        // 2. Remove dangerous Android permissions.
        String[] dangerousPermissions = {
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_BACKGROUND_LOCATION",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.MANAGE_EXTERNAL_STORAGE",
                "android.permission.CAMERA",
                "android.permission.RECORD_AUDIO",
                "android.permission.READ_CONTACTS",
                "android.permission.WRITE_CONTACTS",
                "android.permission.GET_ACCOUNTS",
                "android.permission.READ_PHONE_STATE",
                "android.permission.READ_PHONE_NUMBERS",
                "android.permission.CALL_PHONE",
                "android.permission.ANSWER_PHONE_CALLS",
                "android.permission.READ_CALL_LOG",
                "android.permission.WRITE_CALL_LOG",
                "android.permission.PROCESS_OUTGOING_CALLS",
                "android.permission.READ_SMS",
                "android.permission.SEND_SMS",
                "android.permission.RECEIVE_SMS",
                "android.permission.RECEIVE_MMS",
                "android.permission.RECEIVE_WAP_PUSH",
                "android.permission.BODY_SENSORS",
                "android.permission.ACTIVITY_RECOGNITION",
                "android.permission.ACCESS_MEDIA_LOCATION"
        };

        for (String perm : dangerousPermissions) {
            replaced = removeUsesPermission(replaced, perm);
        }

        // android.webkit.PermissionRequest is a Java/WebView class, not a manifest permission.

        // 3. Remove camera hardware feature declarations.
        replaced = replaced.replaceAll(
                "(?s)<uses-feature[^>]*android:name=\"android\\.hardware\\.camera[^\"]*\"[^>]*/>",
                "");

        // 4. Remove AdMob manifest components.
        replaced = removeMetaData(replaced, "com.google.android.gms.ads.APPLICATION_ID");
        replaced = removeComponent(replaced, "activity",
                "com.google.android.gms.ads.AdActivity");
        replaced = removeComponent(replaced, "provider",
                "com.google.android.gms.ads.MobileAdsInitProvider");

        // Remove known AdMob strings from the string table.
        replaced = replaced
                .replace("com.google.android.gms.ads.APPLICATION_ID", "")
                .replace("ca-app-pub-3940256099942544~3347511713", "")
                .replace("com.google.android.gms.ads.AdActivity", "")
                .replace("com.google.android.gms.ads.MobileAdsInitProvider", "");

        // 5. Write patched binary XML.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        aXmlEditor.write(replaced, bos);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(bos.toByteArray());
        }

        System.out.println("Manifest patched successfully!");
        System.out.println("AdMob components: removed");
        System.out.println("Dangerous permissions: removed");
    }

    private static String removeUsesPermission(String xml, String permission) {
        String p = Pattern.quote(permission);
        return xml.replaceAll(
                "(?s)<uses-permission\\b[^>]*android:name=\"" + p + "\"[^>]*/>",
                "");
    }

    private static String removeMetaData(String xml, String name) {
        String p = Pattern.quote(name);
        return xml.replaceAll(
                "(?s)<meta-data\\b[^>]*android:name=\"" + p + "\"[^>]*/>",
                "");
    }

    private static String removeComponent(String xml, String tag, String className) {
        String p = Pattern.quote(className);
        // Handles both self-closing and paired elements.
        xml = xml.replaceAll(
                "(?s)<" + tag + "\\b[^>]*android:name=\"" + p + "\"[^>]*/>",
                "");
        xml = xml.replaceAll(
                "(?s)<" + tag + "\\b[^>]*android:name=\"" + p + "\"[^>]*>.*?</" + tag + ">",
                "");
        return xml;
    }
}
