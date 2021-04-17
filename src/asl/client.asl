// Agent customer in project naatho_ier

/* Initial beliefs and rules */

/* Initial goals */


/* Plans */


+!at(X,Y) : at(X,Y) <- true.

+!at(X,Y) : at(X0,Y0)
	<-
        if(X0 < X) {
             move(right);
        }
        elif(X0 > X) {
            move(left);
        }
        elif(Y0 < Y) {
            move(down);
        }
        elif(Y0 > Y) {
            move(up);
        };
        .abolish(at(X0,Y0));
        .perceive;
		!at(X,Y).
		 
 -!at(X,Y) <-  !at(X,Y).
		

		
+distributer_at(X,Y) : not arrived_at_dist <- !at(X,Y) ;
	+arrived_at_dist;
	+call_for_taxi.


+call_for_taxi : true <-
			.send("broker", tell, called_for_taxi).

+cashier_at(NAME,X,Y) : call_for_taxi <- !at(X,Y-1); +finished.

+finished : cashier_at(NAME,X,Y) <-
	.send(NAME, tell, decreaseCount);
   	go_home.


+home_at(X,Y) : not arrived_at_home <- !at(X,Y) ;
	+arrived_at_home.

+arrived_at_home :name(S) <-
	remove(S);
	.kill_agent(S).
