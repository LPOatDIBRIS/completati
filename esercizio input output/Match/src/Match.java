//il programma Implementare la classe Java Match che permette di eseguire il comando
//java Match reg_exp option*,
// esempio d esecuzione:-> java Match "([0-9]+)|([a-zA-Z]+)" -s 23 -g 1
//                          String 23 matches group 1





import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;// per la gestione delle opzioni utiliziamo l interfaccia map tramite la sua implementazione hashtable fortina dalla clase hashmap
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException; // gli ultimi 2 import servono per gestire le regexp

public class Match {
    //opzioni aviabili, definiamo una serie di costanti di tipo stringa, il loro valore non cambia, sono variabili di classe e definiscono la sintassi delle opzioni disponibili
    private static final String STRING_OPT = "-s";
    private static final String GROUP_OPT = "-g";
    private static final String OUTPUT_OPT = "-o";

    //espressione regolare argomento, inizialmente non inizializzata, variabile di classe
    private static String regex;

    //creiamo un hashmap per la gestione delle opzioni, una mappa che altro non e che un dizionario implementanto trmaite la classe generica hashmap
    // che inizialmente e vuota e poi viene inizializzata tramite l inizializzatore statico, anche la variabile option e una variabile statica e anche final, final perche una votla creata la mappa non abbiamo bisogno
    //di crearne altre, statica perche ragioniamo di nuovo a livello di classe e quindi variabile di classe e non livello singolo oggetto.
    //nel inizializzatore andiamo a inserire dentro al nostro dizionario, le associazioni tra chiave e valori, le chiavi sono di tipo string, l interfaccia map è un interfaccia, l inizializzatore ha il compito di inserire
    //dentro al nostro dizionario le associazioni chiavi valori disponibili, in questo caso rappresentate dalle nostre 3 costanti, e il valore asociato di tipo stringa è il valore di default, per l opzione -s e -g esiste
    // un opzione di default che sono stringa vuota e gruppo 0, l output non ce alcuna stringa di default e quindi inizializziamo l associazione con il valore null. se decidessimo di avere un valore di default anche per il file di output
    //un nome ad esempio, output.txt allora associeremo quello, in questo caso e quello di default ovvero stdout e qundi e meglio usare un approcio generico e mettere null per dire non esiste un valore prefissato di default.
    public static final Map<String, String> options = new HashMap<>();

    static {
        options.put(STRING_OPT, ""); //inseriamo la chiave STRING_OPT  che ha valore -s e gli associamo il valore del paramentro a sinistra
        options.put(GROUP_OPT, "0");// stessa cosa di sopra con GROUP_COUNT
        options.put(OUTPUT_OPT, null);// null

    }

    //manage errors, gestire errori, prende come argomento la stringa che e il messaggio di errore e lo stampa sullo stderr e chiamare il metodo exit della classe predefinita
    // system con argomento 1 per dire che il programma e interrotto a causa di un errore. se il codice d uscita e 0 vuol dire che e terminato senza errori altrimenti con errori.
    private static void error(String msg) {
        System.err.println(msg); // stampiamo su stderr ( standard error)
        System.exit(1);
    }


    //process all option and their arguments
    // metodo statico che ci permette di gestire gli argomenti passati al main, gli argomenti passanti al main sono un array di stringhe, il primo controllo è che ci deve essere
    // almeno un argomento obbligatorio che è la regex se manca stampiamo il messaggio di errore e interrompiamo il programma, altrimenti possiamo inizializzare la variabile statica regex con args[0],
    // dopo di che entriamo in un loop che ci permette di gestire le varie opzioni che seguono l argomento, questo loop va a considerare tutti gli elementi del array di stringhe args, notiamo che non
    // conviene usare un for each per il semplice motivo che il primo argomento e stato gia gestito dalle linee precedenti, dopo dic he quello
    // che controlliamo è inanzitutto che l argomento corrente, che abbiamo memorizzato nella variabile locale opt corrisponda effetivamente a un opzione valida, verichiamo che l opzione corrisponde alla sintassi valida,
    // e qundi andiamo a controllare che nella nostra mappa esista la chiave corrisopnde al argomento corrente che è satto memorizzato nella variaible locale opt usando il metodo containsKey,
    // se restituisce false vuol dire che il nostro arogmento non corrisponde a nessuna opzione valida e quindi mettiamo un errore col corrisopndente messaggio in cui evidenziamo quale sia la sintassi corretta delle ozioni,
    //l altro controllo è che tutte le opzioni considerato da quesot programma hanno un argomento, e quindi bisogna verificare che esiste effettivamente tale
    // argomento, se la nostra opzione è l ultimo argomento, significa che i+1 è ugaule a args.lenght e qundi significa che ci siamo dimenticati i coriropsndente argomento e quindi anche in quesot caso dobbiamo mettere un messaggio di erroe
    // se questi 2 controlli passano effetivamente quello che si puo fare è aggiornare l associazoine tra la nostra opzione e l argomento, che andiamo a leggere nel array alla posizione successiva qiunddi andiamo a leggere nel array nella
    //posizione successiva e quindi andiamo a scrivere args[++i] e in questo modo usando l operatore di autoincremento prefisso andiamo prima a incrementare automaticamente i di 1 e poi ad accedere al corrispondente elemento che sicuramente ce
    //perche siamo arrivati fino a qui.
    private static void processArgs(String[] args)
    {
        if(args.length==0) error("expecting a regular expression argument as argument"); // ci deve sempre essere almeno la regexp.

        regex=args[0];  // salviamo la nostra  regex
        for (var i=1; i<args.length; i++)
        {
            var opt=args[i];
            if(!options.containsKey(opt)) error("Option error.\nValid Option:\n\t-s <string> \n\t-g <group> \n\t-o <output filename>");
            if(i+1== args.length) error("missing argument for option" + opt);

            options.put(opt, args[++i]);
        }
    }

