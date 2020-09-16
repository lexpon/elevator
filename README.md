# Coding Challenge

Elevator control system for the company Wonderland.

## Problem Specification

Design and implement an elevator control system for the company Wonderland.
What data structures, interfaces and algorithms will you need?
Your elevator control system should be able to handle multiple elevators up to 16.

You can use the language of your choice to implement an elevator control system.
In the end, your control system should provide an interface for:

- Querying the state of the elevators (what floor are they on and where they are going)
- Receiving an update about the status of an elevator
- Receiving a pickup request

Time-stepping the simulation should be possible.

## Assessment Criteria

We expect that your code is well-factored, without needless
duplication, follow good practices and be automatically verified.

What we will look at:
- How clean is your design and implementation, how easy it is to
understand and maintain your code
- How you verified your software, if by automated tests or some
other way

## My solution

I've chosen Java as programming language and Spring Boot as framework to build
the application. The application can be run via command line and the
interaction with it is done via REST. 

To keep it simple, I've decided to have maximum 10 floors and maximum 10 open
pickup requests per elevator.

### Running the application

To be able to run the application JDK 11 needs to be installed. Then it can be
run by executing the following command:

```bash
mvn spring-boot:run
```

### REST commands

#### Get elevator status

Returns all elevators with its current floors, directions, open pickup requests
and pickup requests in progress. 

```curl
curl --request GET 'localhost:8080/api/v1/elevator'
```

#### Sending a pickup request

Sends a pickup request. The request is assigned to an elevator if there is an
elevator available. If no elevator is available, the pickup request will remain
open until there is an elevator available. The maximum number of open
unassigned pickup requests is `<number of elevators> * 10.`

```curl
curl --request POST 'localhost:8080/api/v1/elevator/pickup' \
--header 'Content-Type: application/json' \
--data-raw '{
    "currentFloor": 8,
    "destinationFloor": 1
}'
```


#### Performing time steps

To perform one time step in the simulation it is necessary to trigger the
corresponding endpoint. It's possible to trigger several time steps within
one single request.

The parameter `numberOfSteps` is optional. If there is no such parameter one
time step will be performed.

```curl
curl --request POST 'localhost:8080/api/v1/elevator/step?numberOfSteps=10'
```

### Pros and Cons

- The maximum limit for pickup requests is a random number to have such a
  limit. 
- There is no instance that holds information about assigned requests. If one
  elevator would loose the information about a request, the request would be
  gone.
- Distributing the pickup requests is basic and needs improvement.
  - E.g.: Elevator is at floor 1 and has an assigned request "from 8 to 4" and
    the elevator is moving up. When another request comes in "from 8 to 5" one
    would think that it will get assigned to the same elevator. But this is
    not the case yet because the logic in `PickupRequestRating` is not good
    enough for it.
- ... let's talk about it :-) ...