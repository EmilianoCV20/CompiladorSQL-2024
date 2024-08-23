/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Intermedio
 *                 
 *:                           
 *: Archivo       : GenCodigoInt.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   :  
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *:-----------------------------------------------------------------------------
 */


package compilador;

import general.Linea_BE;
import java.util.ArrayList;
import java.util.List;


public class GenCodigoInt {
 
    //Declarar las  constantes VACIO y ERROR_TIPO
    private final String VACIO = "vacio";
    private final String ERROR_TIPO = "error_tipo";
    private final String BOOLEAN = "boolean";
    private final List<List<String>> array_select = new ArrayList<>();
    

    private Compilador cmp;
    private boolean analizarSemantica = false;
    private String preAnalisis;

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;
        } else {
            errorEmparejar(t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
    private void errorEmparejar(String _token, String _lexema, int numLinea) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + (_lexema.equals("$") ? "fin de archivo" : _lexema)
                + ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico
    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
	public GenCodigoInt ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
    //--------------------------------------------------------------------------
	
    public void generar () {
        Atributos P = new Atributos();
        
        PROGRAMASQL ( P ); //PROCEDURE SIMBOLO INICIAL
    }    
    
    //--------------------------------------------------------------------------
    
    public void emite ( String c3d ){
        cmp.iuListener.mostrarCodInt(c3d);
    }
    
    
   //--------------------------------------------------------------------------
    //AUTOR:Emiliano Cepeda
    private void PROGRAMASQL (Atributos PROGRAMASQL ){
        //VARIABLES LOCALES
        Atributos DECLARACION = new Atributos();
        Atributos SENTENCIAS = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "declare" )  || cmp.be.preAnalisis.complex.equals ( "if" ) || 
            cmp.be.preAnalisis.complex.equals ( "while" ) || cmp.be.preAnalisis.complex.equals ( "print" ) || 
            cmp.be.preAnalisis.complex.equals ( "assign" ) || cmp.be.preAnalisis.complex.equals ( "select" ) ||
            cmp.be.preAnalisis.complex.equals ( "delete" ) || cmp.be.preAnalisis.complex.equals ( "insert" ) ||
            cmp.be.preAnalisis.complex.equals ( "update" ) || cmp.be.preAnalisis.complex.equals ( "create" ) ||
            cmp.be.preAnalisis.complex.equals ( "drop" ) || cmp.be.preAnalisis.complex.equals ( "case" ) ||
            cmp.be.preAnalisis.complex.equals( "end" ) ) {
            // PROGRAMASQL -> DECLARACION SENTENCIAS end {69}
            DECLARACION ( DECLARACION );
            SENTENCIAS ( SENTENCIAS );
            emparejar( "end" );
            //INICIO ACCION C3D
            PROGRAMASQL.codigo = DECLARACION.codigo + " " + SENTENCIAS.codigo; 
            //FIN ACCION
        } else {
            error ( "[PROGRAMASQL] El programa debe iniciar con "
                    + "palabra reservada como 'declare, if, while, print, assign,"
                    + "select, delete, insert, update, create, drop Ó case'. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void ACTREGS (Atributos ACTREGS){
        //ATRIBUTOS LOCALES
        Linea_BE id = new Linea_BE();
        Atributos IGUALACION = new Atributos();
        Atributos EXPRCOND = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "update" ) ) {
            // ACTREGS -> update id set IGUALACION where EXPRCOND {60}
            emparejar( "update" );
            id = cmp.be.preAnalisis; //Se guarda el atributo de id
            emparejar( "id" );
            emparejar( "set" );
            IGUALACION ( IGUALACION );
            emparejar( "where" );
            EXPRCOND ( EXPRCOND );
            //INICIO ACCION C3D
            emite ("USE "+ id.lexema.replace('@', '_') + "\n"
                    + "GO TOP \n"
                    + "DO WHILE NOT EOF() \n"
                    + "IF " + EXPRCOND.codigo + " REPLACE "+ id.lexema.replace('@', '_')
                    + " WITH "+ IGUALACION.codigo +"\n"
                    + "ENDIF \n"
                    + "SKIP \n"
                    + "ENDDO \n"
                    + "BROW \n"
                    + "USE "); 

            //FIN ACCION
        } else {
            error ( "[ACTREGS] El texto debe iniciar con palabra reservada"
                    + "'update'. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR:Monserrat Cervantes
    private void COLUMNAS (Atributos COLUMNAS){
        //VARIABLES LOCALES
        Atributos COLUMNASP = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            // COLUMNAS -> id COLUMNASP {43}
            id = cmp.be.preAnalisis;
            emparejar( "id" );
            COLUMNASP ( COLUMNASP );
            //INICIO ACCION C3D
            COLUMNAS.codigo = id.lexema.replace('@', '_') + " " + COLUMNASP.codigo;
            //FIN ACCION C3D
        } else {
            error ( "[COLUMNAS] El texto debe iniciar con una declaracion de "
                    + "variable." 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void COLUMNASP (Atributos COLUMNASP){
        //VARIABLES LOCALES
        Atributos COLUMNAS = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "," ) ) {
            // COLUMNASP -> , COLUMNAS {44}
            emparejar( "," );
            COLUMNAS ( COLUMNAS );
            //INICIO ACCION C3D
            COLUMNASP.codigo = ", " + COLUMNAS.codigo;
            //FIN ACCION C3D
        } else {
            // COLUMNASP -> empty {45}
            //INICIO ACCION C3D
            COLUMNASP.codigo = "";
            //FIN ACCION C3D
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void DECLARACION (Atributos DECLARACION){
        //VARIABLES LOCALES
        Linea_BE idvar = new Linea_BE();
        Atributos TIPO = new Atributos();
        Atributos DECLARACION1 = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "declare" ) ) {
            // DECLARACION -> declare idvar {41} TIPO DECLARACION
            emparejar( "declare" );
            idvar = cmp.be.preAnalisis; //GUARDAR ID
            emparejar( "idvar" );
            //INICIO ACCION C3D
            emite ("PUBLIC " + idvar.lexema.replace('@', '_'));
            //FIN ACCION C3D
            TIPO ( TIPO );
            DECLARACION ( DECLARACION1 );
        }else {
            // DECLARACION -> empty
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void DESPLIEGUE (Atributos DESPLIEGUE){
        //VARIABLES LOCALES
        Atributos EXPRARIT = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "print" ) ) {
            // DESPLIEGUE -> print EXPRARIT {1}
            emparejar( "print" );
            EXPRARIT ( EXPRARIT );
            //INICIO C3D
            emite ("? " + EXPRARIT.codigo);
            //FIN
        } else {
            error ( "[DESPLIEGUE] El texto debe iniciar con la palabra "
                    + "reservada 'print'. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void DELREG (Atributos DELREG){
        //ATRIBUTOS LOCALES
        Atributos EXPRCOND = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if ( cmp.be.preAnalisis.complex.equals ( "delete" ) ) {
            // DELREG -> delete from id where EXPRCOND {65}
            emparejar( "delete" );
            emparejar( "from" );
            id = cmp.be.preAnalisis;
            emparejar( "id" );
            emparejar( "where" );
            EXPRCOND ( EXPRCOND );
            //INICIO C3D
            emite("USE " + id.lexema.replace('@', '_') + "\n"
                    + "GO TOP \n"
                    + "DO WHILE NOT EOF() \n"
                    +   "IF " + EXPRCOND.codigo + "\n"
                    +       "DELETE \n"
                    +   "ENDIF \n"
                    +   "SKIP \n"
                    + "ENDDO \n"
                    + "BROW \n"
                    + "USE ");

            //FIN
        } else {
            error ( "[DELREG] El texto debe iniciar con la palabra "
                    + "reservada 'delete'. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon 
    private void EXPRESIONES (Atributos EXPRESIONES){
        //VARIABLES LOCALES
        Atributos EXPRARIT = new Atributos();
        Atributos EXPRESIONESP = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "num" ) || cmp.be.preAnalisis.complex.equals ( "num.num" ) ||
             cmp.be.preAnalisis.complex.equals ( "idvar" ) || cmp.be.preAnalisis.complex.equals ( "literal" ) ||
             cmp.be.preAnalisis.complex.equals ( "id" ) || cmp.be.preAnalisis.complex.equals ( "(" ) || 
             cmp.be.preAnalisis.complex.equals ( "," ) ) {
            // EXPRESIONES -> EXPRARIT EXPRESIONESP {15}
            EXPRARIT (EXPRARIT);
            EXPRESIONESP (EXPRESIONESP);
            //INICIO C3D
            EXPRESIONES.codigo = EXPRARIT.codigo + " " + EXPRESIONESP.codigo ;
            //FIN
            
        } else {
            error ( "[EXPRESIONES] El texto debe ser ser uno de los siguientes "
                    + "tipos: num, num.num, idvar, literal, id, '(', ','. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void EXPRESIONESP (Atributos EXPRESIONESP) {
        //VARIABLES LOCALES
        Atributos EXPRESIONES = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "," ) ) {
            // EXPRESIONESP -> , EXPRESIONES {16}
            emparejar( "," );
            EXPRESIONES (EXPRESIONES);
            //INICIO C3D
            EXPRESIONESP.codigo = ", " + EXPRESIONES.codigo ;
            //FIN
        } else {
            // EXPRESIONESP -> empty {17}
            //INICIO C3D
            EXPRESIONESP.codigo = "";
            //FIN
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda 
    private void EXPRARIT (Atributos EXPRARIT){
        //VARIABLES LOCALES
        Atributos OPERANDO = new Atributos();
        Atributos EXPRARIT1 = new Atributos();
        Atributos EXPRARITP = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "num" ) || cmp.be.preAnalisis.complex.equals ( "num.num" ) ||
             cmp.be.preAnalisis.complex.equals ( "idvar" ) || cmp.be.preAnalisis.complex.equals ( "literal" ) ||
             cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            // EXPRARIT -> OPERANDO EXPRARITP {29}
            OPERANDO (OPERANDO);
            EXPRARITP (EXPRARITP);
            //INICIO C3D
            EXPRARIT.codigo = OPERANDO.codigo + " " + EXPRARITP.codigo;
            //FIN
        } else if ( cmp.be.preAnalisis.complex.equals ( "(") ) {
            // EXTRARIT -> ( EXPRARIT_1 ) EXPRARITP {30}
            emparejar( "(" );
            EXPRARIT (EXPRARIT1);
            emparejar( ")" );
            EXPRARITP (EXPRARITP);
            //INICIO C3D
            EXPRARIT.codigo = EXPRARIT1.codigo + " " + EXPRARITP.codigo;
            //FIN
        }else {
            error ( "[EXPRARIT] El texto debe ser ser uno de los siguientes "
                    + "tipos: num, num.num, idvar, literal, un id "
                    + "ó empezar con '('." 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void EXPRARITP (Atributos EXPRARITP){
        //VARIABES LOCALES
        Atributos EXPRARIT = new Atributos();
        Linea_BE opsuma = new Linea_BE();
        Linea_BE opmult = new Linea_BE();
        
        if ( cmp.be.preAnalisis.complex.equals ( "opsuma" ) ) {
            // EXPRARITP -> opsuma EXPRARIT {31}
            opsuma = cmp.be.preAnalisis;
            emparejar( "opsuma" );
            EXPRARIT (EXPRARIT);
            //INICIO C3D
            EXPRARITP.codigo = opsuma.lexema + " " + EXPRARIT.codigo ;
            //FIN
        } else if ( cmp.be.preAnalisis.complex.equals ( "opmult" ) ) {
            // EXPRARITP -> opmult EXPRARIT {32}
            opmult = cmp.be.preAnalisis;
            emparejar( "opmult" );
            EXPRARIT (EXPRARIT);
            //INICIO C3D
            EXPRARITP.codigo = opmult.lexema + " " + EXPRARIT.codigo ;
            //FIN
        } else {
            // EXPRARITP --> empty {33}
            //INICIO C3D
            EXPRARITP.codigo = "";
            //FIN
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void EXPRCOND (Atributos EXPRCOND) {
        //VARIABLES LOCALES
        Atributos EXPRREL = new Atributos();
        Atributos EXPRLOG = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "num" ) || cmp.be.preAnalisis.complex.equals ( "num.num" ) ||
             cmp.be.preAnalisis.complex.equals ( "idvar" ) || cmp.be.preAnalisis.complex.equals ( "literal" ) ||
             cmp.be.preAnalisis.complex.equals ( "id" ) || cmp.be.preAnalisis.complex.equals ( "(" ) || 
             cmp.be.preAnalisis.complex.equals ( "and" ) || cmp.be.preAnalisis.complex.equals ( "or" ) ) {
            // EXPRCOND -> EXPRREL EXPRLOG {11}
            EXPRREL (EXPRREL);
            EXPRLOG (EXPRLOG);
            //INICIO C3D
            EXPRCOND.codigo = EXPRREL.codigo + " " + EXPRLOG.codigo;
            //FIN
        } else {
            error ( "[EXPRCOND] El texto debe ser ser uno de los siguientes "
                    + "tipos: num, num.num, idvar, literal, id, '(', and, or. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void EXPRREL (Atributos EXPRREL) {
        //VARIABLES LOCALES
        Atributos EXPRARIT1 = new Atributos();
        Atributos EXPRARIT2 = new Atributos();
        Linea_BE oprel = new Linea_BE();
        
        if ( cmp.be.preAnalisis.complex.equals ( "num" ) || cmp.be.preAnalisis.complex.equals ( "num.num" ) ||
             cmp.be.preAnalisis.complex.equals ( "idvar" ) || cmp.be.preAnalisis.complex.equals ( "literal" ) ||
             cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            // EXPRREL -> EXPRARIT oprel EXPRARIT {7}
            EXPRARIT (EXPRARIT1);
            oprel = cmp.be.preAnalisis;
            emparejar( "oprel" );
            EXPRARIT (EXPRARIT2);
            //INICIO C3D
            EXPRREL.codigo = EXPRARIT1.codigo  + " " + oprel.lexema 
                    + " " + EXPRARIT2.codigo;
            //FIN
        } else {
            error ( "[EXPRREL] El texto debe ser ser uno de los siguientes "
                    + "tipos: num, num.num, idvar, literal o un identificador. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon 
    private void EXPRLOG (Atributos EXPRLOG) {
        //VARIABLES LOCALES
        Atributos EXPRREL = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "and" ) ) {
            // EXPRLOG -> and EXPRREL {8}
            emparejar( "and" );
            EXPRREL (EXPRREL);
            //INICIO C3D
            EXPRLOG.codigo = " AND  " + EXPRREL.codigo;
            //FIN
        } else if ( cmp.be.preAnalisis.complex.equals ( "or") ) {
            // EXPRLOG -> or EXPRREL {9}
            emparejar( "or" );
            EXPRREL (EXPRREL);
            //INICIO C3D
            EXPRLOG.codigo = " OR " + EXPRREL.codigo;
            //FIN
        } else {
            // EXPRLOG -> empty
            //INICIO C3D
            EXPRLOG.codigo = "";
            //FIN
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR:Monserrat Cervantes
    private void ELIMTAB (Atributos ELIMTAB) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        
        if ( cmp.be.preAnalisis.complex.equals ( "drop" ) ) {
            // ELIMTAB -> drop table id {2}
            emparejar( "drop" );
            emparejar( "table" );
            id = cmp.be.preAnalisis;
            emparejar( "id" );
            //INICIO C3D
            emite ( "DELETE FILE " + id.lexema  + ".DBF" );
            //FIN
        } else {
            error ( "[ELIMTAB] El texto debe iniciar con la palabra "
                    + "reservada 'drop'. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void IFELSE (Atributos IFELSE) {
        //VARIABLES LOCALES
        Atributos EXPRCOND = new Atributos();
        Atributos SENTENCIAS = new Atributos();
        Atributos IFELSEP = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "if" ) ) {
            // IFELSE -> if EXPRCOND begin {3} SENTENCIAS end IFELSEP
            emparejar( "if" );
            EXPRCOND (EXPRCOND);
            emparejar( "begin" );
            //INICIO C3D
            emite ( "IF " + EXPRCOND.codigo );
            //FIN
            SENTENCIAS (SENTENCIAS);
            emparejar( "end" );
            IFELSEP (IFELSEP);
        } else {
            error ( "[IFELSE] El texto debe iniciar con la palabra "
                    + "reservada 'if'. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void IFELSEP (Atributos IFELSEP) {
        //VARIABLES LOCALES
        Atributos SENTENCIAS = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "else" ) ) {
            // IFELSEP -> else begin {5} SENTENCIAS end {6}
            emparejar( "else" );
            emparejar( "begin" );
            //INICIO C3D
            emite ( " ELSE " );
            //FIN
            SENTENCIAS (SENTENCIAS);
            emparejar( "end" );
            //INICIO C3D
            emite ( " ENDIF " );
            //FIN
        } else {
            // IFELSEP -> empty {4}
            //INICIO C3D
            emite ( " ENDIF " );
            //FIN
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void IGUALACION (Atributos IGUALACION) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        Linea_BE opasig = new Linea_BE();
        Atributos EXPRARIT = new Atributos();
        Atributos IGUALACIONP = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "id" ) ) {
            // IGUALACION -> id opasig EXPRARIT IGUALACIONP {61}
            id = cmp.be.preAnalisis; //Se guarda el atributo de id
            emparejar( "id" );
            opasig = cmp.be.preAnalisis; //Se guarda el atributo de opasig
            emparejar( "opasig" );
            EXPRARIT (EXPRARIT);
            IGUALACIONP (IGUALACIONP);
            //INICIO C3D
            IGUALACION.codigo = id.lexema.replace('@', '_') + " " + opasig.lexema +" " 
                    + EXPRARIT.codigo + " " + IGUALACIONP.codigo;
            //FIN
        } else {
            error ( "[IGUALACION] El texto debe iniciar con un identificador. " 
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void IGUALACIONP (Atributos IGUALACIONP) {
        //VARIABLES LOCALES
        Atributos IGUALACION = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals ( "," ) ) {
            // IGUALACIONP -> , IGUALACION {62}
            emparejar( "," );
            IGUALACION (IGUALACION);
            //INICIO C3D
            IGUALACIONP.codigo = ", " + IGUALACION.codigo;
            //FIN
        } else {
            // IGUALACIONP -> empty {63}
            //INICIO C3D
            IGUALACIONP.codigo = "";
            //FIN
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void INSERCION (Atributos INSERCION) {
        //VARIABLES LOCALES
        Atributos COLUMNAS = new Atributos();
        Atributos EXPRESIONES = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if ( cmp.be.preAnalisis.complex.equals( "insert" ) ) {
            // INSERCION -> insert into id ( COLUMNAS ) values ( EXPRESIONES ) {64}
            emparejar ( "insert" );
            emparejar ( "into" );
            id = cmp.be.preAnalisis; //ID GUARDADA
            emparejar ( "id" );
            emparejar ( "(" );
            COLUMNAS (COLUMNAS) ;
            emparejar ( ")" );
            emparejar ( "values" );
            emparejar ( "(" );
            EXPRESIONES (EXPRESIONES);
            emparejar ( ")" );
            //INICIO C3D
//            emite("USE " + id.lexema.replace('@', '_')
//                    + "\n APPEND BLANK \n"
//                    + " REPLACE " + COLUMNAS.codigo + " WITH " + EXPRESIONES.codigo);
            //FIN
            
            //INICIO C3D
            emite("USE " + id.lexema.replace('@', '_') + "\n"
                    + "APPEND BLANK \n"
                    + "REPLACE " + formatoDatos(COLUMNAS.codigo, EXPRESIONES.codigo) );
            //FIN
            
        }
        else {
            error ("[INSERCION] el texto debe de iniciar con la palabra insert"
                  + "en la linea Numero " + cmp.be.preAnalisis.getNumLinea() ) ;
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void LISTAIDS (Atributos LISTAIDS) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        Atributos LISTAIDS1 = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "," ) ) {
            // LISTAIDS -> , id LISTAIDS {46}
             emparejar ( "," );
             id = cmp.be.preAnalisis; //ID GUARDADA
             emparejar ( "id" );
             LISTAIDS (LISTAIDS1) ;
             //INICIO C3D
             LISTAIDS.codigo = id.lexema.replace('@', '_') + " " + LISTAIDS1.codigo;
             //FIN
        }
        else {
            // LISTAIDS -> empty {73}
            //INICIO C3D
             LISTAIDS.codigo = "";
             //FIN
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void NULO(Atributos NULO) {
        //VARIABLES LOCALES (NO HAY)

        if (cmp.be.preAnalisis.complex.equals("null")) {
            // NULO -> null {22}
            emparejar("null");
            //INICIO C3D
            NULO.codigo = " NULL ";
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("not")) {
            // NULO -> not null {23}
            emparejar("not");
            emparejar("null");
            //INICIO C3D
            NULO.codigo = " NOT NULL ";
            //FIN
        } else {
            // NULO -> empty {24}
            //INICIO C3D
            NULO.codigo = "";
            //FIN
        }

    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void OPERANDO (Atributos OPERANDO) {
        //VARIABLES LOCALES
        Linea_BE num = new Linea_BE();
        Linea_BE numnum = new Linea_BE();
        Linea_BE idvar = new Linea_BE();
        Linea_BE literal = new Linea_BE();
        Linea_BE id = new Linea_BE();
        
        if (cmp.be.preAnalisis.complex.equals("num")) {
            //OPERANDO -> num {34}
            num = cmp.be.preAnalisis; //ID GUARDADA
            emparejar("num");
            //INICIO C3D
            OPERANDO.codigo = num.lexema;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("num.num")) {
            //OPERANDO -> num.num {35}
            numnum = cmp.be.preAnalisis; //ID GUARDADA
            emparejar("num.num");
            //INICIO C3D
            OPERANDO.codigo = numnum.lexema;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("idvar")) {
            //OPERANDO -> idvar {36}
            idvar = cmp.be.preAnalisis; //ID GUARDADA
            emparejar("idvar");
            //INICIO C3D
            OPERANDO.codigo = idvar.lexema.replace('@', '_');
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("literal")) {
            //OPERANDO -> literal {37}
            literal = cmp.be.preAnalisis; //ID GUARDADA
            emparejar("literal");
            //INICIO C3D
            OPERANDO.codigo = literal.lexema;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("id")) {
            //OPERANDO -> id {38}
            id = cmp.be.preAnalisis; //ID GUARDADA
            emparejar("id");
            //INICIO C3D
            OPERANDO.codigo = id.lexema.replace('@', '_');
            //FIN
        } else {
            error("[OPERANDO], se esperaba recibir un valor de numero entero, "
                    + "numero de punto flotante, un identificador de variable "
                    + "una literal o un identificador "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void SENTENCIAS (Atributos SENTENCIAS) {
        //VARIABLES LOCALES
        Atributos SENTENCIA = new Atributos();
        Atributos SENTENCIAS1 = new Atributos();
        
       if ( cmp.be.preAnalisis.complex.equals( "if" ) ||
            cmp.be.preAnalisis.complex.equals( "while" ) ||
            cmp.be.preAnalisis.complex.equals( "print" ) ||
            cmp.be.preAnalisis.complex.equals( "assign" ) || 
            cmp.be.preAnalisis.complex.equals( "select" ) ||
            cmp.be.preAnalisis.complex.equals( "delete" ) ||
            cmp.be.preAnalisis.complex.equals( "insert" ) ||
            cmp.be.preAnalisis.complex.equals( "update" ) ||
            cmp.be.preAnalisis.complex.equals( "create" ) ||
            cmp.be.preAnalisis.complex.equals( "drop" ) ||
            cmp.be.preAnalisis.complex.equals( "case" ) ) {
           // SENTENCIAS -> SENTENCIA SENTENCIAS {47}
           SENTENCIA (SENTENCIA);
           SENTENCIAS (SENTENCIAS1);
           //INICIO C3D
           SENTENCIAS.codigo = SENTENCIA.codigo + " " + SENTENCIAS1.codigo;
           //FIN
       }
       else {
           // SENTENCIAS -> empty {48}
           //INICIO C3D
           SENTENCIAS.codigo = "";
           //FIN
       }
        
}
    
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void SENTENCIA(Atributos SENTENCIA) {
        //VARIABLES LOCALES
        Atributos IFELSE = new Atributos();
        Atributos SENREP = new Atributos();
        Atributos DESPLIEGUE = new Atributos();
        Atributos SENTASIG = new Atributos();
        Atributos SENTSELECT = new Atributos();
        Atributos DELREG = new Atributos();
        Atributos INSERCION = new Atributos();
        Atributos ACTREGS = new Atributos();
        Atributos TABLA = new Atributos();
        Atributos ELIMTAB = new Atributos();
        Atributos SELECTIVA = new Atributos();
        Atributos EXPRREL = new Atributos();
        
        if (cmp.be.preAnalisis.complex.equals("if")) {
            // SENTENCIA -> IFELSE {49}
            IFELSE(IFELSE);
            //INICIO C3D
            SENTENCIA.codigo = IFELSE.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("while")) {
            // SENTENCIA -> SENREP {50}
            SENREP(SENREP);
            //INICIO C3D
            SENTENCIA.codigo = SENREP.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("print")) {
            // SENTENCIA -> DESPLIEGUE {51} 
            DESPLIEGUE(DESPLIEGUE);
            //INICIO C3D
            SENTENCIA.codigo = DESPLIEGUE.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("assign")) {
            // SENTENCIA -> SENTASIG {52}
            SENTASIG(SENTASIG);
            //INICIO C3D
            SENTENCIA.codigo = SENTASIG.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("select")) {
            // SENTENCIA -> SENTSELECT {53}
            SENTSELECT(SENTSELECT);
            //INICIO C3D
            SENTENCIA.codigo = SENTSELECT.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("delete")) {
            //SENTENCIA -> DELREG {54}
            DELREG(DELREG);
            //INICIO C3D
            SENTENCIA.codigo = DELREG.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("insert")) {
            // SENTENCIA -> INSERCION {55}
            INSERCION(INSERCION);
            //INICIO C3D
            SENTENCIA.codigo = INSERCION.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("update")) {
            // SENTENCIA -> ACTREGS {56}
            ACTREGS(ACTREGS);
            //INICIO C3D
            SENTENCIA.codigo = ACTREGS.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("create")) {
            // SENTENCIA -> TABLA {57}
            TABLA(TABLA);
            //INICIO C3D
            SENTENCIA.codigo = TABLA.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("drop")) {
            // SENTENCIA -> ELIMTAB {58}
            ELIMTAB(ELIMTAB);
            //INICIO C3D
            SENTENCIA.codigo = ELIMTAB.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("case")) {
            // SENTENCIA -> SELECTIVA {59}
            SELECTIVA(SELECTIVA);
            //INICIO C3D
            SENTENCIA.codigo = SELECTIVA.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("and")) {
            // SENTENCIA -> EXPRELOG {??}
            emparejar("and");
            EXPRREL(EXPRREL);
            //INICIO C3D
            SENTENCIA.codigo = EXPRREL.codigo;
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("or")) {
            // SENTENCIA -> EXPRELOG {??}
            emparejar("or");
            EXPRREL(EXPRREL);
            //INICIO C3D
            SENTENCIA.codigo = EXPRREL.codigo;
            //FIN
        } else {
            error("[SENTENCIA] hubo un error en la creacion de sentencias"
                    + "ya que no contiene o estan mal escrita la sentencia de instruccion"
                    + "[if, while, print, assign, select, delete, insert, update, create, drop, case] "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void SELECTIVA (Atributos SELECTIVA) {
        //VARIABLES LOCALES
        Atributos SELWHEN = new Atributos();
        Atributos SELELSE = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "case") ) {
            // SELECTIVA -> case SELWHEN {25} SELELSE end {27}
            emparejar ("case");
            //INICIO C3D
            emite ( "DO CASE ");
            //FIN
            SELWHEN (SELWHEN);
            SELELSE (SELELSE);
            emparejar ( "end" );
            //INICIO C3D
            emite ( "ENDCASE" );
            //FIN
        }
        else {
            error ( "[SELECTIVA] se esperaba la palabra 'case' "
                    + "de los distintos casos de seleccion a usar "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void SELWHEN (Atributos SELWHEN) {
        //VARIABLES LOCALES
        Atributos EXPRCOND = new Atributos();
        Atributos SENTENCIA = new Atributos();
        Atributos SELWHENP = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "when" ) ){
            // SELWHEN -> when EXPRCOND {26} then SENTENCIA SELWHENP
            emparejar ( "when" );
            EXPRCOND (EXPRCOND);
            //INICIO C3D
            emite ( " CASE " + EXPRCOND.codigo );
            //FIN
            emparejar ( "then" );
            SENTENCIA (SENTENCIA);
            SELWHENP (SELWHENP);
        }
        else {
            error ( "[SELWHEN] Se esperaba la palabra 'when' seguida de una "
                    + "expresion condicional "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void SELWHENP (Atributos SELWHENP) {
        //VARIABLES LOCALES
        Atributos SELWHEN = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "when" ) ){
            // SELWHENP -> SELWHEN {70}
            SELWHEN (SELWHEN);
            //INICIO C3D
            SELWHENP.codigo = SELWHEN.codigo;
            //FIN
        } else {
            // SELWHENP -> empty {71}
            //INICIO C3D
            SELWHENP.codigo = "";
            //FIN
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void SELELSE (Atributos SELELSE) {
        //VAARIABLES LOCALES
        Atributos SENTENCIA = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "else" ) ){
            // SELELSE -> else SENTENCIA
            emparejar ( "else" );
            SENTENCIA (SENTENCIA);
            //INICIO C3D
            SELELSE.codigo = " OTHERWISE " + SENTENCIA.codigo;
            //FIN
        } 
        else {
            // SELELSE -> empty {72}
            //INICIO C3D
            SELELSE.codigo = "";
            //FIN
        }
        
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes 
    private void SENREP (Atributos SENREP) {
        //VARIABLES LOCALES
        Atributos EXPRCOND = new Atributos();
        Atributos SENTENCIAS = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "while" ) ) {
            // SENREP  -> while EXPRCOND begin {39} SENTENCIAS end {40}
            emparejar ( "while" );
            EXPRCOND (EXPRCOND);
            emparejar ( "begin" );
            //INICIO C3D
            emite ( " DO WHILE " + EXPRCOND.codigo );
            //FIN
            SENTENCIAS (SENTENCIAS);
            emparejar ( "end" );
            //INICIO C3D
            emite ( "ENDDO ");
            //FIN
        } 
        else {
            error ( "[SENREP] se esperaba la palabra 'while' seguida de una Expresion Condicional "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }
        
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void SENTASIG (Atributos SENTASIG) {
        //VARIABLES LOCALES
        Linea_BE idvar = new Linea_BE();
        Atributos EXPRARIT = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "assign") ) {
            // SENTASIG -> assign idvar opasig EXPRARIT  {42}
            emparejar ( "assign" );
            idvar = cmp.be.preAnalisis;
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            EXPRARIT (EXPRARIT);
            //INICIO C3D
            emite ( "STORE " + EXPRARIT.codigo + " TO " + idvar.lexema.replace('@', '_'));
            //FIN
        } else {
            error ( "[SENTASIG] se esperaba la palabra 'assign' seguida de una "
                    + "variable de identificador en la linea "
                    + cmp.be.preAnalisis.getNumLinea() );
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void SENTSELECT(Atributos SENTSELECT) {
        //VARIABLES LOCALES
        Atributos SENTSELECTC = new Atributos();
        Atributos EXPRCOND = new Atributos();
        Linea_BE id1 = new Linea_BE();
        Linea_BE idvar = new Linea_BE();
        Linea_BE id2 = new Linea_BE();

        if (cmp.be.preAnalisis.complex.equals("select")) {
            // SENTSELECT -> select idvar opasig id SENTSELECTC from id where EXPRCOND {66}
            emparejar("select");
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            emparejar("opasig");
            id2 = cmp.be.preAnalisis;
            emparejar("id");
            SENTSELECTC(SENTSELECTC);
            emparejar("from");
            id1 = cmp.be.preAnalisis;
            emparejar("id");
            emparejar("where");
            EXPRCOND(EXPRCOND);
            //INICIO C3D
            emite( "USE " + id1.lexema.replace('@', '_') + " \n"
                    + "GO TOP \n"
                    + "DO WHILE NOT EOF() \n"
                    + "IF " + EXPRCOND.codigo + "\n"
                    + "STORE " + id2.lexema.replace('@', '_') + " TO " + idvar.lexema.replace('@', '_')
                    + " "+ SENTSELECTC.codigo + "\n"
                    + "ENDIF \n"
                    + "SKIP \n"
                    + "ENDDO \n"
                    + "BROW \n"
                    + "USE ");

            //FIN
        } else {
            error("[SENTSELECT] se esperaba la palabra select seguida de la "
                    + "siguiente estructura: "
                    + "[idvar opasig id SENTSELECTC from id where EXPRCOND] "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void SENTSELECTC (Atributos SENTSELECTC) {
        //VARIABLE LOCAL
        Linea_BE idvar = new Linea_BE();
        Linea_BE id = new Linea_BE();
        Atributos SENTSELECTC1 = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "," ) ) {
            // SENTSELECTC -> ,idvar opasig id SENTSELECTC {67}
            emparejar ( "," );
            idvar = cmp.be.preAnalisis;
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            SENTSELECTC (SENTSELECTC1);
            //INICIO C3D
            SENTSELECTC.codigo = " \nSTORE "+ id.lexema.replace('@', '_') +" TO " 
                    + idvar.lexema.replace('@', '_') + " " + SENTSELECTC1.codigo;
            //FIN
        } else {
            // SENTSELECTC -> empty {68}
            //INICIO C3D
            SENTSELECTC.codigo = "";
            //FIN
        } 
        
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void TIPO(Atributos TIPO) {
        //VARIABLES LOCALES
        Linea_BE num = new Linea_BE();

        if (cmp.be.preAnalisis.complex.equals("int")) {
            // TIPO -> int {12}
            emparejar("int");
            //INICIO C3D
            TIPO.codigo = "N( 5 )";
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("float")) {
            // TIPO -> float {13}
            emparejar("float");
            //INICIO C3D
            TIPO.codigo = "N( 8,3 )";
            //FIN
        } else if (cmp.be.preAnalisis.complex.equals("char")) {
            // TIPO -> char ( num ) {14}
            emparejar("char");
            emparejar("(");
            num = cmp.be.preAnalisis;
            emparejar("num");
            emparejar(")");
            //INICIO C3D
            TIPO.codigo = "C( "+ num.lexema + " )";
            //FIN
        } else {
            error("[TIPO] error al detectar que tipo de dato "
                    + "se va recibir [int, floar, char ( num )] "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }

    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon 
    private void TABLA (Atributos TABLA) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        Atributos TABCOLUMNAS = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "create" ) ) {
            //  TABLA -> create table id {18} (TABCOLUMNAS)
            emparejar ( "create" );
            emparejar ( "table" );
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            emparejar ( "(" );
            TABCOLUMNAS (TABCOLUMNAS) ;
            emparejar ( ")" );
            //INICIO C3D
            emite ( "CREATE TABLE " + id.lexema.replace('@', '_') + " ( "+ TABCOLUMNAS.codigo +" ) ");
            //FIN
        }
        else {
            error ("[TABLA] se esperaba la palabra create en la linea "
                    + cmp.be.preAnalisis.getNumLinea() ) ;
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void TABCOLUMNAS(Atributos TABCOLUMNAS) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        Atributos TIPO = new Atributos();
        Atributos NULO = new Atributos();
        Atributos TABCOLUMNASP = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "id" ) ) {
            // TABCOLUMNAS -> id TIPO NULO TABCOLUMNASP {19}
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            TIPO (TIPO);
            NULO (NULO);
            TABCOLUMNASP(TABCOLUMNASP);
            //INICIO C3D
            TABCOLUMNAS.codigo = id.lexema.replace('@', '_') + " " + TIPO.codigo + " " 
                    + NULO.codigo + " " + TABCOLUMNASP.codigo;
            //FIN
        }
        else {
            error ("[TABCOLUMNAS] se esperaba la palabra id seguida del tipo de dato "
                    + "que se va a declarar en la linea "
                    + cmp.be.preAnalisis.getNumLinea()) ;
        }
    }
    
    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void TABCOLUMNASP (Atributos TABCOLUMNASP) {
        //VARIABLES LOCALES
        Atributos TABCOLUMNAS = new Atributos();
        
        if ( cmp.be.preAnalisis.complex.equals( "," ) ) {
            // TABCOLUMNASP -> , TABCOLUMNAS {20}
            emparejar ( "," );
            TABCOLUMNAS (TABCOLUMNAS);
            //INICIO C3D
            TABCOLUMNASP.codigo = ", " + TABCOLUMNAS.codigo; 
            //FIN
        }
        else {
            // TABCOLUMNASP -> empty {21}
            //INICIO C3D
            TABCOLUMNASP.codigo = "";
            //FIN
        }
    }

    private String formatoDatos(String columnas, String valores) {
    String[] cols = columnas.split(",");
    String[] vals = valores.split(",");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < cols.length; i++) {
        if (i > 0) {
            sb.append(", ");
        }
        sb.append(cols[i].trim()).append(" WITH ").append(vals[i].trim());
    }
    return sb.toString();
}
}