    // tries to open the output stream: metodo ausiliario sempre, puo sollevare l eccezzione filenotfoundexcpetion, e un eccezzione controllata, quindi bisogna o dichiararle nel intestazione d
    //del metodo o vanno gestite dentro il metoodo con un try chatch, in questo caso la gestione delle cezzione viene demandata al main uqindi semplicemente potrebbe sollverare un eccezzione, questo metodo viene chiamato dal
    // main, va a controllare tramite il metodo get se ce una stringa associata al opzione -o e se ce una stringa associata al ozine, i risutlati sono di 2 tipi o non e stata passata e quindi get restituisce null come
    // abbiamo inziializzato noi oppure e stato passato con il corrispdnete argomento e qiundi get resituisce una stringa e a seconda dei 2 casi andremo a usare 2 costruttorti diversi overloaded della stesa classe
    // printwirter chee è una classe decorator che ci permette di aggiungere funzioalita a stream di output in paritoclare la possiblita di usar eil metodo print printline(), i 2 costruttori sono diversi perche system,out non è
    // deello stesso di path,
    private static PrintWriter tryOpenOutput() throws FileNotFoundException {
        var path = options.get(OUTPUT_OPT);
        return path == null ? new PrintWriter(System.out) : new PrintWriter(path);
    }





//main: chiamiamo il metodo ausiliario process args, con args ovviamente come paramentro, e poi abbiamo un try with resources che ci permette di gestire in automatica apertura e chiusura del file di output
// tramite il metodo tryopenoutput, la cosa interessente e che dato che usiamo questo try esteso che hanno delle risorse che si possono usare associate come variabili locali, non abiamo bisogno di chiudere esplicitamente il nostro
// stream poerche questo vien fatto automaticamente, tutte le variabili dichiarate nel try with resource devono essere di tipo autoclosable e quindi risorse la cui chiusura puo essere gestita automaticamente, questo e il caso di printwriter che e
// sottotipo del interfaccia autoclosable. dopo di che abbiamo il blocco contenente il rpogramma, viene inizializzata la variaible string tramite get associandola al valore del ozpione, poi viene creato un matcher a
// partire da regex che e stata inizializzata da process args usando il factory metodo matcher che iene utilizzato per inizializzare l input del matcher tramite proprio stirng, quindi creiamo prprio un mathcer associato a regex e la stringa string
// compile potrebbe sollevare un eccezzione , dopo di che gestiamo il gruppo, anche qua andiamo a recupare la stringa corrispondete al gruppo, quindi la stringa assoicata a -g, l andiamo a convertire a intero tramite parse int di integer
// anche qui poterbbe eesser soollevata a un eccezione, dopo di che facciamo un altro controllo ossia che il gruppo sia un valido numero intero e che sia ance un valido gruippo della nsotra regex, chiaramente non puo essere negativo
// il numnero piu baso valido per le regex e 0, ma non puo ecedere anche un certo limite che e il numero di gruppi dela regexp, dopo di che apssiamo e finalmente possiamo verificare s ela nsotra stringa appartiene al gruppo specificato
// per fare quesot bisogna inizitutto chjiamare il metodo matches sul matcher e dopo di che verificare chiamando il meodo di query group col il gruppo definito nella variabile group e verificare che il risutlato e diverso da null,
// se le condizioni sono verificato allora il verbo verb del output sara matches oppure not mathces, dopo di che andiamo a stampare tramite println tutto quanto. dopo di che ce tuta una seria di catch in cascata che gestiscono i posibli errori,



    public static void main (String args[])
    {
        processArgs(args);
        try (var pw = tryOpenOutput()) // may throw FileNotFoundException
        {

            var string=options.get(STRING_OPT); //stringa associata a -s
            var matcher = Pattern.compile(regex).matcher(string); // matcher inizializzato con l associazione string e regexp
            var group=Integer.parseInt(options.get(GROUP_OPT));// numero del gruppo dopo l opzione -g
            if (group < 0 || group > matcher.groupCount())//controllo sul gruppo
                error("Invalid group " + group + " for regular expression " + regex);
            if(matcher.matches() && matcher.group(group)!=null) System.out.println("la stringa " + string + " combacia col gruppo " + group);
            else
                System.out.println("dio cane schifosissimo");
        } catch (PatternSyntaxException e) {
            error("Argument is not a valid regular expression");
        } catch (NumberFormatException e) {
            error("Group is not a valid integer");
        } catch (FileNotFoundException e) {
            error("Output error: " + e.getMessage());
        } catch (Throwable t) {
            error("Unexpected error: " + t.getMessage());
        }
    }


    }



