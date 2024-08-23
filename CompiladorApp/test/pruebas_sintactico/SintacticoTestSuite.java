
package pruebas_sintactico;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author FGIL.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses ( {pruebas_sintactico.SintacticoOKTest.class, 
                       pruebas_sintactico.SintacticoErrTest.class })
public class SintacticoTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
