[![Build Status](https://travis-ci.org/dat3startcode/dat3-startcode.svg?branch=master)](https://travis-ci.org/dat3startcode/dat3-startcode)

# NextLevel CA 2 Project
*This is NextLevel's CA 2 project for 3rd semester on CPH-Business Acadamy*

## REST API Documentation
*This is our REST API Documentation for our CA2 project*

| Method    | URL                                    | Request Body (JSON)     | Response (JSON)                       | Error         |
|---        |---                                     |---                      |---                                    |---            |
| GET       | /api/people                            |                         | Person (1)                            |               |
| GET       | /api/people/{id}                       |                         | PersonArray (1.1)                     | (e1)          |
| GET       | /api/people/hobby/{hobby}              |                         | PersonArray (1.1)                     | (e1)          |
| GET       | /api/people/postalcode/{postalcode}    |                         | PersonArray (1.1)                     | (e1)          |
| POST      | /api/people                            | Person(1)               | CREATED Person (1)                    | (e2)          |
| PUT       | /api/people/{id}                       | Person(1)               | UPDATED Person (1)                    | (e1) & (e2)   |
| DELETE    | /api/people/{id}                       |                         | DELETED Person (1)                    | (e1) & (e2)   |
| GET       | /api/postalcodes                       |                         | [PostalCode, PostalCode, ...] (2)     | (e1)          |

### Request Body and Respons Formats
#### Person
1. Person format (dont provide ID, for POST)
```javascript
{
    "id" : Number,
    "firstname": String,
    "lastname": String,
    "address": Address (1.2),
    "phone": Array (Phone (1.3)),
    "email": String,
    "hobbies": Array (Hobby (1.4))
}
```

1.1. Address
```javascript
{
    "data": Array (User (1))
}
```

1.2. Address
```javascript
{
    "street": String,
    "postalcode": String,
    "city": String
}
```

1.3. Phone
```javascript
{
    "number": Number,
    "description": String
}
```

1.4. Hobby
```javascript
{
    "name": String,
    "category": String,
    "type": String
}
```

Example Person (1) Response
```javascript
{
    "id" : 1,
    "firstname": "John",
    "lastname": "Smith",
    "address": {
        "street": "Roskildevej 2.A",
        "postalcode": "4000",
        "city": "Roskilde"
    },
    "phone": [
        {
            "number": 10102020,
            "description": "Work phone"
        },
        {
            "number": 10102030,
            "description": "Private phone"
        }
    ],
    "email": "johnsmith@test.dk",
    "hobbies": [
        {
            "name": "Flag fodbold",
            "category": "Generel",
            "type": "Udendørs"
        }
    ]
}
```

#### Postal Code

2. PostalCode
```javascript
{
    "postalcode": Number
    "city": String
}
```

### Errors
(e) All errors are reported using this format (with the HTTP-Status code matching the number)

{ status : statusCode, "msg" : "Explains problem" }

* (e1) : { status : 404, "msg" : "No content found for this request" }
* (e2) : { status : 400, "msg" : "Field 'xxx' is required" } (For example, no first or last name provided, not a valid email)
