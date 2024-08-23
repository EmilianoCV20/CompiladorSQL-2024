/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 20/FEB/2023 F.Gil, Oswi         -Se implementaron los procedures del parser
 *:                                  predictivo recursivo de leng BasicTec.
 *: 26/FEB/2024 Emiliano,           -Se agregaron los procedures del Parser Predictivo
 *:             Moserrat, Ivan      Recursivo SQL Tec
 *:-----------------------------------------------------------------------------
 */
package compilador;

import general.Linea_BE;
import general.Linea_TS;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SintacticoSemantico {

    //Declarar las  constantes VACIO y ERROR_TIPO
    private final String VACIO = "vacio";
    private final String ERROR_TIPO = "error_tipo";
    private final String BOOLEAN = "boolean";
    private final List<List<String>> array_select = new ArrayList<>();
    private ArrayList<String>[] arraySelect = new ArrayList[2];
    private Pattern arrChar = Pattern.compile("char\\(\\d+\\)");
    private Pattern colmnChar = Pattern.compile("COLUMNA\\(char\\(\\d+\\)\\)");
    

    private Compilador cmp;
    private boolean analizarSemantica = false;
    private String preAnalisis;

    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica
    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        //SIMBOLO INICIAL
        PROGRAMA ();
    }

    //--------------------------------------------------------------------------
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

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
    private void PROGRAMA() {
        //ATRIBUTOS LOCALES
        Atributos P = new Atributos();
        
//        if (checarArchivo("profes.db")) {
//            System.out.println("Si existe archivo profes.db");
//        }
//        if (checarArchivo("alumnos.db")) {
//            System.out.println("Si existe archivo alumnos.db");
//        }
//        if (checarArchivo("calific.db")) {
//            System.out.println("Si existe archivo calific.db");
//        }
//        if (checarArchivo("proveedores.db")) {
//            System.out.println("Si existe archivo proveedores.db");
//        }
//        if (checarArchivo("productos.db")) {
//            System.out.println("Si existe archivo productos.db");
//        }
//        System.out.println("<------------------>");
        PROGRAMASQL( P );
    }
    
    private void add(String idvar, String id) {
        List<String> entry = new ArrayList<>();
        entry.add(idvar);
        entry.add(id);
        array_select.add(entry);
    }

    /*----------------------------------------------------------------------------------------*/
    // AUTOR: Ing. Fernando Gil
    // Metodo para comprobar la existencia en disco del archivo "nomarchivo" 
    // y en su caso cargar su contenido en la Tabla de Simbolos.
    // El argumento representa el nombre de un archivo de texto con extension
    //  ".db" que contiene el esquema (dise�o) de una tabla de base de datos. 
    // Los archivos .db tienen el siguiente dise�o:
    //     Dato            ColIni    ColFin
    //     ==================================
    //     nombre-columna  1         25
    //     tipo-de-dato    30        40
    //
    // Ejemplo:  alumnos.db
    //          1         2         3        
    // 1234567890123456789012345678901234567890
    // ==========================================
    // numctrl                      char(8)  
    // nombre                       char(25)
    // edad                         int
    // promedio                     float
    //
    // Cada columna se carga en la Tabla de Simbolos con Complex = "id" y
    // Tipo = "columna(t)"  siendo t  el tipo de dato de la columna.
    // ----------------------------------------------------------------------
    // 20/Oct/2018: Si en la T.S. ya existe la columna con el mismo ambito 
    // que el que se va a registrar solo se sustituye el TIPO si est� en blanco.
    // Si existe la columna pero no tiene ambito entonces se rellenan los datos
    // del tipo y el ambito. 
    private boolean checarArchivo(String nomarchivo) {
        FileReader fr = null;
        BufferedReader br = null;
        String linea = null;
        String columna = null;
        String tipo = null;
        String ambito = null;
        boolean existeArch = false;
        int pos;

        try {
            // Intentar abrir el archivo con el dise�o de la tabla  
            fr = new FileReader(nomarchivo);
            // cmp.ts.anadeTipo(cmp.be.preAnalisis.getEntrada(), "tabla");
            br = new BufferedReader(fr);

            // Leer linea x linea, cada linea es la especificacion de una columna
            linea = br.readLine();
            while (linea != null) {
                // Extraer nombre y tipo de dato de la columna
                try {
                    columna = linea.substring(0, 24).trim();
                } catch (Exception err) {
                    columna = "ERROR";
                }
                try {
                    tipo = linea.substring(29).trim();
                } catch (Exception err) {
                    tipo = "ERROR";
                }
                try {
                    ambito = nomarchivo.substring(0, nomarchivo.length() - 3);
                } catch (Exception err) {
                    ambito = "ERROR";
                }
                // Agregar a la tabla de simbolos
                Linea_TS lts = new Linea_TS("id",
                        columna,
                        "COLUMNA(" + tipo + ")",
                        ambito
                );

                // Checar si en la Tabla de Simbolos existe la entrada para un 
                // lexema y ambito iguales al de columna y ambito de la tabla .db
                if ((pos = cmp.ts.buscar(columna, ambito)) > 0) {
                    // YA EXISTE: Si no tiene tipo asignarle el tipo columna(t) 
                    if (cmp.ts.buscaTipo(pos).trim().isEmpty()) {
                        cmp.ts.anadeTipo(pos, tipo);
                    }
                } else {
                    // NO EXISTE: Buscar si en la T. de S. existe solo el lexema de la columna
                    if ((pos = cmp.ts.buscar(columna)) > 0) {
                        // SI EXISTE: checar si el ambito esta en blanco
                        Linea_TS aux = cmp.ts.obt_elemento(pos);
                        if (aux.getAmbito().trim().isEmpty()) {
                            // Ambito en blanco rellenar el tipo y el ambito  
                            cmp.ts.anadeTipo(pos, "COLUMNA(" + tipo + ")");
                            cmp.ts.anadeAmbito(pos, ambito);

                        } else {
                            // Insertar un nuevo elemento a la tabla de simb.
                            cmp.ts.insertar(lts);
                        }
                    } else {
                        // NO EXISTE: insertar un nuevo elemento a la tabla de simb.
                        cmp.ts.insertar(lts);
                    }
                }

                // Leer siguiente linea
                linea = br.readLine();
            }
            existeArch = true;
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            // Cierra los streams de texto si es que se crearon
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ex) {
            }
        }
        return existeArch;
    }

    //--------------------------------------------------------------------------
    //AUTOR:Emiliano Cepeda
    private void PROGRAMASQL( Atributos PROGRAMASQL ) {
        //VARIABLES LOCALES
        Atributos DECLARACION = new Atributos();
        Atributos SENTENCIAS = new Atributos();
        
        if (preAnalisis.equals("declare") || preAnalisis.equals("if")
                || preAnalisis.equals("while") || preAnalisis.equals("print")
                || preAnalisis.equals("assign") || preAnalisis.equals("select")
                || preAnalisis.equals("delete") || preAnalisis.equals("insert")
                || preAnalisis.equals("update") || preAnalisis.equals("create")
                || preAnalisis.equals("drop") || preAnalisis.equals("case")
                || preAnalisis.equals("end")) {
            // PROGRAMASQL -> DECLARACION SENTENCIAS end {1}
            DECLARACION(DECLARACION);
            SENTENCIAS(SENTENCIAS);
            emparejar("end");
            //ACCION SEMANTICA 1
            if (analizarSemantica) {
                if (DECLARACION.tipo.equals(VACIO) && SENTENCIAS.tipo.equals(VACIO)) {
                    PROGRAMASQL.tipo = VACIO;
                } else {
                    PROGRAMASQL.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[PROGRAMASQL] La sentencia no empieza con una palabra reservada.");
                }
            }
            //FIN ACCION SEMANTICA
            
        } else {
            error("[PROGRAMASQL] El programa debe iniciar con "
                    + "palabra reservada como 'declare, if, while, print, assign,"
                    + "select, delete, insert, update, create, drop Ó case'. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void ACTREGS(Atributos ACTREGS) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        Atributos IGUALACION = new Atributos();
        Atributos EXPRCOND = new Atributos();

        if (preAnalisis.equals("update")) {
            // ACTREGS -> update id {57} set IGUALACION {58} where EXPRCOND {59}
            emparejar("update");
            id = cmp.be.preAnalisis; //Se guarda el atributo de id
            emparejar("id");
            //ACCION SEMANTICA 57
            if (analizarSemantica) {
                if (checarArchivo(id.lexema + ".db")) {
                    cmp.ts.anadeTipo(id.entrada, "tabla");
                    ACTREGS.h = VACIO;
                } else {
                    ACTREGS.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[ACTREGS] El archivo no existe: " + id.lexema);
                }
            }
            //FIN ACCION SEMANTICA
            emparejar("set");
            IGUALACION(IGUALACION);
            emparejar("where");
            EXPRCOND( EXPRCOND );
            //ACCION SEMANTICA 59
            if (analizarSemantica) {
                if (ACTREGS.h.equals(VACIO) && EXPRCOND.tipo.equals(BOOLEAN) && IGUALACION.tipo.equals(VACIO)) {
                    ACTREGS.tipo = VACIO;
                } else {
                    ACTREGS.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[ACTREGS] Error en la condicion de instruccion.");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[ACTREGS] El texto debe iniciar con palabra reservada"
                    + "'update'. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR:Monserrat Cervantes
    private void COLUMNAS(Atributos COLUMNAS) {
        //VARIABLES LOCALES
        Atributos COLUMNASP = new Atributos();
        Linea_BE id = new Linea_BE();
        Matcher matcher1;

        if (preAnalisis.equals("id")) {
            // COLUMNAS -> id COLUMNASP {74}
            id = cmp.be.preAnalisis; //Se guarda el atributo de id
            emparejar("id");
            //ACCION SEMANTICA
            matcher1 = colmnChar.matcher(cmp.ts.buscaTipo(id.entrada));
            if (analizarSemantica){
                if (cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(int)")
                        || cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(float)")
                        || matcher1.matches()) {
                    COLUMNAS.h = VACIO;
                } else {
                    COLUMNAS.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[COLUMNAS] el id mencionado es inexistente o"
                            + " no le pertenece a una columna"
                            + cmp.be.preAnalisis.numLinea);
                }
            }
            //FIN ACCION SEMANTICA
            COLUMNASP(COLUMNASP);
            //ACCION SEMANTICA 74
            if (analizarSemantica) {
                if( COLUMNAS.h.equals(VACIO) && COLUMNASP.tipo.equals(VACIO) ){
              COLUMNAS.tipo = VACIO;     
            }else{
                COLUMNAS.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO, "[COLUMNAS] Incompatibilidad de tipos en columnas." 
                        + " N°Línea: " + cmp.be.preAnalisis.getNumLinea());
            }
            }
            //FIN ACCION SEMANTICA

        } else {
            error("[COLUMNAS] El texto debe iniciar con una declaracion de "
                    + "variable."
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void COLUMNASP(Atributos COLUMNASP) {
        //VARIABLES LOCALES
        Atributos COLUMNAS = new Atributos();

        if (preAnalisis.equals(",")) {
            // COLUMNASP -> , COLUMNAS {75}
            emparejar(",");
            COLUMNAS(COLUMNAS);
            //ACCION SEMANTICA 75
            if (analizarSemantica) {
                COLUMNASP.tipo = COLUMNAS.tipo;
            }
            //FIN ACCION SEMANTICA
        } else {
            // COLUMNASP -> empty {76}
            //ACCION SEMANTICA 76
            if (analizarSemantica) {
                COLUMNASP.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void DECLARACION(Atributos DECLARACION) {
        //VARIABLES LOCALES
        Linea_BE idvar = new Linea_BE();
        Atributos TIPO = new Atributos();
        Atributos DECLARACION1 = new Atributos();
        
        if (preAnalisis.equals("declare")) {
            // DECLARACION -> declare idvar TIPO {2} DECLARACION {3}
            emparejar("declare");
            idvar = cmp.be.preAnalisis; //Se guarda el atributo de id
            emparejar("idvar");
            TIPO(TIPO);
            //ACCION SEMANTICA 2
            if (analizarSemantica) {
                if (cmp.ts.buscaTipo(idvar.entrada).equals("")) {
                    cmp.ts.anadeTipo(idvar.entrada, TIPO.tipo);
                    DECLARACION.h = VACIO;
                } else {
                    DECLARACION.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[DECLARACION] Identificador ya declarado: " + idvar.lexema
                    + " N°Linea: " + cmp.be.preAnalisis.getNumLinea());
                }
            }
            //FIN ACCION SEMANTICA
            DECLARACION(DECLARACION1);
            //ACCION SEMANTICA 3
            if (analizarSemantica) {
                if (DECLARACION.h.equals(VACIO) && DECLARACION1.tipo.equals(VACIO)) {
                    DECLARACION.tipo = VACIO;
                } else {
                    DECLARACION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[DECLARACION] Error en declaracion de Identificadores");
                }
            }
            //FIN ACCION SEMANTICA
            
        } else {
            // DECLARACION -> empty {4}
            //ACCION SEMANTICA 4
            if (analizarSemantica) {
                    DECLARACION.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void DESPLIEGUE(Atributos DESPLIEGUE) {
        //VARIABLES LOCALES
        Atributos EXPRARIT = new Atributos();
        
        if (preAnalisis.equals("print")) {
            // DESPLIEGUE -> print EXPRARIT {30}
            emparejar("print");
            EXPRARIT(EXPRARIT);
            //ACCION SEMANTICA 30
            if (analizarSemantica) {
                if (!EXPRARIT.tipo.equals(ERROR_TIPO)) {
                    DESPLIEGUE.tipo = VACIO;
                } else {
                    DESPLIEGUE.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[DESPLIEGUE] Error en el despliegue de datos");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[DESPLIEGUE] El texto debe iniciar con la palabra "
                    + "reservada 'print'. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void DELREG(Atributos DELREG) {
        //VARIABLES LOCALES
        Atributos EXPRCOND = new Atributos();
        Linea_BE id = new Linea_BE();

        if (preAnalisis.equals("delete")) {
            // DELREG -> delete from id {49} where EXPRCOND {50}
            emparejar("delete");
            emparejar("from");
            id = cmp.be.preAnalisis;
            emparejar("id");
            //ACCION SEMANTICA 49
            if (analizarSemantica) {
                if (checarArchivo(id.lexema + ".db")) {
                    cmp.ts.anadeTipo(id.entrada, "tabla");
                    DELREG.h = VACIO;
                } else {
                    DELREG.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[DELREG] El id no es una tabla: " + id.lexema
                    + ". N°Linea: " + cmp.be.preAnalisis.getNumLinea());
                }
            }
            //FIN ACCION SEMANTICA
            emparejar("where");
            EXPRCOND(EXPRCOND);
            //ACCION SEMANTICA 50
            if (analizarSemantica) {
                if (DELREG.h.equals(VACIO) && EXPRCOND.tipo.equals(BOOLEAN)) {
                    DELREG.tipo = VACIO;
                } else if (DELREG.h.equals(ERROR_TIPO)) {
                    DELREG.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[DELREG] El identificador " + id.lexema + " no es una tabla."
                    + " N°Linea: " + cmp.be.preAnalisis.getNumLinea());
                } else {
                    DELREG.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[DELREG] La expresion condicional no es boolean."
                    + " N°Linea: " + cmp.be.preAnalisis.getNumLinea());
                }
            }
            //FIN ACCION SEMANTICA 
        } else {
            error("[DELREG] El texto debe iniciar con la palabra "
                    + "reservada 'delete'. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon 
    private void EXPRESIONES(Atributos EXPRESIONES) {
        //VARIABLES LOCALES
        Atributos EXPRARIT = new Atributos();
        Atributos EXPRESIONESP = new Atributos();

        if (preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id") || preAnalisis.equals("(")
                || preAnalisis.equals(",")) {
            // EXPRESIONES -> EXPRARIT EXPRESIONESP {77}
            EXPRARIT(EXPRARIT);
            EXPRESIONESP(EXPRESIONESP);
            //ACCION SEMANTICA 77
            if (analizarSemantica) {
                if (!EXPRARIT.tipo.equals(ERROR_TIPO) && EXPRESIONESP.tipo.equals(VACIO)) {
                    EXPRESIONES.tipo = VACIO;
                } else {
                    EXPRESIONES.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[EXPRESIONES] Error en la Expresiones");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[EXPRESIONES] El texto debe ser ser uno de los siguientes "
                    + "tipos: num, num.num, idvar, literal, id, '(', ','. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void EXPRESIONESP(Atributos EXPRESIONESP) {
        //VARIABLES LOCALES
        Atributos EXPRESIONES = new Atributos();

        if (preAnalisis.equals(",")) {
            // EXPRESIONESP -> , EXPRESIONES {78}
            emparejar(",");
            EXPRESIONES(EXPRESIONES);
            //ACCIONES SEMANTICAS 78
            if (analizarSemantica) {
                EXPRESIONESP.tipo = EXPRESIONES.tipo;
            }
            //FIN ACCIONES SEMANTICAS
        } else {
            // EXPRESIONESP -> empty {79}
            //ACCIONES SEMANTICAS 79
            if (analizarSemantica) {
                EXPRESIONESP.tipo = VACIO;
            }
            //FIN ACCIONES SEMANTICAS
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda 
    private void EXPRARIT(Atributos EXPRARIT) {
        //VARIABLES LOCALES
        Atributos OPERANDO = new Atributos();
        Atributos EXPRARIT1 = new Atributos();
        Atributos EXPRARITP = new Atributos();

        if (preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {
            // EXPRARIT -> OPERANDO EXPRARITP {63}
            OPERANDO(OPERANDO);
            EXPRARITP(EXPRARITP);
            //ACCION SEMANTICA 63
            if (analizarSemantica) {
                if (!OPERANDO.tipo.equals(ERROR_TIPO) && !EXPRARITP.tipo.equals(ERROR_TIPO)) {
                    EXPRARIT.tipo = OPERANDO.tipo;
                } else {
                    EXPRARIT.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[EXPRARIT] Error."
                            + "Incompatibilidad de tipos en la expresión aritmética. No. de Línea: "
                            + cmp.be.preAnalisis.getNumLinea());
                }
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("(")) {
            // EXTRARIT -> ( EXPRARIT1 ){64} EXPRARITP {65}
            emparejar("(");
            EXPRARIT(EXPRARIT1);
            emparejar(")");
            EXPRARITP(EXPRARITP);
            //ACCION SEMANTICA 65
            if (analizarSemantica) {
                if (!EXPRARIT1.tipo.equals(ERROR_TIPO) && !EXPRARITP.tipo.equals(ERROR_TIPO)) {
                    EXPRARIT.tipo = EXPRARIT1.tipo;
                } else {
                    EXPRARIT.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[EXPRARIT] Error de tipos en las Expresiones Aritmeticas."
                    + " N°Linea: " + cmp.be.preAnalisis.getNumLinea());
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[EXPRARIT] El texto debe ser ser uno de los siguientes "
                    + "tipos: num, num.num, idvar, literal, un id "
                    + "ó empezar con '('."
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void EXPRARITP(Atributos EXPRARITP) {
        //VARIABES LOCALES
        Atributos EXPRARIT = new Atributos();

        if (preAnalisis.equals("opsuma")) {
            // EXPRARITP -> opsuma EXPRARIT {71}
            emparejar("opsuma");
            EXPRARIT(EXPRARIT);
            //ACCION SEMANTICA 71
            if (analizarSemantica) {
                if (EXPRARIT.tipo.equals("int") || EXPRARIT.tipo.equals("float") ) {
                    EXPRARITP.tipo = EXPRARIT.tipo;
                } else {
                    EXPRARITP.tipo = ERROR_TIPO;
                }
                //EXPRARITP.tipo = EXPRARIT.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("opmult")) {
            // EXPRARITP -> opmult EXPRARIT {72}
            emparejar("opmult");
            EXPRARIT(EXPRARIT);
            //ACCION SEMANTICA 72
            if (analizarSemantica) {
                //EXPRARITP.tipo = EXPRARIT.tipo;
                if (EXPRARIT.tipo.equals("int") || EXPRARIT.tipo.equals("float") ) {
                    EXPRARITP.tipo = EXPRARIT.tipo;
                } else {
                    EXPRARITP.tipo = ERROR_TIPO;
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            // EXPRARITP --> empty {73}
            //ACCION SEMANTICA 73
            if (analizarSemantica) {
                EXPRARITP.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void EXPRCOND( Atributos EXPRCOND) {
        //VARIABLES LOCALES
        Atributos EXPRREL = new Atributos();
        Atributos EXPRLOG = new Atributos();
        
        if (preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id") || preAnalisis.equals("(")
                || preAnalisis.equals("and") || preAnalisis.equals("or")) {
            // EXPRCOND -> EXPRREL EXPRLOG {25}
            EXPRREL(EXPRREL);
            EXPRLOG(EXPRLOG);
            //ACCION SEMANTICA 25
            if (analizarSemantica) {
                if ( EXPRREL.tipo.equals(BOOLEAN) && !EXPRLOG.tipo.equals(ERROR_TIPO)) {
                    EXPRCOND.tipo = BOOLEAN;
                } else {
                    EXPRCOND.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[EXPRCOND] Error en la condicion de la expresion"
                    + " N°Linea: " + cmp.be.preAnalisis.getNumLinea());
                }
            }
            //FIN ACCION SEMANTICA
            
        } else {
            error("[EXPRCOND] El texto debe ser ser uno de los siguientes "
                    + "tipos: num, num.num, idvar, literal, id, '(', and, or. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void EXPRREL(Atributos EXPRREL) {
        //VARIABLES LOCALES
        Atributos EXPRARIT = new Atributos();
        Atributos EXPRARIT1 = new Atributos();
        Matcher matcher1;
        Matcher matcher2;

        if (preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id") || preAnalisis.equals("(")) {
            // EXPRREL -> EXPRARIT oprel EXPRARIT {26}
            EXPRARIT(EXPRARIT);
            emparejar("oprel");
            EXPRARIT(EXPRARIT1);
            //ACCION SEMANTICA 26
            if (analizarSemantica) {
                matcher1 = arrChar.matcher(EXPRARIT.tipo);
                matcher2 = arrChar.matcher(EXPRARIT1.tipo);
                
                if (EXPRARIT.tipo.equals(EXPRARIT.tipo)) {
                    EXPRREL.tipo = BOOLEAN;
                } else if (EXPRARIT.tipo.equals("int") && EXPRARIT1.tipo.equals("float")) {
                    EXPRREL.tipo = BOOLEAN;
                } else if (EXPRARIT.tipo.equals("float") && EXPRARIT1.tipo.equals("int")) {
                    EXPRREL.tipo = BOOLEAN;
                } else if (matcher1.matches() && matcher2.matches()) {
                    EXPRREL.tipo = BOOLEAN;
                } else {
                    EXPRREL.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[EXPRREL] Incopatibilidad de Variables"
                            + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
                }
            }
            //FINACCION SEMANTICA
        } else {
            error("[EXPRREL] El texto debe ser ser uno de los siguientes "
                    + "tipos: num, num.num, idvar, literal o un identificador. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon 
    private void EXPRLOG(Atributos EXPRLOG) {
        //VARIABLES LOCALES
        Atributos EXPRREL = new Atributos();
        
        if (preAnalisis.equals("and")) {
            // EXPRLOG -> and EXPRREL {27}
            emparejar("and");
            EXPRREL(EXPRREL);
            //ACCION SEMANTICA 27
            if (analizarSemantica) {
                EXPRLOG.tipo = EXPRREL.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("or")) {
            // EXPRLOG -> or EXPRREL {28}
            emparejar("or");
            EXPRREL(EXPRREL);
            //ACCION SEMANTICA 28
            if (analizarSemantica) {
                EXPRLOG.tipo = EXPRREL.tipo;
            }
            //FIN ACCION SEMANTICA
        } else {
            // EXPRLOG -> empty {29}
            //ACCION SEMANTICA 29
            if (analizarSemantica) {
                EXPRLOG.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR:Monserrat Cervantes
    private void ELIMTAB( Atributos ELIMTAB) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        
        if (preAnalisis.equals("drop")) {
            // ELIMTAB -> drop table id {38}
            emparejar("drop");
            emparejar("table");
            id = cmp.be.preAnalisis;
            emparejar("id");
            //ACCION SEMANTICA 38
            if ( analizarSemantica ){
                if(checarArchivo(id.lexema+".db")){
                    cmp.ts.anadeTipo(id.entrada, "tabla");
                    ELIMTAB.tipo = VACIO;
                } else {
                    ELIMTAB.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[ELIMTAB] El identificador no es una tabla: " + id.lexema );
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[ELIMTAB] El texto debe iniciar con la palabra "
                    + "reservada 'drop'. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void IFELSE(Atributos IFELSE) {
        //VARIABLES LOCALES
        Atributos EXPRCOND = new Atributos();
        Atributos SENTENCIAS = new Atributos();
        Atributos IFELSEP = new Atributos();
        
        if (preAnalisis.equals("if")) {
            // IFELSE -> if EXPRCOND begin SENTENCIAS end IFELSEP {21}
            emparejar("if");
            EXPRCOND(EXPRCOND);
            emparejar("begin");
            SENTENCIAS(SENTENCIAS);
            emparejar("end");
            IFELSEP(IFELSEP);
            //ACCION SEMANTICA 21
            if (analizarSemantica) {
                if ( EXPRCOND.tipo.equals(BOOLEAN) && SENTENCIAS.tipo.equals(VACIO) && IFELSEP.tipo.equals(VACIO)) {
                    IFELSE.tipo = VACIO;
                } else {
                    IFELSE.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[IFELSE] Error en la condicion de if-else");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[IFELSE] El texto debe iniciar con la palabra "
                    + "reservada 'if'. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void IFELSEP(Atributos IFELSEP) {
        //VARIABLES LOCALES
        Atributos SENTENCIAS = new Atributos();
        
        if (preAnalisis.equals("else")) {
            // IFELSEP -> else begin SENTENCIAS end {22}
            emparejar("else");
            emparejar("begin");
            SENTENCIAS(SENTENCIAS);
            emparejar("end");
            //ACCION SEMANTICA 22
            if (analizarSemantica) {
                IFELSEP.tipo = SENTENCIAS.tipo;
            }
            //FIN ACCION SEMANTICA
        } else {
            // IFELSEP -> empty {23}
            //ACCION SEMANTICA 23
            if (analizarSemantica) {
                IFELSEP.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void IGUALACION(Atributos IGUALACION) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        Atributos EXPRARIT = new Atributos();
        Atributos IGUALACIONP = new Atributos();
        Matcher matcher1;
        Matcher matcher2;

        if (preAnalisis.equals("id")) {
            // IGUALACION -> id opasig EXPRARIT {60} IGUALACIONP {61}
            id = cmp.be.preAnalisis; //Se guarda el atributo de id
            emparejar("id");
            emparejar("opasig");
            EXPRARIT(EXPRARIT);
            //ACCION SEMANTICA 60
            if (analizarSemantica) {
                matcher1 = colmnChar.matcher(cmp.ts.buscaTipo(id.entrada));
                matcher2 = arrChar.matcher(EXPRARIT.tipo);

                if (matcher1.matches() && matcher2.matches()) {
                    IGUALACION.h = VACIO;
                } else if (cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(float)") && EXPRARIT.tipo.equals("int")) {
                    IGUALACION.h = VACIO;
                } else if (cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(float)") && EXPRARIT.tipo.equals("float")) {
                    IGUALACION.h = VACIO;
                } else if (cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(int)") && EXPRARIT.tipo.equals("int")) {
                    IGUALACION.h = VACIO;
                } else {
                    IGUALACION.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[IGUALACION] Incopatibilidad de Variables");
                }
            }
            //FINACCION SEMANTICA
            IGUALACIONP(IGUALACIONP);
            //ACCION SEMANTICA 61
            if (analizarSemantica) {
                if (IGUALACION.h.equals(VACIO) && IGUALACIONP.tipo.equals(VACIO)) {
                    IGUALACION.tipo = VACIO;
                } else {
                    IGUALACION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[IGUALACION] Error de tipos en la declaracion de variables");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[IGUALACION] El texto debe iniciar con un identificador. "
                    + "N°Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void IGUALACIONP(Atributos IGUALACIONP) {
        //VARIABLES LOCALES
        Atributos IGUALACION = new Atributos();

        if (preAnalisis.equals(",")) {
            // IGUALACIONP -> , IGUALACION {62}
            emparejar(",");
            IGUALACION(IGUALACION);
            //ACCION SEMANTICA 61
            if (analizarSemantica) {
                IGUALACIONP.tipo = IGUALACION.tipo;
            }
            //FIN ACCION SEMANTICA
        } else {
            // IGUALACIONP -> empty {63}
            //ACCION SEMANTICA 61
            if (analizarSemantica) {
                IGUALACIONP.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void INSERCION(Atributos INSERCION) {
        //VARIABLES LOCALES
        Atributos COLUMNAS = new Atributos();
        Atributos EXPRESIONES = new Atributos();
        Linea_BE id = new Linea_BE();

        if (preAnalisis.equals("insert")) {
            // INSERCION -> insert into id {46} ( COLUMNAS ){47} values ( EXPRESIONES ) {48}
            emparejar("insert");
            emparejar("into");
            id = cmp.be.preAnalisis;
            emparejar("id");
            //ACCION SEMANTICA 46
            if (analizarSemantica) {
                if (cmp.ss.checarArchivo(id.lexema + ".db")) {
                    cmp.ts.anadeTipo(id.entrada, "tabla");
                    INSERCION.h = VACIO;
                } else {
                    INSERCION.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[INSERCION] El id no existe: " + id.lexema);
                }
            }
            //FIN ACCION SEMANTICA 
            emparejar("(");
            COLUMNAS(COLUMNAS);
            emparejar(")");
            emparejar("values");
            emparejar("(");
            EXPRESIONES(EXPRESIONES);
            emparejar(")");
            //ACCION SEMANTICA 48
            if (analizarSemantica) {
                if (INSERCION.h.equals(VACIO) && EXPRESIONES.tipo.equals(VACIO) && COLUMNAS.tipo.equals(VACIO)) {
                    INSERCION.tipo = VACIO;
                } else if (INSERCION.h.equals(ERROR_TIPO)) {
                    INSERCION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[INSERCION] El id no es una tabla ");
                } else {
                    INSERCION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[INSERCION] Error de la ecpresion condicional ");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[INSERCION] el texto debe de iniciar con la palabra insert"
                    + "en la linea Numero " + cmp.be.preAnalisis.getNumLinea());
        }

    }

    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void LISTAIDS(Atributos LISTAIDS) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        Atributos LISTAIDS1 = new Atributos();

        if (preAnalisis.equals(",")) {
            // LISTAIDS -> , id {80} LISTAIDS {81}
            emparejar(",");
            id = cmp.be.preAnalisis; //ID GUARDADA
            emparejar("id");
            //ACCION SEMANTICA 80
            if (analizarSemantica) {
                if (checarArchivo(id.lexema + ".db")) {
                    LISTAIDS.h = VACIO;
                } else {
                    LISTAIDS.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[LISTAIDS] El id no existe: " + id.lexema);
                }
            }
            //FINACCION SEMANTICA
            LISTAIDS(LISTAIDS1);
            //ACCION SEMANTICA 81
            if (analizarSemantica) {
                if (LISTAIDS.h.equals(VACIO) && LISTAIDS1.tipo.equals(VACIO)) {
                    LISTAIDS.tipo = VACIO;
                } else {
                    LISTAIDS.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[LISTAIDS] Error de tipos en la declaracion de identificadores ");
                }
            }
            //FINACCION SEMANTICA
        } else {
            // LISTAIDS -> empty {82}
            //ACCION SEMANTICA 82
            if (analizarSemantica) {
                LISTAIDS.tipo = VACIO;
            }
            //FINACCION SEMANTICA
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void NULO(Atributos NULO) {
        //VARIABLES LOCALES (NO HAY)

        if (preAnalisis.equals("null")) {
            // NULO -> null {83}
            emparejar("null");
            //ACCION SEMANTICA 83
            if (analizarSemantica) {
                NULO.tipo = "null";
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("not")) {
            // NULO -> not null {84}
            emparejar("not");
            emparejar("null");
            //ACCION SEMANTICA 84
            if (analizarSemantica) {
                NULO.tipo = "not_null";
            }
            //FIN ACCION SEMANTICA
        } else {
            // NULO -> empty {85}
            //ACCION SEMANTICA 85
            if (analizarSemantica) {
                NULO.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void OPERANDO(Atributos OPERANDO) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        Linea_BE idvar = new Linea_BE();
        Linea_BE literal = new Linea_BE();

        if (preAnalisis.equals("num")) {
            //OPERANDO -> num {66}
            emparejar("num");
            //ACCION SEMANTICA 66
            if (analizarSemantica) {
                OPERANDO.tipo = "int";
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("num.num")) {
            //OPERANDO -> num.num {67}
            emparejar("num.num");
            //ACCION SEMANTICA 67
            if (analizarSemantica) {
                OPERANDO.tipo = "float";
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("idvar")) {
            //OPERANDO -> idvar {68}
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            //ACCION SEMANTICA 68
            if (analizarSemantica) {
                if (!cmp.ts.buscaTipo(idvar.entrada).equals("")) {
                    OPERANDO.tipo = cmp.ts.buscaTipo(idvar.entrada);
                } else {
                    OPERANDO.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[OPERANDO] ERROR: La variable no ha sido declarada"
                            + "NO. LINEA: " + cmp.be.preAnalisis.numLinea);
                }
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("literal")) {
            //OPERANDO -> literal {69}
            literal = cmp.be.preAnalisis;
            emparejar("literal");
            //ACCION SEMANTICA 69
            if (analizarSemantica) {
                OPERANDO.tipo = "char("+literal.lexema.length()+")";
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("id")) {
            //OPERANDO -> id {70}
            id = cmp.be.preAnalisis;
            emparejar("id");
            //ACCION SEMANTICA 70
            if (analizarSemantica) {
                //OPERANDO.tipo = cmp.ts.buscaTipo(id.entrada);
                if (analizarSemantica) {
                if (!cmp.ts.buscaTipo(id.entrada).equals("")) {
                    OPERANDO.tipo = cmp.ts.buscaTipo(id.entrada);
                } else {
                    OPERANDO.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[OPERANDO] ERROR: La variable no ha sido declarada"
                            + "NO. LINEA: " + cmp.be.preAnalisis.numLinea);
                }
            }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[OPERANDO], se esperaba recibir un valor de numero entero, "
                    + "numero de punto flotante, un identificador de variable "
                    + "una literal o un identificador "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void SENTENCIAS(Atributos SENTENCIAS) {
        //VARIABLES LOCALES
        Atributos SENTENCIA = new Atributos();
        Atributos SENTENCIAS1 = new Atributos();
        
        if (preAnalisis.equals("if")
                || preAnalisis.equals("while")
                || preAnalisis.equals("print")
                || preAnalisis.equals("assign")
                || preAnalisis.equals("select")
                || preAnalisis.equals("delete")
                || preAnalisis.equals("insert")
                || preAnalisis.equals("update")
                || preAnalisis.equals("create")
                || preAnalisis.equals("drop")
                || preAnalisis.equals("case")) {
            // SENTENCIAS -> SENTENCIA SENTENCIAS {8}
            SENTENCIA(SENTENCIA);
            SENTENCIAS(SENTENCIAS1);
            //ACCION SEMANTICA 8
            if (analizarSemantica) {
                if ( SENTENCIA.tipo.equals(VACIO) && SENTENCIAS1.tipo.equals(VACIO) ) {
                    SENTENCIAS.tipo = VACIO;
                } else {
                    SENTENCIAS.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SENTENCIAS] Error en las sentencias");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            // SENTENCIAS -> empty {9}
            //ACCION SEMANTICA 85
            if (analizarSemantica) {
                SENTENCIAS.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
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
        
        if (preAnalisis.equals("if")) {
            // SENTENCIA -> IFELSE {10}
            IFELSE(IFELSE);
            //ACCION SEMANTICA 10
            if (analizarSemantica) {
                SENTENCIA.tipo = IFELSE.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("while")) {
            // SENTENCIA -> SENREP {11}
            SENREP(SENREP);
            //ACCION SEMANTICA 11
            if (analizarSemantica) {
                SENTENCIA.tipo = SENREP.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("print")) {
            // SENTENCIA -> DESPLIEGUE {12} 
            DESPLIEGUE(DESPLIEGUE);
            //ACCION SEMANTICA 12
            if (analizarSemantica) {
                SENTENCIA.tipo = DESPLIEGUE.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("assign")) {
            // SENTENCIA -> SENTASIG {13}
            SENTASIG(SENTASIG);
            //ACCION SEMANTICA 13
            if (analizarSemantica) {
                SENTENCIA.tipo = SENTASIG.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("select")) {
            // SENTENCIA -> SENTSELECT {14}
            SENTSELECT(SENTSELECT);
            //ACCION SEMANTICA 14
            if (analizarSemantica) {
                SENTENCIA.tipo = SENTSELECT.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("delete")) {
            //SENTENCIA -> DELREG {15}
            DELREG(DELREG);
            //ACCION SEMANTICA 15
            if (analizarSemantica) {
                SENTENCIA.tipo = DELREG.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("insert")) {
            // SENTENCIA -> INSERCION {16}
            INSERCION(INSERCION);
            //ACCION SEMANTICA 16
            if (analizarSemantica) {
                SENTENCIA.tipo = INSERCION.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("update")) {
            // SENTENCIA -> ACTREGS {17}
            ACTREGS(ACTREGS);
            //ACCION SEMANTICA 17
            if (analizarSemantica) {
                SENTENCIA.tipo = ACTREGS.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("create")) {
            // SENTENCIA -> TABLA {18}
            TABLA(TABLA);
            //ACCION SEMANTICA 18
            if (analizarSemantica) {
                SENTENCIA.tipo = TABLA.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("drop")) {
            // SENTENCIA -> ELIMTAB {19}
            ELIMTAB(ELIMTAB);
            //ACCION SEMANTICA 19
            if (analizarSemantica) {
                SENTENCIA.tipo = ELIMTAB.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("case")) {
            // SENTENCIA -> SELECTIVA {20}
            SELECTIVA(SELECTIVA);
            //ACCION SEMANTICA 20
            if (analizarSemantica) {
                SENTENCIA.tipo = SELECTIVA.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("and")) {
            // SENTENCIA -> EXPRELOG {??}
            emparejar("and");
            EXPRREL(EXPRREL);
            //ACCION SEMANTICA ??
            if (analizarSemantica) {
                SENTENCIA.tipo = EXPRREL.tipo;
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("or")) {
            // SENTENCIA -> EXPRELOG {??}
            emparejar("or");
            EXPRREL(EXPRREL);
            //ACCION SEMANTICA ??
            if (analizarSemantica) {
                SENTENCIA.tipo = EXPRREL.tipo;
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[SENTENCIA] hubo un error en la creacion de sentencias"
                    + "ya que no contiene o estan mal escrita la sentencia de instruccion"
                    + "[if, while, print, assign, select, delete, insert, update, create, drop, case] "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void SELECTIVA(Atributos SELECTIVA) {
        //VARIABLES LOCALES
        Atributos SELWHEN = new Atributos();
        Atributos SELELSE = new Atributos();
        
        if (preAnalisis.equals("case")) {
            // SELECTIVA -> case SELWHEN SELELSE end {39}
            emparejar("case");
            SELWHEN(SELWHEN);
            SELELSE(SELELSE);
            emparejar("end");
            //ACCION SEMANTICA 39
            if (analizarSemantica) {
                if ( SELWHEN.tipo.equals(VACIO) && SELELSE.tipo.equals(VACIO) ) {
                    SELECTIVA.tipo = VACIO;
                } else {
                    SELECTIVA.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SELECTIVA] Error de sintaxis en calusula CASE");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[SELECTIVA] se esperaba la palabra 'case' "
                    + "de los distintos casos de seleccion a usar "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void SELWHEN(Atributos SELWHEN) {
        //VARIABLES LOCALES
        Atributos EXPRCOND = new Atributos();
        Atributos SENTENCIA = new Atributos();
        Atributos SELWHENP = new Atributos();

        if (preAnalisis.equals("when")) {
            // SELWHEN -> when EXPRCOND {40} then SENTENCIA SELWHENP {41}
            emparejar("when");
            EXPRCOND(EXPRCOND);
            //ACCION SEMANTICA 40
            if (analizarSemantica) {
                if (EXPRCOND.tipo.equals(BOOLEAN)) {
                    SELWHEN.h = VACIO;
                } else {
                    SELWHEN.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SELWHEN]La sentencias no es valida");
                }
            }
            //FIN ACCION SEMANTICA
            emparejar("then");
            SENTENCIA(SENTENCIA);
            SELWHENP(SELWHENP);
            //ACCION SEMANTICA 41
            if (analizarSemantica) {
                if (SENTENCIA.tipo.equals(VACIO) && SELWHENP.tipo.equals(VACIO) && SELWHEN.h.equals(VACIO)) {
                    SELWHEN.tipo = VACIO;
                } else {
                    SELWHEN.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SELWHEN]La sentencias no es valida");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[SELWHEN] Se esperaba la palabra 'when' seguida de una "
                    + "expresion condicional "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void SELWHENP(Atributos SELWHENP) {
        //VARIABLES LOCALES
        Atributos SELWHEN = new Atributos();

        if (preAnalisis.equals("when")) {
            // SELWHENP -> SELWHEN {42}
            SELWHEN(SELWHEN);
            //ACCION SEMANTICA 42
            if (analizarSemantica) {
                SELWHENP.tipo = SELWHEN.tipo;
            }
            //FIN ACCION SEMANTICA
        } else {
            // SELWHENP -> empty {43}
            //ACCION SEMANTICA 42
            if (analizarSemantica) {
                SELWHENP.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void SELELSE(Atributos SELELSE) {
        //VAARIABLES LOCALES
        Atributos SENTENCIA = new Atributos();

        if (preAnalisis.equals("else")) {
            // SELELSE -> else SENTENCIA {44}
            emparejar("else");
            SENTENCIA(SENTENCIA);
            //ACCION SEMANTICA 44
            if (analizarSemantica) {
                SELELSE.tipo = SENTENCIA.tipo;
            }
            //FIN ACCION SEMANTICA
        } else {
            // SELELSE -> empty {45}
            //ACCION SEMANTICA 45
            if (analizarSemantica) {
                SELELSE.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }

    }

    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes 
    private void SENREP(Atributos SENREP) {
        //VARIABLES LOCALES
        Atributos EXPRCOND = new Atributos();
        Atributos SENTENCIAS = new Atributos();
        
        if (preAnalisis.equals("while")) {
            // SENREP  -> while EXPRCOND begin SENTENCIAS end {24}
            emparejar("while");
            EXPRCOND(EXPRCOND);
            emparejar("begin");
            SENTENCIAS(SENTENCIAS);
            emparejar("end");
            //ACCION SEMANTICA 24
            if (analizarSemantica) {
                if (EXPRCOND.tipo.equals(BOOLEAN) && SENTENCIAS.tipo.equals(VACIO)) {
                    SENREP.tipo = VACIO;
                } else {
                    SENREP.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SENREP] Error de tipos en la comprobacion del WHILE");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[SENREP] se esperaba la palabra 'while' seguida de una Expresion Condicional "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }

    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void SENTASIG(Atributos SENTASIG) {
        //VARIABLES LOCALES
        Linea_BE idvar = new Linea_BE();
        Atributos EXPRARIT = new Atributos();
        Matcher matcher1;
        Matcher matcher2;
        
        if (preAnalisis.equals("assign")) {
            // SENTASIG -> assign idvar opasig EXPRARIT {31}
            emparejar("assign");
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            emparejar("opasig");
            EXPRARIT(EXPRARIT);
            //ACCION SEMANTICA 31
            if (analizarSemantica) {
                matcher1 = arrChar.matcher(cmp.ts.buscaTipo(idvar.entrada));
                matcher2 = arrChar.matcher(EXPRARIT.tipo);
                System.out.println(EXPRARIT.tipo);
                if (cmp.ts.buscaTipo(idvar.entrada).equals(EXPRARIT.tipo)) {
                    SENTASIG.tipo = VACIO;
                } else if (cmp.ts.buscaTipo(idvar.entrada).equals("float") && EXPRARIT.tipo.equals("int")) {
                    SENTASIG.tipo = VACIO;
                } else if (matcher1.matches() && matcher2.matches()) {
                    SENTASIG.tipo = VACIO;
                } else {
                    SENTASIG.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SENTASIG] Incopatibilidad de Variables" 
                    + cmp.be.preAnalisis.getNumLinea());
                }
            }
            //FINACCION SEMANTICA
        } else {
            error("[SENTASIG] se esperaba la palabra 'assign' seguida de una "
                    + "variable de identificador en la linea "
                    + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon
    private void SENTSELECT(Atributos SENTSELECT) {
        //VARIABLES LOCALES
        Atributos SENTSELECTC = new Atributos();
        Atributos EXPRCOND = new Atributos();
        Linea_BE id = new Linea_BE();
        Linea_BE idvar = new Linea_BE();
        Linea_BE idtabla = new Linea_BE();
        Matcher matcher1;
        Matcher matcher2;

        if (preAnalisis.equals("select")) {
            // SENTSELECT -> select idvar opasig id1 {51} SENTSELECTC from id2 {52} where EXPRCOND {53}
            emparejar("select");
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            emparejar("opasig");
            id = cmp.be.preAnalisis;
            emparejar("id");
            SENTSELECTC(SENTSELECTC);
            emparejar("from");
            idtabla = cmp.be.preAnalisis;
            emparejar("id");
            //ACCION SEMANTICA 52
            if (analizarSemantica) {
                matcher1 = arrChar.matcher(cmp.ts.buscaTipo(idvar.entrada));
                matcher2 = colmnChar.matcher(cmp.ts.buscaTipo(id.entrada));

                if (cmp.ss.checarArchivo(idtabla.lexema + ".db")) {
                    cmp.ts.anadeTipo(idtabla.entrada, "tabla");
                    System.out.println("id: " + cmp.ts.buscaTipo(id.entrada));
                    if (SENTSELECTC.tipo.equals(VACIO)) {
                        if (cmp.ts.buscaTipo(idvar.entrada).equals("int") && cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(int)")) {
                            SENTSELECT.h = VACIO;
                        } else if (cmp.ts.buscaTipo(idvar.entrada).equals("float") && cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(int)")) {
                            SENTSELECT.h = VACIO;
                        } else if (cmp.ts.buscaTipo(idvar.entrada).equals("float") && cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(float)")) {
                            SENTSELECT.h = VACIO;
                        } else if (matcher1.matches() && matcher2.matches()) {
                            SENTSELECT.h = VACIO;
                        } else {
                            SENTSELECT.h = ERROR_TIPO;
                            cmp.me.error(Compilador.ERR_SEMANTICO, "[SENTSELECT] El id no es tabla: " + idtabla.lexema);
                        }
                    } else {

                        SENTSELECT.h = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO, "[SENTSELECT] Error de tipos en la sentencia siguiente."
                                + id.lexema + "linea: " + cmp.be.preAnalisis.numLinea);
                    }
                } else {

                    SENTSELECT.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SENTSELECT] Error de tipos en la sentencia siguiente."
                            + id.lexema + "linea: " + cmp.be.preAnalisis.numLinea);
                }
            }
            //FIN ACCION SEMANTICA
            emparejar("where");
            EXPRCOND(EXPRCOND);
            //ACCION SEMANTICA 53--------------------------------------------------------------------------------------------------------------------------
            if (analizarSemantica) {
                if( SENTSELECT.h.equals(VACIO) && EXPRCOND.tipo.equals("boolean")){
                   SENTSELECT.tipo = VACIO;
              }else{
                  SENTSELECT.tipo = ERROR_TIPO;
                   cmp.me.error(Compilador.ERR_SEMANTICO, "[SENTSELECT]"
                           + " Incompatibilidad de tipos: " 
                           + cmp.be.preAnalisis.numLinea);
              }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[SENTSELECT] se esperaba la palabra select seguida de la "
                    + "siguiente estructura: "
                    + "[idvar opasig id SENTSELECTC from id where EXPRCOND] "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Monserrat Cervantes
    private void SENTSELECTC(Atributos SENTSELECTC) {
        //VARIABLE LOCAL
        Linea_BE idvar = new Linea_BE();
        Linea_BE id = new Linea_BE();
        Atributos SENTSELECTC1 = new Atributos();
        Matcher matcher1;
        Matcher matcher2;

        if (preAnalisis.equals(",")) {
            // SENTSELECTC -> ,idvar opasig id {54} SENTSELECTC {55}
            emparejar(",");
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            emparejar("opasig");
            id = cmp.be.preAnalisis;
            emparejar("id");
            //ACCION SEMANTICA 54---------------------------------------------------------------------------------------------------------------------------
            if (analizarSemantica) {
                matcher1 = arrChar.matcher(cmp.ts.buscaTipo(idvar.entrada));
                matcher2 = colmnChar.matcher(cmp.ts.buscaTipo(id.entrada));

                if (cmp.ts.buscaTipo(idvar.entrada).equals("int") && cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(int)")) {
                    SENTSELECTC.h = VACIO;
                } else if (cmp.ts.buscaTipo(idvar.entrada).equals("float") && cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(int)")) {
                    SENTSELECTC.h = VACIO;
                } else if (cmp.ts.buscaTipo(idvar.entrada).equals("float") && cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(float)")) {
                    SENTSELECTC.h = VACIO;
                } else if (matcher1.matches() && matcher2.matches()) {
                    SENTSELECTC.h = VACIO;
                } else {
                    SENTSELECTC.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SENTSELECTC] Incompatibilidad de tipos en la sentencia."
                            + id.lexema + "linea: " + cmp.be.preAnalisis.getNumLinea());

                }
            }
            //FIN ACCION SEMANTICA
            SENTSELECTC(SENTSELECTC1);
            //ACCION SEMANTICA 55
            if (analizarSemantica) {
                if (SENTSELECTC.h.equals(VACIO) && SENTSELECTC1.tipo.equals(VACIO)) {
                    SENTSELECTC.tipo = VACIO;
                } else {
                    SENTSELECTC.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SENTSELECTC] "
                            + "Incompatibilidad de tipos en la sentencia."
                            + id.lexema + "linea: " + cmp.be.preAnalisis.numLinea);
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            // SENTSELECTC -> empty
            //ACCION SEMANTICA 56
            if (analizarSemantica) {
                SENTSELECTC.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }

    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void TIPO(Atributos TIPO) {
        //VARIABLES LOCALES
        Linea_BE num = new Linea_BE();
        
        if (preAnalisis.equals("int")) {
            // TIPO -> int {5}
            emparejar("int");
            //ACCION SEMANTICA 5
            if (analizarSemantica) {
                TIPO.tipo = "int";
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("float")) {
            // TIPO -> float {6}
            emparejar("float");
            //ACCION SEMANTICA 6
            if (analizarSemantica) {
                TIPO.tipo = "float";
            }
            //FIN ACCION SEMANTICA
        } else if (preAnalisis.equals("char")) {
            // TIPO -> char ( num ) {7}
            emparejar("char");
            emparejar("(");
            num = cmp.be.preAnalisis;
            emparejar("num");
            emparejar(")");
            //ACCION SEMANTICA 7
            if (analizarSemantica) {
                TIPO.tipo = "char(" + num.lexema + ")";
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[TIPO] error al detectar que tipo de dato "
                    + "se va recibir [int, floar, char ( num )] "
                    + "en la linea " + cmp.be.preAnalisis.getNumLinea());
        }

    }

    //--------------------------------------------------------------------------
    //AUTOR: Ivan Rincon 
    private void TABLA(Atributos TABLA) {
        //VARIABLES LOCALES
        Linea_BE id = new Linea_BE();
        Atributos TABCOLUMNAS = new Atributos();
        
        if (preAnalisis.equals("create")) {
            //  TABLA -> create table id {32} (TABCOLUMNAS){33}
            emparejar("create");
            emparejar("table");
            id = cmp.be.preAnalisis;
            emparejar("id");
            //ACCION SEMANTICA 32
            if (analizarSemantica) {
                if ( cmp.ts.buscaTipo(id.entrada).isEmpty() || cmp.ts.buscaTipo(id.entrada).equals("") ) {
                    cmp.ts.anadeTipo(id.entrada, "tabla");
                    TABLA.h = VACIO;
                } else {
                    TABLA.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[TABLA] El id de la tabla ya fue declarado: " + id.lexema);
                }
            }
            //FIN ACCION SEMANTICA
            emparejar("(");
            TABCOLUMNAS(TABCOLUMNAS);
            emparejar(")");
            //ACCION SEMANTICA 33
            if (analizarSemantica) {
                if (TABLA.h.equals(VACIO) && TABCOLUMNAS.tipo.equals(VACIO)) {
                    TABLA.tipo = VACIO;
                } else {
                    TABLA.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[TABLA] Error de tipos en la declaracion de columnas");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[TABLA] se esperaba la palabra create en la linea "
                    + cmp.be.preAnalisis.getNumLinea());
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
        
        if (preAnalisis.equals("id")) {
            // TABCOLUMNAS -> id TIPO NULO {34} TABCOLUMNASP {35}
            id = cmp.be.preAnalisis;
            emparejar("id");
            TIPO(TIPO);
            NULO(NULO);
            //ACCION SEMANTICA 34
            if (analizarSemantica) {
                if ( cmp.ts.buscaTipo(id.entrada).isEmpty() || cmp.ts.buscaTipo(id.entrada).equals("") ) {
                    cmp.ts.anadeTipo(id.entrada, "COLUMNA(" + TIPO.tipo + ")");
                    TABCOLUMNAS.h = VACIO;
                } else {
                    TABCOLUMNAS.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[TABCOLUMNAS] El id de la columna ya fue declarado: " + id.lexema);
                }
            }
            //FIN ACCION SEMANTICA
            TABCOLUMNASP(TABCOLUMNASP);
            //ACCION SEMANTICA 35
            if (analizarSemantica) {
                if (TABCOLUMNAS.h.equals(VACIO) && TABCOLUMNASP.tipo.equals(VACIO)) {
                    TABCOLUMNAS.tipo = VACIO;
                } else {
                    TABCOLUMNAS.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[TABLA] Error de tipos en la declaracion de columnas");
                }
            }
            //FIN ACCION SEMANTICA
        } else {
            error("[TABCOLUMNAS] se esperaba la palabra id seguida del tipo de dato "
                    + "que se va a declarar en la linea "
                    + cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------
    //AUTOR: Emiliano Cepeda
    private void TABCOLUMNASP( Atributos TABCOLUMNASP) {
        //VARIABLES LOCALES
        Atributos TABCOLUMNAS = new Atributos();
        
        if (preAnalisis.equals(",")) {
            // TABCOLUMNASP -> , TABCOLUMNAS {36}
            emparejar(",");
            TABCOLUMNAS(TABCOLUMNAS);
            //ACCION SEMANTICA 36
            if (analizarSemantica) {
                TABCOLUMNASP.tipo = TABCOLUMNAS.tipo;
            }
            //FIN ACCION SEMANTICA
        } else {
            // TABCOLUMNASP -> empty {37}
            //ACCION SEMANTICA 37
            if (analizarSemantica) {
                TABCOLUMNASP.tipo = VACIO;
            }
            //FIN ACCION SEMANTICA
        }
    }
}
//------------------------------------------------------------------------------
//::
