.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

============
Requirements
============

This document presents the abstract data model implemented by the Acumos Common Data Service.
The data model is explained in terms of entities in the system, attributes of the entities,
and relationships among the entities.  The details here are abstract in that they don't specify
data type, data length, table name, column name, etc.

Support for Federation
----------------------

The Acumos system is intended to be federated. This has implications for identifiers used in the system, because they will have to be usable globally:

* Multiple systems will be running in different organizations
* Information will be shared selectively across the systems
* A public "root" instance will be used to publish some information
* Users can publish their solutions for use by others.

Entity Overview
---------------

* Solution:

  - A downloadable, executable and shareable version of a trained statistical model

* Solution revision

  - A version of a solution with a user-assigned identifier

* Solution artifact

  - A data file or document associated with a solution revision

* User

  - Authorized user of the system

* User role

  - A role is used to grant access to user role functions

* User role function

  - A function is a specific activity supported by a system

* Social identity provider

  - Websites that are recognized by the system as identity providers

* User social identity provider information

  - The social-identity provider accounts held by the user and available for authentication purposes.

* Peer

  - External instances that communicate with this instance in a federated system

* Peer subscription

  - Selector and metadata for polling the remote federated instance for updates

* Peer group, peer group membership, peer group access, peer-peer mapping

  - Feature used to manage federated access for restricted solutions.  
    Fundamentally groups of peers are granted access to groups of solutions.

* Solution group, solution group membership, solution group access, peer-solution group mapping

  - Feature used to manage federated access for restricted solutions.
    Fundamentally groups of peers are granted access to groups of solutions.

* Solution download

  - The date and user ID when downloaded

* Solution rating

  - User opinion and feedback about a solution

* Solution tag

  - A keyword used to mark and search solutions

* Notification

  - A message to be shown to a user in the system

* Site configuration

  - Details for administration of the system, initially for the Portal/Marketplace web site

* Step result

  - Outcome of a task performed for a user; e.g., on-boarding or validating a model

* User Notification Preference

  - What delivery preference and message priority a user will choose for receiving a notification  


Entity Relationship Overview
----------------------------

* Solution

  - 1:many rel with solution revisions
  - many:1 rel with users (owner)
  - many:many with authorized users (sharing feature)

* Solution artifact

  - 1:many rel with solution revisions (artifact may be reused)
  - many:1 rel with users (owner)
  - 1:many rel with download
  - 1:many rel with rating
  - 1:many rel with tag

* Solution revision

  - many:1 rel with solution
  - 1:many rel with artifacts

* Trained statistical model

  - 1:1 rel with solution revision
  - many:1 rel with users (owner)

* Download

  - many:1 with solution

* Favorite

  - many:many with solution, user

* Rating/Review

  - many:1 rel with solution

* User

  - 1:many rel with most system entities: solutions, solution artifacts, solution revision, trained models, reviews, etc.
  - many:1 rel with organization

* User role

  - 1:many rel with user role functions

* User role function

  - many:1 rel with user role

* Peer

  - 1:many with user

* Peer subscription

  - Many:1 with peer

* Notification

  - Many:1 with user

* Site configuration

  - Many:1 with user

Entity and Attribute Details
----------------------------

All entities and attributes are listed below, grouped into three sections:

* Simple code-name entities (readonly pairs of values)
* Complex entities
* Relationship (mapping) entities


Enumerated Code-Name Sets
-------------------------

Access Type
^^^^^^^^^^^

| OR "Organization"
| PB "Public"
| PR "Private"

Artifact Type
^^^^^^^^^^^^^

| BP "BLUEPRINT FILE"
| CD "CDUMP FILE"
| DI "DOCKER IMAGE"
| DS "DATA SOURCE"
| MD "METADATA"
| MH "MODEL-H2O"
| MI "MODEL IMAGE"
| MR "MODEL-R"
| MS "MODEL-SCIKIT"
| MT "MODEL-TENSORFLOW"
| TE "TOSCA TEMPLATE"
| TG "TOSCA Generator Input File"
| TS "TOSCA SCHEMA"
| TT "TOSCA TRANSLATE"
| PJ "PROTOBUF FILE"

