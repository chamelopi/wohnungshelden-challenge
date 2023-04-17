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

As can be seen in the codebase, I decided for this approach and built a generic conversion layer above it, which translates
the query parameters into specifications and handles all the necessary checks for optional filters, allowing for a *more
concise controller implementation* and *making it easier to add more filter parameteres* in the future.

#### Drawback of the generic `FilterSpecifications` class

My current generic implementation does have a drawback however - it does not handle any type of checks besides *equality*. That could
be a drawback if you wanted to, for example, filter for a range of `earliestMoveInDate`s (a possible use case).

If more specific cases are to be added, this generic solution will not work in the current form and should be replaced by either
a more broadly applicable generic solution (which could be hard to implement, due to the necessary type conversion between the `String` values
in the query map and the actual fields of the entity) or specific chain of specifications as shown in the linked example above, e.g like this:

```java
repo.findAll(where(hasEmail(queryMap.get("email")))
        .and(isWbsPresent(queryMap.get("wbsPresent")))
        .and(isMoveInDateBetween(queryMap.get("earliestMoveInDate", "latestMoveInDate"))),
        pageable);
```

This will make the code more verbose, less generic and needs to account for optional filters, too.

### 1.3 OData queries

[Example](https://www.baeldung.com/olingo)

| pros                                                                             | cons                                                                         |
|----------------------------------------------------------------------------------|------------------------------------------------------------------------------|
| standardized API, widespread library support                                     | documentation and libraries seem to be mostly outdated                       |
| supports filters, paging via top & skip, count, etc.                             | no direct spring boot integration, harder to integrate                       |
| out of the box support for startsWith, contains, between, and similar predicates | odata v2 is no longer recommended, but odata v4 lacks examples/documentation |

Due to the time constraints, lack of up-to-date documentation and examples, I decided against experimenting with OData
for this project.

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
with contains the fields `email`, `salutation`, `firstName` (if validation is only done in the subclass), `lastName`
and `propertyId`.

However, this would make them both harder to read and their differences (especially the optional `firstName`
in `PortalApplicationDto`) more difficult to spot.

### 2.2 No DTO for filter endpoint

I made this decision primarily because I don't use a public ID + private ID pattern here, and therefore a DTO would be
identical with the entity class.

I decided against the public ID pattern because

- it was not a requirement
- public IDs help if e.g. the internal database ID could change during the entities lifecycle (unlikely)
- readability for humans is not a concern here (e.g. `application-23568923` vs. `12`)
- it increases complexity

In a real-world application however, the pattern could be used to hide database internals and increase readability.

### 2.3 Update DTO

I designed the update endpoint for status updates and userComment updates as a single endpoint, with one common DTO
containing both fields. This has several advantages:

- scales well if more fields should be updatable
- only one REST call from frontend when multiple fields are updated simultaneously
- can define validations in one central place

There is one drawback with the current solution however:

- manual null checks are necessary for every single field

There might be a way to merge the non-null fields with the existing PropertyApplication entity, but I didn't explore
this possibility.
