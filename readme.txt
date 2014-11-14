Reflect about your solution!

Summary:

Details / Questions
===================

 * Calculations are parsed to trees and calculated recursively. 
   This is also nice for prices. 
   It's an unbalanced tree (right side is always a number), therefore 
   parallelization of calculations wouldn't be trivial.

 * Controller handles a list of nodes. Is there a _really_ nice way to handle
   this?
 
 * Wouldn't a logging thread be nicer (and also avoid the ThreadLocal)?

Status
======

Client
------

 * 98% works
 * does not react to closed controllers
	
Controller
----------

 * 90 % works
 * Handling of nodes need to be improved.
 
Node
----

 * fully works like in specification
