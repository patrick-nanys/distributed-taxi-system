// Agent distributer in project naatho_ier

/* Initial beliefs and rules */

/* Initial goals */


/* Plans */

+client_called_at(X,Y)[source(S)] <-
	.broadcast(tell, client_called_at(X,Y));
	+searching_for_taxi(S).
	
+client_cost(_) : taxi_num(TN) & .count(client_cost(_)[source(_)], N) & N==TN <-
	.findall(offer(CC,T),client_cost(CC)[source(T)],L);
	.min(L, offer(WCC, WT));
	if (WCC < 10000) {
	    ?searching_for_taxi(S);
	    .send(WT, tell, client_won(S));
        ?cashier_at(WA,X,Y);
        .send(S, tell, cashier_at(WA,X,Y));
        .abolish(searching_cashier(_));
        .abolish(custcount(_)).
	}


