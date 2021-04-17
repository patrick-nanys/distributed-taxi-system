// Agent cashier in project naatho_ier

/* Initial beliefs and rules */

/* Initial goals */

/* Plans */


+customer_arrive[source(S)] : custcount(X) <- 
 	.send(S, tell, custcount(X));
 	.abolish(customer_arrive).
 	
 +you_win : custcount(X) <-
 		+custcount(X+1);
 		.abolish(custcount(X));
 		.abolish(you_win).
 		
+decreaseCount : custcount(X) <-
		+custcount(X-1);
		.abolish(custcount(X));
		.abolish(decreaseCount).
