--------------------------- MODULE assignment01 ---------------------------
\* nel libro Practical TLA+ vengono chiamati readers and writers!
\* astrarre il più possibile dall'architettura del sistema, serve solo per analizzare la strategia che modella il comportamento -> semplificare
\* Esempio: usare una struttura dati predefinita come List<String> che modella il contenuto dei file
\* e bisogna dare il contenuto, in base all'architettura che si sceglie all'interno del bounded buffer
\* Oppure si vuole verificare che tutti i file vengano elaborati, non c'è bisogno neanche di vedere cosa c'è all'interno dei file
\* Lettura dei file e poi elaborazione: usare i semafori per modellare la sezione critica, se usiamo i monitor nel codice,
\* con TLA+ non è possibile crearli ma si possono utilizzare i semafori per modellare lo stesso comportamento ad un certo livello

\* Nel caso Mandelbrot ad esempio prendere il Controller verifyAPI (verfyBegin, verifyEnd) rendere atomica (un'unica azione) per non fare interleaving
\* o si toglie tutto (tutti i calcoli, dipende dal tipo di dato che si ha) e si lasciano solo i punti di sincronizzazione
\* ridurre il programma in uno minimale in modo che non cambia il coordinamento tra i thread

EXTENDS TLC, Integers, Sequences
CONSTANTS MaxQueueSize

(*--algorithm message_queue
variable queue = <<>>; \* variabile condivisa
define
  \* bounded buffer senza always [] da definire all'interno di Invariants
  BoundedQueue == Len(queue) <= MaxQueueSize
end define;

process producer \in { "prod1", "prod2" } \* due produttori
variable item = "";
begin Produce:
  while TRUE do
    produce:
        item := "item";
    put:
        \* operazioni atomiche
        await Len(queue) < MaxQueueSize; \* aspetta che ci sia posto, Len: dimensione della coda
        queue := Append(queue, item);
  end while;
end process;

process consumer \in { "cons1", "cons2" } \* due consumatori
variable item = "none";
begin Consume:
  while TRUE do
    take:
        await queue /= <<>>;
        item := Head(queue);  \* head: elemento della lista
        queue := Tail(queue); \* tail: puntatore/riferimento alla coda della lista (che è il resto della lista) -> rimozione
    consume:
        print item; \* stampa degli elementi su console (astrazione del consumo degli elementi della coda)
  end while;
end process;
end algorithm;*)


=============================================================================
