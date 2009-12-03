package br.com.bit.ideias.reflection.scanner;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import br.com.bit.ideias.reflection.test.artefacts.ClasseDominio;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominioFilha;
import br.com.bit.ideias.reflection.test.artefacts.MyAnnotation;


/**
 * @author Leonardo Campos
 * @date 16/08/2009
 */
public class PackageScannerTest {
    @Test
    public void scannOnArtefactsPackageShouldReturnAllClasses() throws Exception {
        PackageScanner scanner = PackageScanner.forPackage("br.com.bit.ideias.reflection.test.artefacts");
        
        ScannerResult result = scanner.scan();
        Set<Class<?>> classes = result.getClasses();
        
        Set<Class<?>> expected = new HashSet<Class<?>>();
        expected.add(ClasseDominio.class);
        expected.add(ClasseDominioFilha.class);
        expected.add(MyAnnotation.class);
        
        assertEquals(expected, classes);
    }
}
