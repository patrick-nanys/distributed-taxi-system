// Agent client in project distributed_taxi_system

/* Initial beliefs and rules */

// go_to(X,Y).

/* Initial goals */

!call_for_taxi.

/* Plans */

+setup <-
    .print("Setting up client");
    !call_for_taxi;
    .abolish(setup).

+!call_for_taxi <-
    .print("Calling for taxi");
    ?at(X,Y);
    .send("broker", tell, client_called_at(X,Y)).

+call_again <-
    .print("Waiting");
    .wait(1000);
    .abolish(call_again);
    !call_for_taxi.

+where_to[source(S)] <-
    ?go_to(X,Y);
    .send(S, tell, take_client_to(X,Y)).
