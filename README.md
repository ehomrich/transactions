# Transactions

A function that authorizes transactions for a specific account.

## Structure

This project mimics the architecture of [nubank/basic-microservice-example](https://github.com/nubank/basic-microservice-example), trying to implement something similar to the hexagonal architecture.

### Project anatomy

```
src 
 └ transactions                     → Application source code
    └ db                            → In-memory storage
       └ account.clj                → Account data structure and state methods
       └ transaction.clj            → Transaction history and state methods
    └ file                          → File input/stdin component
       └ schemas.clj                → JSON schemas and validator method
    └ controller.clj                → "Glue code" wiring all the other layers
    └ logic.clj                     → Business logic
    └ core.clj                      → Main application entry point
└ test                              → Test suites
└ project.clj
└ Dockerfile
```

#### db

Rudimentary in-memory storage components.

Account storage is simply an atom pointing to a map, and transaction history is an atom pointing to a simple vector. A few methods are provided to retrieve and change the value/properties of each structure value.

#### file

File input/stdin "port". Contains account and transaction schemas, and a predicate validator method.


#### logic

Business logic. Takes care of the transaction rules.
No side effects and no exceptions.

#### controller

Orchestrates the processing flow by wiring the layers.

Triggers schema validation, redirects input to proper execution methods, and formats output.

#### core

Application entry point. A simple loop that reads from stdin and prints to stdout.