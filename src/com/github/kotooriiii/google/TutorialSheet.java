package com.github.kotooriiii.google;

import com.github.kotooriiii.LostShardPlugin;
import com.github.kotooriiii.channels.CensorManager;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.bukkit.Location;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class TutorialSheet {

    private final String sheetsID = "1BspV6pYaddIz3IUqC2UheFBvx9ODdFXEFhhvB1FOuPc";
    private final String sheetName;
    private final String sheetData;
    private final String RANGE;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/resources/credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String TOKENS_DIRECTORY_PATH = LostShardPlugin.plugin.getDataFolder() + "/tokens";

    private static Sheets service;

    private static TutorialSheet instance;

    private TutorialSheet() {
        sheetName = "Tutorial Data";
        sheetData = "A2:H";
        RANGE = sheetName + "!" + sheetData;
        build();
    }

    public List<List<Object>> get() {
        try {
            ValueRange response = service.spreadsheets().values().get(sheetsID, RANGE).execute();
            List<List<Object>> values = response.getValues();

            return values;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void append(UUID uuid, String name, boolean isTutorialComplete, String chapterName, int x, int y, int z, boolean hasPlotBannerUse, boolean hasMarkCreate, float duration) {


        try {
            ValueRange response = service.spreadsheets().values().get(sheetsID, RANGE).execute();
            List<List<Object>> values = response.getValues();

            if(values != null) {

                row:
                for (List<Object> row : values) {
                    column:
                    for (Object column : row) {
                        if (column.toString().equals(uuid.toString()))
                            return;
                        continue row;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        ValueRange contentToAddValueRange = new ValueRange();

        ArrayList<List<Object>> values = new ArrayList<>();

        ArrayList<Object> row = new ArrayList<>();
        row.add(uuid.toString());
        row.add(name);
        row.add(isTutorialComplete ? 1 : 0);
        row.add(chapterName);
        row.add("x= " + x + " y=" + y + " z=" + z);
        row.add(hasPlotBannerUse ? 1 : 0);
        row.add(hasMarkCreate ? 1 : 0);
        row.add(duration);


        values.add(row);
        contentToAddValueRange.setValues(values);


        try {
            service.spreadsheets().values().append(sheetsID, RANGE, contentToAddValueRange).setValueInputOption("USER_ENTERED").execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void append(UUID uuid, String name, boolean isTutorialComplete, String chapterName, Location location, boolean hasPlotBannerUse, boolean hasMarkCreate, float duration) {


        try {
            ValueRange response = service.spreadsheets().values().get(sheetsID, RANGE).execute();
            List<List<Object>> values = response.getValues();

            row:
            for (List<Object> row : values) {
                column:
                for (Object column : row) {
                    if (column.toString().equals(uuid.toString()))
                        return;
                    continue row;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        ValueRange contentToAddValueRange = new ValueRange();

        ArrayList<List<Object>> values = new ArrayList<>();

        ArrayList<Object> row = new ArrayList<>();
        row.add(uuid.toString());
        row.add(name);
        row.add(isTutorialComplete ? 1 : 0);
        row.add(chapterName);
        row.add("x= " + location.getBlockX() + " y=" + location.getBlockY() + " z=" + location.getBlockZ());
        row.add(hasPlotBannerUse ? 1 : 0);
        row.add(hasMarkCreate ? 1 : 0);
        row.add(duration);


        values.add(row);
        contentToAddValueRange.setValues(values);


        try {
           service.spreadsheets().values().append(sheetsID, RANGE, contentToAddValueRange).setValueInputOption("USER_ENTERED").execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print(List<List<Object>> values) {
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("UUID\tCached Name\tTutorial Complete?\tChapter Logged Off?\tPlot Banner Use?\tMark Created?\tDuration?");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\n", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5), row.get(6));
            }
        }
    }


    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = TutorialSheet.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private void build() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

            service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName("Kotori's Sheet Bot")
                    .build();


//            ValueRange response = sheets.spreadsheets().values()
//                    .get(sheetsID, range)
//                    .execute();
//
////            List<List<Object>> values = response.getValues();
////            if (values == null || values.isEmpty()) {
////                System.out.println("No data found.");
////            } else {
////                System.out.println("Name, Major");
////                for (List row : values) {
////                    // Print columns A and E, which correspond to indices 0 and 4.
////                    System.out.printf("%s, %s\n", row.get(0), row.get(4));
////                }
////            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static TutorialSheet getInstance() {
        if (instance == null) {
            synchronized (TutorialSheet.class) {
                if (instance == null)
                    instance = new TutorialSheet();
            }
        }
        return instance;

    }
}
