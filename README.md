# 🧵 ModelliDiEsecuzione

Progetto Java che esplora e confronta diversi **modelli di esecuzione concorrente e parallela**, mostrando in modo pratico le differenze di performance e i rischi della programmazione multi-thread.

---

## 📌 Cosa dimostra

| Test | Descrizione |
|------|-------------|
| `testCounter` | Race condition su un contatore condiviso senza sincronizzazione |
| `testEsecuzioneSequenziale` | Due task pesanti eseguiti uno dopo l'altro |
| `testEsecuzioneConcorrente` | Due task eseguiti in parallelo tramite `ExecutorService` |
| `testEsecuzioneParallela` | Somma su miliardi di elementi con `parallel stream` |

---

## ⚠️ Race Condition

```java
// Due thread incrementano lo stesso Counter contemporaneamente
// increment() NON è atomica → il risultato finale è spesso < 2000
t1.start();
t2.start();
t1.join();
t2.join();
System.out.println(c.getCount()); // es. 1847 invece di 2000
```

Questo è un esempio classico di **race condition**: quando due thread leggono e scrivono la stessa variabile senza sincronizzazione, alcune operazioni si sovrappongono e gli aggiornamenti vanno persi.

---

## ⏱️ Confronto Performance

```
Processori disponibili: 8
Tempo sequenziale:    ~900 ms   (T1 + T2)
Tempo concorrente:    ~480 ms   (max(T1, T2))
Tempo parallelStream: ~200 ms   (diviso su tutti i core)
```

> I valori variano in base alla macchina. Con più core, il guadagno è più evidente.

---

## 🔧 Struttura del Progetto

```
src/
└── main/
    └── java/
        ├── Main.java       # Entry point, tutti i test
        └── Counter.java    # Contatore condiviso (non thread-safe)
```

---

## 🚀 Come eseguire

### Con IntelliJ IDEA
Apri il progetto e premi **Run** su `Main.java`.

### Con Gradle da terminale
```bash
git clone https://github.com/KevinFerrari0505/ModelliDiEsecuzione.git
cd ModelliDiEsecuzione
.\gradlew build      # Windows
./gradlew build      # Linux/macOS
.\gradlew run        # Windows
./gradlew run         # Linux/macOS
```

---

## 📚 Concetti chiave

- **`Thread`** — unità base di esecuzione concorrente in Java
- **`join()`** — blocca il thread chiamante finché il thread target non termina
- **`ExecutorService`** — pool di thread riutilizzabili, più efficiente del creare thread a mano
- **`Future<?>`** — rappresenta il risultato di un task che terminerà in futuro; `.get()` è bloccante
- **`executor.shutdown()`** — necessario per terminare correttamente il pool, altrimenti la JVM non si chiude
- **`LongStream.parallel()`** — divide automaticamente il lavoro tra tutti i core disponibili usando il `ForkJoinPool`

---

## 🛠️ Tecnologie

- Java 17+
- Gradle (Kotlin DSL)
- JUnit 5 (configurato ma non ancora usato)
