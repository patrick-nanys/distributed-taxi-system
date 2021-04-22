// Agent taxi in project distributed_taxi_system

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
    //.print("not busy client called: ", C);
    ?at(X0,Y0);
    .send(S,tell,client_cost(math.abs(X-X0)+math.abs(Y-Y0),C));
    .abolish(client_called_at(C,X,Y)).

+client_called_at(C,X,Y)[source(S)] : busy <-
    //.print("busy client called: ", C);
    .send(S,tell,client_cost(10000,C));
    .abolish(client_called_at(C,X,Y)).

+client_waiting_at(C,X,Y) : not_busy <-
    +busy;
    .abolish(not_busy);
    //.print("client waiting at ", X, " ", Y, " - ", C);
    ?at(X0,Y0);
    if(Y0 < Y) {
        !at(X,Y-1);
    }elif(Y0 > Y) {
        !at(X,Y+1);
    }
    .abolish(client_waiting_at(C,X,Y));
    .send("broker", tell, client_picked_up(C)).

+client_waiting_at(C,X,Y)[source(S)] : busy <-
    //.print("Rejecting ", C);
    .send(S,tell,reject(C,X,Y));
    .abolish(client_waiting_at(C,X,Y)).

+pickup_ack(C) <-
    //.print("pickup ack");
    .abolish(pickup_ack(C));
    .send(C,tell,where_to).

+take_client_to(X,Y)[source(C)] <-
    //.kill_agent(C);
    //.send("broker", tell, client_picked_up(C));
    //.wait(1000);
    //.wait(1000);
    //.print("Take client to (", C, ")");
    remove(C);
    //.print("Picked up ", C);
    !at(X,Y);
    //.print(C, " arrived to destination!");
    +not_busy;
    .abolish(busy);
    .abolish(take_client_to(X,Y)).