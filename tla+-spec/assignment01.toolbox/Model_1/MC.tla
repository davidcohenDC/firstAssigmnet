---- MODULE MC ----
EXTENDS assignment01, TLC

\* CONSTANT definitions @modelParameterConstants:0MaxNumFiles
const_168133420962857000 == 
4
----

\* INIT definition @modelBehaviorNoSpec:0
init_168133420962858000 ==
FALSE/\item_ = 0
----
\* NEXT definition @modelBehaviorNoSpec:0
next_168133420962859000 ==
FALSE/\item_' = item_
----
=============================================================================
\* Modification History
\* Created Wed Apr 12 23:16:49 CEST 2023 by HP
