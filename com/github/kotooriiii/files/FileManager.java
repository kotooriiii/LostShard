package com.github.kotooriiii.files;

import com.github.kotooriiii.LostShardK;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.logging.Level;

public class FileManager {
    private File clans_folder;
    private File clans_warning_file;
    private File plugin_folder = LostShardK.plugin.getDataFolder();

    public FileManager(){
        if(!clans_warning_file.exists()){
            saveResource("ClanWarning.txt", clans_folder, true);
        }
    }

    public void saveResource(String resourcePath, File out_to_folder, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = LostShardK.plugin.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + LostShardK.plugin.getName());
            } else {
                File outFile = new File(out_to_folder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(out_to_folder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        LostShardK.plugin.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[4096];

                        int len;
                        while((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException var10) {
                    LostShardK.plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

}
