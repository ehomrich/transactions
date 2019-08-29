# Transactions

A function that authorizes a transaction for a specific account
following a set of predefined rules.

## Usage

The program read operations as JSON lines from stdin, processing one at a time, in the order they are provided.

Example:

```bash
$ cat operations.txt
{ "account": { "activeCard": true, "availableLimit": 100 } }
{ "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }
{} # empty JSON

$ lein run < operations.txt

{"account":{"activeCard":true,"availableLimit":100},"violations":[]}
{"account":{"activeCard":true,"availableLimit":80},"violations":[]}
    # parsing errors result in empty lines
```

It can be run by interacting with the user, processing as the user enters operations, or redirecting file contents. So the calls below work the same way:

```bash
# User input
lein run

# File redirect
lein run < filename
```

The alias `start` is also available, to run the program with the `dev` profile.

To exit the program, enter Ctrl+C or Command+C (OS X).

> The state is discarded when the program exists.

### Operations

The program handles two kinds of operations:

- Account creation
- Transaction autorization

#### Account creation

To create an account, the program expects input with the following format (indented for better readability):

```json
{
    "account": {
        "activeCard": "boolean",
        "availableLimit": "number"
    }
}
```

This input defines the account state, with `availableLimit` and `activeCard`.

#### Transaction authorization

Expect input with the following format:

```json
{
    "transaction": {
        "merchant": "string",
        "amount": "number",
        "time": "ISO date-time string"
    }
}
```

The program will attempt to authorize a transaction of a certain `amount` for a particular `merchant` at a given `time`. 

### Output

For each operation, regardless of type, the output will be the account's current state and any **business logic violations**.

Example:

```json
{
  "account": { "activeCard": true, "availableLimit": 80 },
  "violations": ["insufficient-limit"]
}
```

### Business logic violations

For an operation to be authorized, it must pass some validations. Below the rules are described along with their violation code:

- `unknown-account`: Transactions can only be processed if there is an account.
- `illegal-account-reset`: Once created, accounts cannot be reset.
- `insufficient-limit`: The transaction amount should not exceed available limit.
- `card-blocked`: Transactions are denied when the card is blocked.
- `high-frequency-small-interval`: There should not be more than 3 transactions on a 2 minute interval.
- `doubled-transaction`: There should not be more than 2 similar transactions (same amount and merchant) in a 2 minutes interval.

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
