# Transactions

A function that authorizes a transaction for a specific account
following a set of predefined rules.

## Usage



## Building

### Uberjar

Build an executable:

```bash
lein uberjar
```

The application is processed and produces a standalone jar. Outputs to `/target`.

Execute it with a simple:

```bash
java -jar path/to/standalone.jar
```

### Dockerized build

Build the image:

```bash
docker build -t <name> .
```

A single standalone executable jar file is generated and defined as `ENTRYPOINT`.

Run:

```bash
# User input
docker run -i <name>

# File redirect
docker run -i <name> < filename
```

## Tests

This project uses the [Midje](https://github.com/marick/Midje) test framework.

### Running tests

```bash
lein midje
```

### Coverage

[cloverage](https://github.com/cloverage/cloverage) is used to measure code coverage. To collect execution data, run:

```bash
lein coverage
```

This command uses the `midje` runner and outputs results as HTML reports to the `/coverage` directory, at the root of the project.


## Project anatomy

The directory structure mimics [nubank/basic-microservice-example](https://github.com/nubank/basic-microservice-example), trying to implement something similar to the hexagonal architecture.

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
└ test/                             → Test suites
└ project.clj
└ Dockerfile
```

#### db

Rudimentary in-memory storage components.

Account and transaction storages are simple atoms, the former points to a map, the latter to a vector. A few methods are provided to retrieve and change the value or properties of each data structure.

#### file

File input/stdin "port". Contains account and transaction schemas, and a predicate validator.

#### logic

Business logic. Takes care of transaction validation rules.
No side effects and no exceptions.

#### controller

Orchestrates the processing flow by wiring the layers.

Parses input, triggers schema validation, redirects to proper execution methods, and formats output.

#### core

Application entry point. A loop that reads from stdin, triggers the controller and prints the results to stdout.
