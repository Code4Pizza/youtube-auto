package org.youtube;

import org.youtube.util.OSUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class Test {
    public static void main(String[] args) {
        URL url = ClassLoader.getSystemResource("chromedriver_unix");
        if (url != null) {
            System.out.println("uri is not null : " + url.getPath());
            File file = null;
            try {
                file = new File(url.toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            assert file != null;
            System.out.println(file.getAbsolutePath());
        } else {
            System.out.println("khong the tim thay gi car");
        }
        ;

    }
}
