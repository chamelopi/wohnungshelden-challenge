# Must have requirements
- [x] Enable backend to manually create new applications via the UI (by user)
  - These fields can be set via the UI:
    - Email **(required)**
    - Salutation (optional)
    - FirstName **(required)**
    - LastName **(required)**
    - UserComment (optional)
  - Validation
    - Required fields
    - Valid email
  - The manual creation will set the CreationSource to MANUAL (see data model).
  - You can decide how the API will look like which the UI calls.
- [x] Enable backend to automatically create applications from portals via external service
  - These fields can be set via the external service:
    - Email **(required)**
    - Salutation (optional)
    - FirstName (optional)
    - LastName **(required)**
    - NumberOfPersons (optional)
    - WbsPresent (optional)
    - EarliestMoveInDate (optional)
    - Pets (optional)
    - ApplicantComment (optional)
  - Validation
    - Required fields
    - Valid email
  - The automatic creation will set the CreationSource to PORTAL.
  - You can decide how the API will look like which the other service calls.
  - Note that the other service is out-of-scope.
- [x] Created applications need to be saved in the database
  - The creationTimestamp must be set as the current timestamp.
  - When an application is created the status is set to CREATED.
  - Both creation use cases need to set the PropertyId (this is an additional input
    parameter for both the manual and portal cases).
- [x] Enable backend to filter created applications for displaying in the UI without pagination
  - The result must be sorted by “newest first based on creation timestamp”
  - Filter parameters (all except the PropertyId are optional from the UI, meaning that
    there must also be a way to display all applications for a Property)
    - Status (in the UI this is done via tab selection)
    - email
    - numberOfPersons
    - wbsPresent
    - PropertyId
  - Hint: The UI is always showing the applications for a specific
    property, see above. But the user doesn’t explicitly select this as a
    filter parameter, but instead the property is selected by navigating
    to a different page. That means the property needs to be
    considered in the filter.
  - You can decide how the API will look like which the UI calls.
- [x] Enable backend to retrieve the detailed representation of a single application by id.
  - The result contains all the fields of the application for displaying in the details
    view.
  - You can decide how the API will look like which the UI calls.
- [x] Unit test for at least one method that contains business logic.

# Nice to have requirements (bonus)

- [x] The filter feature should support pagination with a page size and page number which is
  given from the UI
- [x] Add more validations to “automatically create applications from portals”
  - EarliestMoveInDate must be in the future, if set
  - ApplicantComment must not be longer than 1000 characters
- [x] Enable backend to change the status of multiple applications to INVITED (invite use
  case)
- [x] Enable backend to change the status of multiple applications to DECLINED (decline use
  case)
- [ ] Set/Update the UserComment via separate endpoint
  - Add validation that input must not be longer than 1000 characters
