package org.vaccs.ir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import javax.net.ssl.HttpsURLConnection;

public class HttpsUtils {

    public HttpsUtils() {
    }

    private HttpsURLConnection makeConnection(String https_url) throws IOException {

        URL url = new URL(https_url);

        return (HttpsURLConnection) url.openConnection();
    }

    public void getTarGzipFile(String https_url, String destDirectory, String javafxVersion, boolean sameDir) throws IOException {
        HttpsURLConnection con = makeConnection(https_url);
        untarAndGunzipData(con, destDirectory, javafxVersion, sameDir);
        con.disconnect();
    }

    private void untarAndGunzipData(HttpsURLConnection con, String destDirectory, String javafxVersion, boolean sameDir)
            throws IOException {
        TarArchiveInputStream tis = new TarArchiveInputStream(new GZIPInputStream(con.getInputStream()));

        TarArchiveEntry entry;

        while ((entry = tis.getNextTarEntry()) != null) {
            String tarPath = entry.getName();
            File file;

            if (tarPath.contains(javafxVersion))
                continue;
            else if (entry.isDirectory()) {
                if (!sameDir) {
                    file = new File(destDirectory + tarPath.replace("/",File.separator));
                    file.mkdirs();
                } else if (tarPath.length() > tarPath.indexOf("/") + 1) {
                    file = new File(destDirectory + tarPath.substring(tarPath.indexOf("/")+1).replace("/",File.separator));
                    file.mkdirs();
                }
            } else {
                if (sameDir) {
                    file = new File(destDirectory + tarPath.substring(tarPath.indexOf("/") + 1).replace("/", File.separator));
                } else {
                    file = new File(destDirectory + tarPath.replace("/",File.separator));
                }
                IOUtils.copy(tis, new FileOutputStream(file));
            }
        }

        tis.close();
    }

    public String getDataFileContents(String https_url) throws IOException {
        HttpsURLConnection con = makeConnection(https_url);
        String ret = new String(con.getInputStream().readAllBytes());
        con.disconnect();
        return ret;
    }
}
