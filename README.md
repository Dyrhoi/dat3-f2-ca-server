[![Build Status](https://travis-ci.org/dat3startcode/dat3-startcode.svg?branch=master)](https://travis-ci.org/dat3startcode/dat3-startcode)

# NextLevel CA 2 Project
*This is NextLevel's CA 2 project for 3rd semester on CPH-Business Acadamy*

## REST API Documentation
*This is our REST API Documentation for our CA2 project*

| Method    | URL                                   | Request Body (JSON)   | Response (JSON)                       | Error         |
|---        |---                                    |---                    |---                                    |---            |
| GET       | /api/users                            |                       | User {id} (1)                         |               |
| GET       | /api/users{id}                        |                       | [User, User, ...] (1)                 | (e1)          |
| GET       | /api/users/hobby/{hobby}              |                       | [User, User, ...] (1)                 | (e1)          |
| GET       | /api/postalcodes                      |                       | [Postal code, Postal code, ...] (2)   | (e1)          |
| GET       | /api/users/postalcode/{postalcode}    |                       | [User, User, ...] (1)                 | (e1)          |
| POST      | /api/users                            | User(1) without ID    |                                       | (e2)          |
| PUT       | /api/users/{id}                       | User(1) with ID       |                                       | (e1) & (e2)   |

### Request Body and Respons Formats
1. User format (dont provide ID, for POST)
```javascript
{
    "Id" : Number,
    "FirstName" : String,
    "LastName" : String,
    "Phone" : Array (Phone),
    "Email" : String (Email),
    "Hobbies" : Array (Hobby)
}
```
2. Postal code format
```javascript
{
    "PostalCode" : Number,
    "City", String
}
```

### Errors
(e) All errors are reported using this format (with the HTTP-Status code matching the number)

{ status : statusCode, "msg" : "Explains problem" }

* (e1) : { status : 404, "msg" : "No content found for this request" }
* (e2) : { status : 400, "msg" : "Field 'xxx' is required" } (For example, no first or last name provided, not a valid email)
