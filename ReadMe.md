#Deependee

##Language philosophy
- everything is a dependency
- fields depend on computations
- functions depend on implementation
- dependencies can be existing or latent 

##Platform philosophy
- cloud-ready
- code/config based deployment and integration
- no logging
- observable
- versioned

##Testing
- when a constraint is defined before assigning a dependency, the assignment is checked against the constraint
- when a constraint is defined after assigning a dependency, the constraint is checked against the value
- tests can probably be executed both ways, but the standard way is to define constraints after dependencies