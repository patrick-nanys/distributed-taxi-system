// Agent customer in project naatho_ier

/* Initial beliefs and rules */

// go_to(X,Y).

/* Initial goals */

!call_for_taxi.

/* Plans */

+!call_for_taxi <-
    ?at(X,Y);
    .send("broker", tell, client_called_at(X,Y)).

+where_to[source(S)] <-
    ?go_to(X,Y);
    .send(S, tell, take_client_to(X,Y)).
