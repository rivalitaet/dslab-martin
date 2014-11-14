
Details / Questions
===================

 * Calculations are parsed to trees and calculated recursively. 
   This is also nice for prices. 
   It's an unbalanced tree (right side is always a number), therefore 
   parallelization of calculations wouldn't be trivial.

 * Controller handles a list of nodes. Is there a _really_ nice way to handle 
   this?
 
 * Node: Wouldn't a logging thread be nicer (and also avoid the ThreadLocal)?

 * How can you deal with assertions (à la "this code should never be reached")
   in a good way?


Status
======

Client
------

 * All commands work


Controller
----------

 * All commands work
 * UDP works
 * Commands from Client work
 * Commands to Nodes work


Node
----

 * Everything works
 
