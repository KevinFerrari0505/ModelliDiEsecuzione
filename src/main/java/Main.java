import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Confronto tra diversi modelli di esecuzione in Java:
 * sequenziale, concorrente (thread pool), e parallela (parallel stream).
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        testCounter();
        testEsecuzioneSequenziale();
        testEsecuzioneConcorrente();
        testEsecuzioneParallela();
    }

    // -------------------------------------------------------------------------
    // COUNTER
    // Dimostra la race condition: due thread incrementano lo stesso contatore
    // senza sincronizzazione, quindi il risultato finale può essere < 2000.
    // -------------------------------------------------------------------------
    private static void testCounter() throws InterruptedException {
        Counter c = new Counter();

        // Entrambi i thread eseguono lo stesso task sullo stesso oggetto Counter
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                c.increment(); // operazione NON atomica → possibile race condition
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        // join() blocca il main finché t1 e t2 non terminano
        t1.join();
        t2.join();

        System.out.println("Contatore finale: " + c.getCount()); // spesso < 2000
        System.out.println("Processori disponibili: " + Runtime.getRuntime().availableProcessors());
    }

    // -------------------------------------------------------------------------
    // ESECUZIONE SEQUENZIALE
    // Le due chiamate a lavoroPesante() vengono eseguite una dopo l'altra
    // sullo stesso thread → tempo totale ≈ T1 + T2.
    // -------------------------------------------------------------------------
    private static void testEsecuzioneSequenziale() {
        long start = System.currentTimeMillis();

        lavoroPesante();
        lavoroPesante();

        long end = System.currentTimeMillis();
        System.out.println("Tempo sequenziale: " + (end - start) + " ms");
    }

    // -------------------------------------------------------------------------
    // ESECUZIONE CONCORRENTE (ExecutorService)
    // I due task vengono affidati a un thread pool di 2 thread.
    // Se la macchina ha almeno 2 core, girano in parallelo → tempo ≈ max(T1, T2).
    //
    // Future<?> rappresenta il risultato di un task che terminerà in futuro;
    // future.get() è bloccante: aspetta che il task sia completato.
    // -------------------------------------------------------------------------
    private static void testEsecuzioneConcorrente() throws InterruptedException, ExecutionException {
        // Pool fisso di 2 thread: i task in eccesso vengono messi in coda
        ExecutorService executor = Executors.newFixedThreadPool(2);

        long start = System.currentTimeMillis();

        Future<?> f1 = executor.submit(() -> lavoroPesante());
        Future<?> f2 = executor.submit(() -> lavoroPesante());

        f1.get(); // attende il completamento di f1
        f2.get(); // attende il completamento di f2

        long end = System.currentTimeMillis();
        System.out.println("Tempo concorrente: " + (end - start) + " ms");

        // shutdown() impedisce l'invio di nuovi task e termina i thread del pool
        // senza chiamarlo, la JVM non terminerebbe mai
        executor.shutdown();
    }

    // -------------------------------------------------------------------------
    // ESECUZIONE PARALLELA (parallel stream)
    // LongStream.parallel() divide automaticamente il range tra tutti i core
    // disponibili usando il ForkJoinPool comune → la JVM gestisce tutto.
    // Più semplice da scrivere rispetto all'ExecutorService, ma meno controllabile.
    // -------------------------------------------------------------------------
    private static void testEsecuzioneParallela() {
        long start = System.currentTimeMillis();

        // Il range [0, 2_000_000_000) viene suddiviso in chunk elaborati in parallelo
        long sum = java.util.stream.LongStream.range(0, 2_000_000_000L).parallel().sum();

        long end = System.currentTimeMillis();
        System.out.println("Tempo parallelStream: " + (end - start) + " ms");
    }

    // -------------------------------------------------------------------------
    // UTILITY
    // Simula un carico computazionale pesante (somma da 0 a 1 miliardo).
    // Usato per rendere apprezzabile la differenza di tempo tra i vari approcci.
    // -------------------------------------------------------------------------
    private static long lavoroPesante() {
        long sum = 0;
        for (long i = 0; i < 1_000_000_000L; i++) {
            sum += i;
        }
        return sum;
    }
}