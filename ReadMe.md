#Deependee

##Language philosophy
- everything is a dependency
  - fields depend on computations
  - functions depend on implementation
  - dependency graphs (traces) are always visible
- dependencies can be existing or latent
- arrays and objects are streamed by default
- no invalid states
  - no null values
  - no exceptions

##Platform philosophy
- cloud-ready
- code/config based deployment and integration
- no logging
- observable
- versioned and time series
- automated input validation

##Environment philosophy
- data storage is cheap
- networks are fast but
  - need to consider geolocation
  - network distribution needs to be obvious
- machines can make better decisions
  - heuristics, coded best practices, algorithms
  - machine learning
- small compute is cheap and ubiquitous
  - high threading counts
  - lambdas
  - on demand compute
- as a result of these, with time
  - performance increases instead of decreasing
  - TCO decreases instead of increasing

##Testing
- when a constraint is defined before assigning a dependency, the assignment is checked against the constraint
- when a constraint is defined after assigning a dependency, the constraint is checked against the value
- tests can probably be executed both ways, but the standard way is to define constraints after dependencies

##Design notes
- Parser -> automatic instantiation
- Registry -> multiple implementations possible, in particular for connecting to other registries
- Deployer -> target dependent
- Executor -> multiple strategies, each with their own implementation
  - resolves dependencies based on strategies
    - local direct call
    - local thread pipe
    - remote simple call
    - remote call with retry/circuit breaker
    - remote call with throttling
  - communicates with other executors to resolve upstream and downstream dependencies
  - highly multithreaded
  - recalc: https://lord.io/blog/2020/spreadsheets/