Deployment Status
^^^^^^^^^^^^^^^^^

| DP "Deployed"
| FA "Failed"
| IP "In Progress"
| ST "Started"

Social Login Provider
^^^^^^^^^^^^^^^^^^^^^

| FB "Facebook"
| GH "GitHub"
| GP "Google Plus"
| LI "LinkedIn"

Model Type
^^^^^^^^^^

| CL "Classification"
| DS "Data Sources"
| DT "Data Transformer"
| PR "Prediction"
| RG "Regression"

StepStatus
^^^^^^^^^^

| ST "Started"
| SU "Succeeded"
| FA "Failed"

StepType
^^^^^^^^

| OB "Onboarding"
| VL "Validation"

Toolkit Type
^^^^^^^^^^^^

| CP "Composite Solution"
| DS "Design Studio"
| H2 "H2O"
| RC "R"
| SK "Scikit-Learn"
| TF "TensorFlow"
| TC "Training Client"
| BR "Data Broker"

Validation Status
^^^^^^^^^^^^^^^^^

| FA "Failed"
| IP "In Progress"
| NV "Not Validated"
| PS "Passed"
| SB "Submitted"

Validation Type
^^^^^^^^^^^^^^^

| SS "Security Scan"
| LC "License Check"
| OQ "OSS Quantification"
| TA "Text Analysis"

Message Severity
^^^^^^^^^^^^^^^^

| HG "High"
| MD "Medium"
| LW "Low"

Entities
--------

The system entities are shown in alphabetical order.

Comment
^^^^^^^

This stores a user comment within a comment thread.

Attributes:

*    Comment ID
*    Thread ID
*    Parent ID (identifies the comment ID for which this comment is a reply; optional)
*    User ID
*    Text (the comment content)


Notification
^^^^^^^^^^^^

A notification is a message for a user about an event, for example that a solution previously downloaded has been updated.

Attributes:

*    Notification ID
*    Title (like an email subject)
*    Message (like an email body)
*    URL (a link)
*    Start (earliest date/time when the notification is active)
*    End (latest date/time when the notification is active)

Notifications are mapped to users in a many:many relationship.  That relationship must track which notifications have been viewed by the user.


Peer
^^^^

Registered and authorized external instances of the platform that communicate with this instance.  The registration is intended to be controlled by any user with admin roles.  This model is used to support the federated architecture.

Attributes:

*    Unique ID for peer
*    Site name
*    Subject name

     -  For an X.509 certificate

*    Site URL(s)

     -   How many interfaces will be required by federation?
     -   For now we are considering 2 types of urls: API url and web url.

*    Description
*    IsActive
*    IsSelf
*    Contacts (a pair, one as primary and another as backup)
*    Create timestamp
*    Modified timestamp


Peer Group
^^^^^^^^^^^

Defines a group that may be assigned to peers to facilitate access control. Only seen locally, not federated.

Attributes:

*    Group ID
*    Name
*    Description

     -   Additional textual information about this group


Role for Users
^^^^^^^^^^^^^^

Roles are named like "designer" or "administrator" and are used to assign privilege levels to users, in terms of the functions those users may perform; i.e., the system features they are authorized to use.

Attributes:

*    Unique ID
*    Name
*    Active (yes/no)


Role Function
^^^^^^^^^^^^^

A role function is a name for an action that may be performed by a user within a specific role, such as createModel. The software system may grant access to specific features based on whether the user role function is assigned to the user making a request. Role functions are related to roles in a many:mnany relationship.  So for example, a "designer" role may have many functions such as "read", "create", "update" and "delete" while an "operator" role may have only the function "read".

Attributes:

*    Unique ID
*    Role ID
*    Function name


Site Configuration
^^^^^^^^^^^^^^^^^^

This stores administrative details for management of the system.

Attributes:

*    Config key
*    Config value, which is required to be a JSON block
*    User ID, the last person who updated the entry; optional to allow creation of initial row without a user ID
*    Created date
*    Modified date


Solution
^^^^^^^^

