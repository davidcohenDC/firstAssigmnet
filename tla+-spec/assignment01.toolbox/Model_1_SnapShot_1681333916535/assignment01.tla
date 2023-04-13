--------------------------- MODULE assignment01 ---------------------------

EXTENDS TLC, Integers, Sequences
CONSTANTS MaxNumFiles

(*--algorithm readers_writers

variables distribution = <<>>, mutex = 1;

define
  NoOverflowFiles == Len(distribution) <= MaxNumFiles
  MutualExclusion == []~(pc["writer1"] = "write" /\ (pc["reader1"] = "read" \/ pc["reader2"] = "read"))
  NoStarvationWriter == [](pc["writer1"] = "w1" ~> <>(pc["writer1"] = "write"))
  NoStarvationReader == []((pc["reader1"] = "w2" ~> <>(pc["reader1"] = "read")) \/ (pc["reader2"] = "w2" ~> <>(pc["reader2"] = "read")))
  DeadlockFreedom == \A subset \in SUBSET { "writer1", "reader1", "reader2" } : ~(subset = {} /\ \A self \in subset : pc[self] \in {"ReadInterval", "WriteInterval"})
end define;

macro wait(s) begin
  await s > 0;
  s := s - 1;
end macro;

macro signal(s) begin
  s := s + 1;
end macro;

fair+ process writer \in { "writer1" }
variable item = <<>>;
begin WriteInterval:
  while TRUE do
        w1: wait(mutex);
        write: 
            item := <<"interval", "file">>;
            distribution := Append(distribution, item);
        s1: signal(mutex);
    put:
        await Len(distribution) < MaxNumFiles;
        \*Contains(item) == item \in UnionSeq(distribution)
        distribution := Append(distribution, item);
  end while;
end process;

fair+ process reader \in { "reader1", "reader2" }
variable item = <<"none", "none">>;
begin ReadInterval:
  while TRUE do
    take:
        await distribution /= <<>>;
        item := Head(distribution);
        distribution := Tail(distribution);
        w2: wait(mutex);
        read: print item;
        s2: signal(mutex);
  end while;
end process;
end algorithm;*)

\* BEGIN TRANSLATION (chksum(pcal) = "e379607b" /\ chksum(tla) = "1afe7402")
\* Process variable item of process writer at line 28 col 10 changed to item_
VARIABLES distribution, mutex, pc

(* define statement *)
NoOverflowFiles == Len(distribution) <= MaxNumFiles
MutualExclusion == []~(pc["writer1"] = "write" /\ (pc["reader1"] = "read" \/ pc["reader2"] = "read"))
NoStarvationWriter == [](pc["writer1"] = "w1" ~> <>(pc["writer1"] = "write"))
NoStarvationReader == []((pc["reader1"] = "w2" ~> <>(pc["reader1"] = "read")) \/ (pc["reader2"] = "w2" ~> <>(pc["reader2"] = "read")))
DeadlockFreedom == \A subset \in SUBSET { "writer1", "reader1", "reader2" } : ~(subset = {} /\ \A self \in subset : pc[self] \in {"ReadInterval", "WriteInterval"})

VARIABLES item_, item

vars == << distribution, mutex, pc, item_, item >>

ProcSet == ({ "writer1" }) \cup ({ "reader1", "reader2" })

Init == (* Global variables *)
        /\ distribution = <<>>
        /\ mutex = 1
        (* Process writer *)
        /\ item_ = [self \in { "writer1" } |-> <<>>]
        (* Process reader *)
        /\ item = [self \in { "reader1", "reader2" } |-> <<"none", "none">>]
        /\ pc = [self \in ProcSet |-> CASE self \in { "writer1" } -> "WriteInterval"
                                        [] self \in { "reader1", "reader2" } -> "ReadInterval"]

WriteInterval(self) == /\ pc[self] = "WriteInterval"
                       /\ pc' = [pc EXCEPT ![self] = "w1"]
                       /\ UNCHANGED << distribution, mutex, item_, item >>

w1(self) == /\ pc[self] = "w1"
            /\ mutex > 0
            /\ mutex' = mutex - 1
            /\ pc' = [pc EXCEPT ![self] = "write"]
            /\ UNCHANGED << distribution, item_, item >>

write(self) == /\ pc[self] = "write"
               /\ item_' = [item_ EXCEPT ![self] = <<"interval", "file">>]
               /\ distribution' = Append(distribution, item_'[self])
               /\ pc' = [pc EXCEPT ![self] = "s1"]
               /\ UNCHANGED << mutex, item >>

s1(self) == /\ pc[self] = "s1"
            /\ mutex' = mutex + 1
            /\ pc' = [pc EXCEPT ![self] = "put"]
            /\ UNCHANGED << distribution, item_, item >>

put(self) == /\ pc[self] = "put"
             /\ Len(distribution) < MaxNumFiles
             /\ distribution' = Append(distribution, item_[self])
             /\ pc' = [pc EXCEPT ![self] = "WriteInterval"]
             /\ UNCHANGED << mutex, item_, item >>

writer(self) == WriteInterval(self) \/ w1(self) \/ write(self) \/ s1(self)
                   \/ put(self)

ReadInterval(self) == /\ pc[self] = "ReadInterval"
                      /\ pc' = [pc EXCEPT ![self] = "take"]
                      /\ UNCHANGED << distribution, mutex, item_, item >>

take(self) == /\ pc[self] = "take"
              /\ distribution /= <<>>
              /\ item' = [item EXCEPT ![self] = Head(distribution)]
              /\ distribution' = Tail(distribution)
              /\ pc' = [pc EXCEPT ![self] = "w2"]
              /\ UNCHANGED << mutex, item_ >>

w2(self) == /\ pc[self] = "w2"
            /\ mutex > 0
            /\ mutex' = mutex - 1
            /\ pc' = [pc EXCEPT ![self] = "read"]
            /\ UNCHANGED << distribution, item_, item >>

read(self) == /\ pc[self] = "read"
              /\ PrintT(item[self])
              /\ pc' = [pc EXCEPT ![self] = "s2"]
              /\ UNCHANGED << distribution, mutex, item_, item >>

s2(self) == /\ pc[self] = "s2"
            /\ mutex' = mutex + 1
            /\ pc' = [pc EXCEPT ![self] = "ReadInterval"]
            /\ UNCHANGED << distribution, item_, item >>

reader(self) == ReadInterval(self) \/ take(self) \/ w2(self) \/ read(self)
                   \/ s2(self)

Next == (\E self \in { "writer1" }: writer(self))
           \/ (\E self \in { "reader1", "reader2" }: reader(self))

Spec == /\ Init /\ [][Next]_vars
        /\ \A self \in { "writer1" } : SF_vars(writer(self))
        /\ \A self \in { "reader1", "reader2" } : SF_vars(reader(self))

\* END TRANSLATION 


=============================================================================
