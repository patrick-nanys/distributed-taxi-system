// Agent broker in project distributed_taxi_system

/* Initial beliefs and rules */

/* Initial goals */


/* Plans */

+client_called_at(X,Y)[source(C)] <-
    //.print("Broadcasting for ", C);
	.broadcast(tell, client_called_at(C,X,Y)).

//+client_cost(_,C) : taxi_num(TN) & .count(client_cost(_,C)[source(_)], N) & N<TN <-
    //.print("Auction for ", C, " (not enough ", N, ")").

+client_cost(_,C) : taxi_num(TN) & .count(client_cost(_,C)[source(_)], N) & N==TN <-
    //.print("Auction for ", C);
	.findall(offer(CC,T),client_cost(CC,C)[source(T)],L);
	.min(L, offer(WCC, WT));
	if (WCC < 10000) {
        ?client_called_at(X,Y)[source(C)];
        .send(WT, tell, client_waiting_at(C,X,Y));
        //.print("Winner of client ", C, " is ", WT);
        .abolish(client_cost(_,C));
 	} else {
 	    //.print("Sending call again to ", C);
 	    .send(C, tell, call_again);
 	    .abolish(client_cost(_,C));
 	    .abolish(client_called_at(_,_)[source(C)]);
 	}.

+reject(C,X,Y) <-
    //.print(C, " got rejected");
    .broadcast(tell, client_called_at(C,X,Y));
    .abolish(reject(C,X,Y)).

+client_picked_up(C)[source(S)] <-
    //.print(C, "picked up");
    .abolish(client_called_at(X,Y)[source(C)]);
    .abolish(client_delivered(C));
    .abolish(client_picked_up(C));
    .send(S, tell, pickup_ack(C)).