* A solution is composed by a user in the Design Studio and generated by the system.
* A solution consists of a collection of solution revisions; which in turn consist of artifacts.
* May be generated by the system from an on-boarded trained statistical model.
* The primary element of the Catalog that is displayed to users
* Supports versioning - a solution may have many solution revisions

The metadata listed here describes the solution as a whole.

Attributes:

* Unique ID for system use
* Name:

  - Chosen by user. This name is not required to be unique

* Description

  - Free-text description of what the solution does

* Owner ID

  - The owner is the author of the solution, and is automatically assigned to the person who uploaded the machine-learning model artifact originally.

* List of authorized users

  - To facilitate review and collaborative work with a team

* Provider

  - Name of organization that sponsored and/or supports the solution

* Peer

  - ID of Acumos peer where the solution was first onboarded

* Toolkit (aka implementation technology) code

  - Underlying ML technology; e.g., Scikit, RCloud, Composite solution, and more TBD

* Model type code

  - Underlying ML category; valid values include CLASSIFICATION and PREDICTION

* Access type code

  - This refers to the visibility of the solution. It can be 'Private', 'Organization Shared' or 'Public'.

* Proposed attribute: System ID where created

  - Supports federation, exchange of solutions among peer systems

* Proposed attribute: collection of child solutions

  - Supports composite solutions

* Create time

  - The time when the solution was created; i.e., upload time

* Modification time

  - The time when the solution gets updated

* Version

  - Redundant; this is already covered by the child revision entities to a solution

* Referenced docker images

  - Redundant - the solution revision tracks artifacts.
  - Question: could this be used to prevent deletion of a docker image as long as a solution with that docker image exists?

* Usage statistics: number of views, number of downloads, number of ratings, average rating

  - These may be derived from other entities


Solution Artifact
^^^^^^^^^^^^^^^^^

* An artifact is a component of a solution revision.
* Example: a Docker image with one micro service that exposes one trained statistical model
* Example: a TOSCA model for deploying a solution revision
* Example: a trained statistical model
* The output of a machine-learning algorithm created by a data scientist using training data and on-boarded to the system; e.g., Python pickle or R binary object

Attributes:

*    The file image, treated as an opaque byte stream

     -  Very likely to be stored as a binary file in a Nexus repository, so the URL to the file can be stored as an attribute.

*    Unique ID for system use, a generated UUID to be globally unique

*    Type

     -   An artifact type can be either a statistical model, metadata, docker image or TOSCA file (and TBD).

*    Descriptive name

     -   Chosen by user. This name may not be unique.

*    URL

     -   Using this, the artifact image can be retrieved from a Nexus repository

*    Owner ID

     -    The person's ID who created the artifact and is the owner of it.

*    Create time

      -  Time when the artifact is created

*    Modification time

     -   The time when the artifact gets updated

*    Description

     -   Describes what the artifact does

*    Size

     -   Represents the size of the artifact in KB

Below are detailed descriptions of some artifact types:

Trained statistical model

A trained statistical model is the output of a machine-learning algorithm.  The model is an opaque byte array, probably stored as a binary file in a Nexus repository.

Docker Image

A docker image is generated by the system, containing a microservice which in turn makes the trained statistical model usable.
TOSCA Model

A TOSCA model is used to deploy a solution to a specific hosted environment; e.g., Rackspace. Multiple TOSCA models can be defined for each solution. TOSCA models may be shared with other users.


Solution Deployment
^^^^^^^^^^^^^^^^^^^

This captures information about deployment of a specific revision of a solution to a target environment.

Attributes:

*    Deployment ID - generated
*    Solution ID - required
*    Revision ID - required
*    User ID - required
*    Target deployment environment
*    Deployment status (in progress, deployed, failed, etc.)



Solution Group
^^^^^^^^^^^^^^

Defines a group that may be assigned to solutions to facilitate access control. Only seen locally, not federated.

Attributes:

*    Group ID
*    Name
*    Description

     - Additional textual information about this group
     

Solution Revision
^^^^^^^^^^^^^^^^^

