// Agent distributer in project naatho_ier

/* Initial beliefs and rules */

/* Initial goals */


/* Plans */

+client_called_at(X,Y)[source(C)] <-
	.broadcast(tell, client_called_at(C,X,Y)).
	
+client_cost(_,C) : taxi_num(TN) & .count(client_cost(_,C)[source(_)], N) & N==TN <-
	.findall(offer(CC,T),client_cost(CC,C)[source(T)],L);
	.min(L, offer(WCC, WT));
	if (WCC < 10000) {
        ?client_called_at(X,Y)[source(C)];
        .send(WT, tell, client_waiting_at(C,X,Y));
        .print("Winner of client ", C, " is ", WT);
        .abolish(client_cost(_,C));
 	}.
    // TODO else

+reject(C,X,Y) <-
    .broadcast(tell, client_called_at(C,X,Y));
    .abolish(reject(C,X,Y)).

+client_delivered(C) <-
    .abolish(client_called_at(X,Y));
    .abolish(client_delivered(C)).