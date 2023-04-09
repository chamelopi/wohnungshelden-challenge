# Decisions

Here, I will document some of the technical decisions made in this project, along with possible alternatives and their
pros/cons.

## 1 Filtering approaches

### 1.1 Spring Criteria Builder

Criteria Builder is a Builder-Syntax for creating queries with the EntityManager.

[Example](https://www.baeldung.com/spring-data-criteria-queries#repository)

| pros                                   | cons                                                                            |
|----------------------------------------|---------------------------------------------------------------------------------|
| very fine grained control over queries | very verbose                                                                    |
|                                        | does not integrate directly with `Pageable`, would have to use e.g. limit query |
|                                        | does not scale well with the number of predicates                               |
|                                        | does not deal well with optional filters                                        |

### 1.2 JPA Specifications

Specifications is another mechanism for implementing filters in Spring Boot, built upon the CriteriaBuilder.
There is a `JpaSpecificationExecutor<T>` interface which can deal with them out of the box.

[Example](https://www.baeldung.com/spring-data-criteria-queries#specifications)

| pros                                         | cons                                                                                             |
|----------------------------------------------|--------------------------------------------------------------------------------------------------|
| less verbose than criteria builder           | does not deal well with optional filters                                                         |
| still very fine grained control if necessary | hard to unit-tests, since Specifications are Lambdas which need a CriteriaBuilder as an argument |
| out of the box repository support            |                                                                                                  |
| integrates with `Pageable`                   |                                                                                                  |

### 1.3 OData queries

[Example](https://www.baeldung.com/olingo)

| pros                                                                             | cons                                                                         |
|----------------------------------------------------------------------------------|------------------------------------------------------------------------------|
| standardized API, widespread library support                                     | documentation and libraries seem to be mostly outdated                       |
| supports filters, paging via top & skip, count, etc.                             | no direct spring boot integration, harder to integrate                       |
| out of the box support for startsWith, contains, between, and similar predicates | odata v2 is no longer recommended, but odata v4 lacks examples/documentation |

Due to the time constraints, lack of up-to-date documentation and examples, I decided against experimenting with OData for this project. 

### 1.4 Hand-written JPQL or SQL

While this might theoretically be an option, there are too many drawbacks for a simple CRUD application like this

| pros                        | cons                                           |
|-----------------------------|------------------------------------------------|
| complete control over query | hard to unit-test                              |
|                             | more error-prone                               |
|                             | harder to maintain                             |
|                             | does not scale well with the number of filters | 
|                             | optional filters cannot be handled easily      | 

## 2 API Design

### 2.1 Two separate DTOs for ApplicationCreation

The two DTOs `UiApplicationDto` and `PortalApplicationDto` could be implemented with a common base class
with contains the fields `email`, `salutation`, `firstName` (if validation is only done in the subclass), `lastName` and `propertyId`.

However, this would make them both harder to read and their differences (especially the optional `firstName` in `PortalApplicationDto`) more difficult to spot.

### 2.2 No DTO for filter endpoint

I made this decision primarily because I don't use a public ID + private ID pattern here, and therefore a DTO would be
identical with the entity class.

I decided against the public ID pattern because
- it was not a requirement
- public IDs help if e.g. the internal database ID could change during the entities lifecycle (unlikely)
- readability for humans is not a concern here (e.g. `application-23568923` vs. `12`)
- it increases complexity

In a real-world application however, the pattern could be used to hide database internals and increase readability.