* Captures all the revisions of any solution as it goes through updates.
* Represents a collection of artifacts that implement the solution
* E.g., revision "1.0-alpha" is a consistent set of artifacts

A solution revision consists of a collection of solution artifacts. The metadata listed here describes the collection.

Attributes:

*    Unique Revision ID

     -  A globally unique ID for this specific revision

*    Solution ID

     -   Represents the solution, allows multiple revisions per solution

*    Version

     -   Chosen by the user. This serves as the solution's child revision entry identifier. This needs to be unique for any solution revision within the same solution.

*    Create time

     -   The time when this revision of the solution is created

*    Status

     -   Denotes if the solution is active or not

*    Creator

     -   The person who created the revision of the solution (reference to the user table)


Solution Validation Sequence
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This represents the steps to be performed in solution validation.  For example, in some environments a peer review may be required, and in other environments an automated scanner may be used.

Attributes:

*    Sequence; i.e., ordering of tasks
*    Validation task type


Step Result
^^^^^^^^^^^

This tracks the status of steps in the Acumos system by some actor or process. For example, the on-boarding feature can store information about the status and outcome of its steps.

Attributes:

*    Step Result ID - generated
*    Tracking ID - optional

     -  This represents a workflow execution instance. For example it may represent onboarding of a ML model workflow instance.

*    Step type Code - required

     -   Represents the type of workflow being tracked- for example whether it is onboarding of ML model workflow, validation of a ML model workflow or something else. Currently onboarding and validation are the two types of workflows being identified, but this list will grow as the need for tracking additional workflows arise.

*    Solution ID - optional
*    Revision ID - optional
*    Artifact ID - optional
*    User ID - optional
*    Name - required

     -   Represents the specific step involved in the workflow. For example for onboarding workflow, step name can "Soultion ID creation"

*    Status Code - required

     -   Represents the state at which the workflow step is currently in. Currently "started", "succeeded" and "failed" are the three step states which are tracked.

*    Result - optional

     -    Text information for a workflow step progress, for debugging purposes.

*    Start Date - required

     -   Date/time when a step starts

*    End Date - optional

     -   Date/time when a step ends


User Notification Preference
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This stores the delivery mechanism and message priority preferences by the user for receiving notifications

Attributes: 

*    User ID (notification recipient) 
*    Notification type (email/text/web)
*    Message Severity (low/medium/high)  


Tag for Solution
^^^^^^^^^^^^^^^^

Keywords applied to solutions. Attributes:

*    Tag name

Mapped many:many to solutions.


Thread
^^^^^^

This stores the general topic of discussion to which a comment is associated

Attributes:

*    Thread ID
*    Thread Title (optional)
*    Solution ID
*    Revision ID


User
^^^^

* Authorized users of the system must be recognized and authenticated.
* May be authenticated using a social identity provider; e.g., LinkedIn

Attributes:

*    Unique ID for system use
*    User's organization name
*    Login name
*    Login password
*    Password expiration date/time
*    First, middle, last names
*    Email address(es)
*    Phone number(s)
*    Profile picture (subject to some size limit)
*    Authentication mechanism

     -   We have discussed Facebook, Github, Linkedin

*    Authentication token

     -   For example, JSON Web Token, which should be short (hundreds of bytes) but may be large (thousand of bytes). This will be used to Secure APIs after logging in.

*    Levels of access

     -   For example, users might be modelers (data scientists) who upload models; integrators who build solutions in the design studio; or consumers who download and run solutions only.
     -   As one possible implementation, the EP-SDK represents privileges using roles and role functions.  A user is assigned one or more roles.  Each role is associated with one or more functions.  A function is a specific feature in the system. Still TBD if an external authentication system will deliver privileges like roles, or if all must be stored locally.

Users are related to user roles in a 1:many relationship; in other words, multiple roles may be assigned to a single user.


User Social Login Provider Account
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Describes the details of a user's account at a social identity provider.  One user may use multiple login providers; e.g., Facebook, Google, LinkedIn, Github; further a user may use multiple accounts with a single provider.

Attributes:

