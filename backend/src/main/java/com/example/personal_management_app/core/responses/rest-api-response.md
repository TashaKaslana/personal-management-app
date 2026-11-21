# REST API response package

| Class | Role |
| --- | --- |
| [RestApiResponse](RestApiResponse.java) | Builds the HTTP response and wraps its payload or error details. |
| [ApiErrorResponse](ApiErrorResponse.java) | Describes a failure in the response's `error` field. |
| [ResponseMetadata](ResponseMetadata.java) | Holds pagination details and optional named links in `metadata`. |
| [PaginationInfo](PaginationInfo.java) | Describes the current page and the full result set in `metadata.pagination`. |

Controllers and exception handlers can return the result of a static factory on [RestApiResponse](RestApiResponse.java) directly. Each factory sets the HTTP status on a `ResponseEntity`. When present, its body contains `success`, a `timestamp`, and a `message`. The payload goes in `data`, pagination details in `metadata`, and failure details in `error`. Null fields are omitted from JSON.

## Success methods

| Factory | HTTP status | Response body |
| --- | --- | --- |
| `success(data)`, `success(data, message)` | 200 OK | Payload with a message that defaults to `SUCCESS`. |
| `success(page)`, `success(page, message)` | 200 OK | For a Spring Data `Page<E>`, returns the current page's elements as `data` and adds pagination metadata. The message defaults to `SUCCESS`. |
| `success()`, `success(message)` | 200 OK | Message only, defaulting to `SUCCESS`. |
| `created(data)`, `created(data, message)` | 201 Created | Created resource with a message that defaults to `CREATED`. |
| `created()` | 201 Created | Empty HTTP body. |
| `accepted(data)`, `accepted(data, message)` | 202 Accepted | Payload with a message that defaults to `ACCEPTED`. |
| `accepted()`, `accepted(message)` | 202 Accepted | Message only, defaulting to `ACCEPTED`. |
| `noContent()` | 204 No Content | Empty HTTP body. |

A single `String` argument to `success` or `accepted` selects the message overload. Supply both arguments to return a string as the payload.

```java
return RestApiResponse.success("Saved");            // message = "Saved"
// Or, when the endpoint returns a string payload
return RestApiResponse.success("draft", "Loaded");  // data = "draft"
```

## Error methods

Error factories return `ResponseEntity<RestApiResponse<Void>>` with `success` set to `false` and an `ApiErrorResponse` in `error`. Pass the request path and a caller-facing message to the shortcut methods.

| Factory | HTTP status | Usage |
| --- | --- | --- |
| `badRequest(path, message)` | 400 Bad Request | Describe an invalid request. |
| `badRequest(errorDetails)`, `badRequest(errorDetails, message)` | 400 Bad Request | Wrap an existing error. An explicit message overrides the top-level message. |
| `validationError(path, message, fieldErrors, globalErrors)` | 400 Bad Request | Pass field-specific errors as a `Map<String, Object>` and request-wide errors as a `List<String>`. |
| `unauthorized(path, message)` | 401 Unauthorized | Report an authentication failure. |
| `forbidden(path, message)` | 403 Forbidden | Report denied access. |
| `notFound(path, message)` | 404 Not Found | Identify a missing resource. |
| `methodNotAllowed(errorResponse, message, headers)` | 405 Method Not Allowed | Wrap an existing error and forward HTTP headers such as `Allow`. |
| `conflict(path, message)` | 409 Conflict | Describe a conflict with the resource's current state. |
| `internalServerError(path, message)` | 500 Internal Server Error | Report an unexpected server failure. |

```java
return RestApiResponse.notFound("/notes/42", "Note 42 was not found");
```

## ApiErrorResponse

Use `new ApiErrorResponse(status, message, path)` for the numeric HTTP status, caller-facing message, and request path. The four-argument constructor adds an `error` label such as `"Bad Request"`. It accepts either a string message or a `SystemError`, whose `getErrorMessage()` supplies the text.

The six-argument constructors also accept `fieldErrors` and `globalErrors`. Both message types support a `Map<String, Object>` of field errors and a `List<String>` of request-wide errors. The string-message overload also accepts an arbitrary object for `fieldErrors`.

The timestamp defaults to the local creation time. All fields have getters. Use `setTimestamp`, `setError`, `setFieldErrors`, or `setGlobalErrors` to update optional details. When a factory wraps the error, it reuses the error timestamp if present.

## ResponseMetadata

Calling `RestApiResponse.success(page)` sets `data` to the page content and builds `metadata.pagination` automatically. To construct metadata directly, choose a constructor according to the information available.

| Constructor | Fields populated |
| --- | --- |
| `ResponseMetadata()` | Start empty and populate with `setPagination(...)` or `setLinks(...)`. |
| `ResponseMetadata(page)` | Pagination derived from a Spring Data `Page<T>`. |
| `ResponseMetadata(links)` | Named links supplied as a `Map<String, String>`. |
| `ResponseMetadata(page, links)` | Both pagination and links. |

Read the fields with `getPagination()` and `getLinks()`. The page factories populate pagination only.

## PaginationInfo

`new PaginationInfo(page)` copies the following values from a Spring Data page. For example, the first page of 23 matching items with a requested page size of 10 has these values.

| Field | Meaning | Example |
| --- | --- | --- |
| `currentPage` | Zero-based page number. | `0` |
| `pageSize` | Requested page size, including on a partially filled last page. | `10` |
| `totalItems` | Number of matching items across all pages. | `23` |
| `totalPages` | Number of pages in the result set. | `3` |
| `hasNext` | Whether another page follows. | `true` |
| `hasPrevious` | Whether an earlier page exists. | `false` |

The class also provides a no-argument constructor, getters and setters, and an all-arguments constructor in the field order shown above. The page constructor uses `Math.toIntExact` for `totalItems` and throws `ArithmeticException` if the count exceeds the range of an `int`.
