/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:        # Casos de prueba JUnit  para el Analizador Sintactico                 
 *:                           
 *: Archivo       : SinctacticoErrTest.java
 *: Autor         : Fernando Gil   
 *: 
 *: Fecha         : 25/Feb/2024
 *: Compilador    : Java JDK 17
 *: Descripción   : Casos de prueba con programas con error sintactico en 
 *:                 lenguajes SQLTec. Los errores que se prueban son al emparejar.   
 *:           	      
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 25/Feb/2024 FGil               -Creacion de la estructura de la prueba.  
 *:-----------------------------------------------------------------------------
 */

package pruebas_sintactico;

import compilador.Compilador;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author FGIL.0
 */
public class SintacticoErrTest {
    Compilador        cmp       = new Compilador ();
    ArrayList<String> programas = new ArrayList<> ();
    
    //--------------------------------------------------------------------------
    
    public SintacticoErrTest() {
        // #1 - Error se esperaba end
//        programas.add ( """
//        """ );
              
        // #02-declaracionVariables.sql : Se esperaba idvar en linea 2.
//        programas.add ( """
//              declare @nombre_completo  char (30)
//              declare promedio1         float
//              declare @base_men         int 
//              declare @nombre_completo2 char (10)
//              declare @promedio2        float
//              declare @base_men2        int   
//            end
//        """ );

        // #03-sentenciasAsignacion.sql : Se esperaba opasig en linea 3
//        programas.add ( """
//              assign @nombre_completo := 'Fernando'
//              assign @promedio1 := 0
//              assign @promedio1  = 3.14159  
//              assign @promedio1 := ( @calif1 + @calif2 ) * 20 
//            end  
//        """ );        
  
        // #04a-sentenciaCondicional.sql : En linea 13 se esperaba begin
//        programas.add ( """
//              if @prom1 > 70   
//                begin
//                  assign @prom := 0
//                  print  @prom
//                end
//            
//              if @prom1 > 70 and @prom2 < 80
//                begin
//                  assign @prom := 0
//                  print  @prom
//                end
//              else
//                bigin
//                  assign @prom := 0
//                  print  @prom
//                end
//            
//              case
//                when @min + 10 > @max + 20 then print 'dentro'
//                when @min < 50 then print 'fuera'
//                else print 'enmedio'
//              end 
//            end  
//        """ ); 
        
        // #04b-sentenciaCondicional.sql : En linea 3 se esperaba when
//        programas.add ( """
//              case
//                when @min + 10 > @max + 20 then print 'dentro'
//                owen @min < 50 then print 'fuera'
//                else print 'enmedio'
//              end 
//            end  
//        """ );         

        // #05-sentenciaRepetitiva.sql : En linea 5 se esperaba end
//        programas.add ( """
//            while @prom < 0  or @prom > 100
//                begin
//                  assign @prom := 0
//                  print  @prom      
//                begin 
//            end
//        """ );         

        // #07-recuperacionDatos.sql : Se esperaba id en linea 6, despues del from
//        programas.add ( """
//            select @codigo := codigoprod 
//             from   productos
//             where  precio > 10  
//
//             select @codigo := codigoprod, @preciounit := precio
//             from   
//             where  precio > 10 and precio < 20 
//
//             select @codigo := codigoprod, @preciounit := precio, @descrip := descripcion
//             from   productos
//             where  precio > 10 or precio < 20 
//
//           end  
//        """ );  
        
        // #08-eliminacionRegistros.sql : Se esperaba where en linea 3
//        programas.add ( """
//              delete from productos where precio >= 0
//              delete from productos where precio >= @promedio + 10 
//              delete from productos       precio >= @promedio + 10  and  codigo = 'maruchan'
//            end  
//        """ );  
        
        // #09-insercionRegistros.sql : Se esperaba ) en linea 3, antes de values
//        programas.add ( """
//            insert into productos ( codigoprod ) values ( 10322 )
//
//             insert into productos ( codigoprod, descrip   values ( 10322, 'Sopa miserable' )
//
//
//             insert into productos ( codigoprod, descrip, precio1, precio2 )
//             values ( 10322, 'Sopa miserable', @prom * 10, precio1 + 20 )
//
//           end   
//        """ );  
        
        // #10-actualizacionRegistros.sql : Se esperaba id en linea 1, despues del update
//        programas.add ( """
//              update update
//              set    descrip := 'Sopa maruchan' 
//              where  precio1 < @preciobase * 1.16
//            
//              update productos
//              set    descrip := 'Sopa maruchan', precio1 := 25
//              where  precio1 < precio2 
//              
//              update productos
//              set    descrip := 'Sopa maruchan', precio1 := 25, ubicacion := 'A32'
//              where  precio1 < precio2  or  precio1 = 10
//              
//            end  
//        """ );  
        
        // #11-creacionTablas.sql : Se esperaba table en linea 1, despues de create
//        programas.add ( """
//            create tabla articulos
//             ( IDARTICULO              int                not null,
//               NOMBRE                  char ( 30 )        not null,
//               DESCRIPCION             char ( 50 )        not null,
//               IDCATEGORIA             int                not null,
//               PRECIOPORUNIDAD         float              null,
//               NIVELREABASTECIMIENTO   int                null,
//               QOH                     int                null
//             ) 
//           end   
//        """ );  
        
        // #12-eliminacionTablas.sql : Se esperaba id en linea 1, despues de table
//        programas.add ( """
//            drop table @articulos 
//           end   
//        """ );          
             
    }
    
    //--------------------------------------------------------------------------
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    //--------------------------------------------------------------------------
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    //--------------------------------------------------------------------------
    
    @Before
    public void setUp() {
    }
    
    //--------------------------------------------------------------------------
    
    @After
    public void tearDown() {
    }

    //--------------------------------------------------------------------------
    
    @Test
    public void emparejarTest () {
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* emparejarTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "emparejarTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "emparejarTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[emparejar]" ) );
        }
    }
  
    //--------------------------------------------------------------------------
    
    @Test
    public void simboloInicialTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Error en la linea 1, no se permite un programa en blanco
        programas.add ( "" );
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* siboloInicialTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "siboloInicialTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "siboloInicialTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[P]" ) );
        }
    }  
    
    //--------------------------------------------------------------------------
    
    @Test
    public void tipoDatoTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Error en la linea 1, se esperaba un tipo de dato
        programas.add ( 
      """

        """         
        );
        
        // #2 - Error en la linea 1, se esperaba un tipo de dato
        programas.add ( 
      """

        """         
        );        
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* tipoDatoTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "tipoDatoTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "tipoDatoTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[T]" ) );
        }
    }     
    
    //--------------------------------------------------------------------------
    
    @Test
    public void expresionTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Error en la linea 2, se esperaba una expresion
        programas.add ( 
      """

        """         
        );
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* expresionTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "expresionTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "expresionTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[E]" ) );
        }
    }     

    //--------------------------------------------------------------------------    
}