*    User ID
*    Login provider code
*    User's login name at the provider
*    Rank (which provider to prefer)
*    Display name
*    Profile URL
*    Image URL
*    Secret
*    Access token
*    Refresh token
*    Expiration time


Entity Mapping Relationships
----------------------------

This section documents the relationships among entities that are managed in separate mapping tables.  The extra tables allow many-many relationships using entity ID values. These standalone relationship tables do not define new entities, but may store information about the relationship, such as the time when it was created.

Please note this section does not document simple relationships managed within entities, which includes one-to-one and many-to-one relationships.  For example, every comment has the ID of the containing thread, so a separate table is not required to manage that relationship.


Relationship Solution - Revision
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures the relationship of a revision within a solution.

Attributes:

*    Revision ID
*    Solution ID
*    Version name (user-assigned string)
*    Description
*    Owner (User ID)


Relationship Revision - Artifact
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures the relationship of an artifact within a revision.

Attributes:

*    Revision ID
*    Artifact ID


Relationship Solution - Solution for Composite Solutions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures a parent-child relationship of a composite solution; i.e., a solution that reuses other solutions.

Attributes:

*    Parent solution ID
*    Child solution ID


Relationship Solution - Revision - Task for Validation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This relationship stores details of validating a solution revision against specific criteria such as a license check.

Attributes:

*    Solution ID
*    Revision ID
*    Task ID (validation job identifier)
*    Validation type
*    Validation status (pass, fail, ..)
*    Details of validation results


Relationship Solution - Tag
^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures the assignment of tags to solutions.

Attributes:

*    Solution ID
*    Tag value


Relationship Solution - User for Access
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This represents an access grant on a solution for a specific user. For example, a solution may be shared by a solution creator with a reviewer.

Attributes:

*    Solution ID
*    User ID


Relationship Solution - Artifact - User for Download
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures a download of a solution artifact by a user.

Attributes:

*    Solution ID
*    Artifact ID
*    User ID
*    Download date and time

Descriptive statistics are derived from individual records; for example total number of downloads and last download time. The statistics must be cached and updated on changes to reduce the time needed to fetch information.  For example, update the cached number of downloads and last-download time each time an artifact is downloaded.


Relationship Solution - User for Favorite
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures an action by a user to specify that a solution is a favorite

Attributes:

*    Solution ID
*    User ID


Relationship Solution - User for Rating
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures a rating, text review and other feedback contributed by users about a solution. In keeping with other application stores, the rating is modeled at the solution level (not revision).

Attributes:

*    Solution ID
*    User ID

     -  Identifier of the user who rated that solution through the web user interface.

*    Rating

     -  A numerical rating scale, for example 1-5

*    Text of review
*    Create time

     -   The time when the solution rating was created by the user

*    Modification time

     -   The time when the rating gets updated

Descriptive statistics are derived from individual solution ratings; for example average rating. The statistics may be cached and updated on change to reduce the time needed to fetch information about a solution. For example, update the cached number of reviews and average rating each time a solution is reviewed.


Relationship User - Role
^^^^^^^^^^^^^^^^^^^^^^^^

This captures the assignment of a role to a user.

Attributes:

*    User ID
*    Role ID


Relationship Peer Subscription
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Describes which solution(s) available on a remote peer should be tracked and/or replicated.

Attributes:

*    Subscription ID
*    Peer ID
*    Selector

     - What solutions should be selected

*    Refresh interval

     -  How often to poll the remote system

*    Create timestamp
*    Modified timestamp


Relationship Notification - User
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This captures the relationship between a notification and a user; i.e., specifies which users should see which notifications.

Attributes:

*    Notification ID
*    User ID
*    Viewed date and time


Relationship Peer - Peer Group for Membership
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Represents the membership of peers in a peer access group.

Attributes:

*   Peer Group ID
*   Peer ID
*   Create timestamp


Relationship Solution - Solution Group for Membership
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Represents the membership of solutions in a solution access group.

Attributes:

*    Solution Group ID
*    Solution ID
*    Create timestamp


Relationship Solution Group - Peer Group for Access
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Represents granting of access to all solutions in the solution group by peers in the peer group.

Attributes:

