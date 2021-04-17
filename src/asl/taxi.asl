// Agent cashier in project naatho_ier

/* Initial beliefs and rules */

not_busy.

/* Initial goals */

/* Plans */

+!at(X,Y) : at(X,Y) <- true.

+!at(X,Y) : at(X0,Y0) <-
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

-!at(X,Y) <- !at(X,Y).

+client_called_at(C,X,Y)[source(S)] : not_busy <-
    ?at(X0,Y0);
    .send(S,tell,client_cost(math.abs(X-X0)+math.abs(Y-Y0),C));
    .abolish(client_called_at(C,X,Y)).

+client_called_at(C,X,Y)[source(S)] : busy <-
    .send(S,tell,client_cost(10000,C));
    .abolish(client_called_at(C,X,Y)).

+client_waiting_at(C,X,Y) : not_busy <-
    +busy;
    .abolish(not_busy);
    ?at(X0,Y0);
    if(Y0 < Y) {
        !at(X,Y-1);
    }elif(Y0 > Y) {
        !at(X,Y+1);
    }
    .send(C,tell,where_to);
    .abolish(client_waiting_at(C,X,Y)).

+client_waiting_at(C,X,Y)[source(S)] : busy <-
    .print("Rejecting ", C);
    .send(S,tell,reject(C,X,Y));
    .abolish(client_waiting_at(C,X,Y)).

+take_client_to(X,Y)[source(C)] <-
    .kill_agent(C);
    .print("Picked up ", C);
    !at(X,Y);
    .print(C, " arrived to destination!");
    .send("broker", tell, client_delivered(C));
    +not_busy;
    .abolish(busy);
    .abolish(take_client_to(X,Y)).