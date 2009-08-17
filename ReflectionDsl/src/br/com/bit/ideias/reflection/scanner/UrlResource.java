package br.com.bit.ideias.reflection.scanner;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Leonardo Campos
 * @date 16/08/2009
 */
public class UrlResource implements Resource {

    private final URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    public URL getURL() {
        return url;
    }

    public Object createRelative(String relativePath) {
        return null;
    }

    public File getFile() {
        URI resourceUri = null;
        try {
            resourceUri = url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new File(resourceUri.getSchemeSpecificPart());
    }
}