*   Solution Group ID
*   Peer Group ID
*   Active flag (yes/no)
*   Create timestamp


Relationship Peer Group - Peer Group for Access
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Represents granting of access to resource peers for principal peers.  

Attributes:

*   Principal peer group ID
*   Resource peer group ID
*   Create timestamp


Required Operations
-------------------

This section lists the required operations that shall be supported by the Common Data Micro Service. The list serves as a requirements document for both the client and server, in support of the entities and attributes identified above.

Metadata operations
^^^^^^^^^^^^^^^^^^^

These read-only actions provide access to value sets that may change over time:

*  Get access types
*  Get artifact types
*  Get login providers
*  Get model types
*  Get toolkit types
*  Get validation status values

CRUD operations
^^^^^^^^^^^^^^^

To keep the rest of this document brief, the standard "CRUD" operation definitions are repeated here:

*    (C)reate an entity; a REST POST operation that requires new content. If the entity ID field is not supplied, this operation generates a unique ID; otherwise the supplied ID is used.
*    (R)etrieve an enity; a REST GET operation that requires the entity ID
*    (U)pdate an entity; a REST PUT operation that requires the entity ID and the new content
*    (D)elete an entity; a REST DELETE operation that requires the entity ID

Operations on artifacts
^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

*    Get a page of artifacts from the complete set, optionally sorted on one or more attributes
*    Get a page of artifacts using partial ("like") value match on the name and description attributes, optionally sorted on one or more attributes
*    Search for artifacts using exact value match on one or more attributes, either all (conjunction-and) or one (disjunction-or)
*    Get all the artifacts for a particular solution revision
*    Add an artifact to a solution revision
*    Delete an artifact from a solution revision.

Operations on solutions
^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get a page of solutions from the complete set, optionally sorted on one or more attributes
* Get a page of solutions using partial ("like") value match on the name and description attributes, optionally sorted on one or more attributes
* Search for solutions using exact value match on one or more attributes, either all (conjunction-and) or one (disjunction-or)
* Get a page of solutions that use a specified toolkit type
* Tags

  - Get all tags assigned to a solution
  - Add a tag to a solution
  - Drop a tag from a solution
  - Get a page of solutions that have a specified tag

*  Authorized users

   - Get all authorized users assigned to a solution
   - Add a user to a solution
   - Drop a user from a solution

Operations on solution revisions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get all revisions for a specific solution
* Get all revisions for multiple solutions
* Get a solution revision for a particular solution id and revision id.
* Get all the solution revisions for a particular artifact.

(Also see operations on artifacts, which are associated with solution revisions)

Operations on solution downloads
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* Standard CRUD operations plus the following:
* Get all downloads for a specific solution
* Get the count of downloads for a specific solution

Operations on solution ratings
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

*  Get all ratings for a specific solution
*  Get the average rating for a specific solution

Operations on tags
^^^^^^^^^^^^^^^^^^

Standard CRUD operations apply.

Operations on users
^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get a page of users from the complete set, optionally sorted on one or more attributes
* Get a page of users using partial ("like") value match on the first, middle, last or login name attributes, optionally sorted on one or more attributes
* Search for users using exact value match on one or more attributes, either all (conjunction-and) or one (disjunction-or)
* Check user credentials - the login operation. Match login name/email address as user, password as password. Returns user object if found; signals bad request if no match is found.

Operations on user login providers
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get all login providers for the specified user

Operations on roles
^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get all roles for the specified user
* Search for roles using exact value match on one or more attributes

Operations on role functions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get all role functions for the specified role

Operations on peers
^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get a page of peers from the complete set, optionally sorted on one or more attributes
* Search for peers using exact value match on one or more attributes

Operations on peer subscriptions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Get a page of peer subscriptions from the complete set, optionally sorted on one or more attributes

Operations on notifications
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations plus the following:

* Add a user as a notification recipient
* Update that a user has viewed a notification
* Drop a user as a notification recipient
* Get all notifications for a user

Operations on workflow step result
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations apply.

Operations on workflow step type
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations apply.

Operations on workflow step status
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Standard CRUD operations apply.
