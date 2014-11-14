
Details / Questions
===================

 * Calculations are parsed to trees and calculated recursively. 
   This is also nice for prices. 
   It's an unbalanced tree (right side is always a number), therefore 
   parallelization of calculations wouldn't be trivial.

 * Controller handles a list of nodes. Is there a _really_ nice way to handle
   this?
 
 * Node: Wouldn't a logging thread be nicer (and also avoid the ThreadLocal)?


Status
======

Client
------

 * All commands work
 * Does not react to closed controllers
	
Controller
----------

 * Everything should work
 
Node
----

 * Everything works
 
