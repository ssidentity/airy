---
title: HTTP
sidebar_label: HTTP
---

This documents offers a high-level overview of the different parts that
compose the Airy API.

## Introduction

The HTTP endpoints adhere to the following conventions:

- Endpoints only accept `POST` JSON requests.
- Communication always requires a valid [JWT token](#authorization), except for
  `/users.login` and `/users.signup` endpoints.
- We use dots for namespacing URLs (eg `/things.add`).

## Authentication

In order to communicate with the API endpoints, you need a valid
[JWT](https://jwt.io/) token. Get a valid token by sending a request to the
login endpoint [login](#login). It returns a short-lived JWT token you can use
for HTTP requests.

### Login

As the purpose of this endpoint is to obtain valid JWT tokens, it
does not require a valid token to be present in the headers.

`POST /users.login`

**Sample request**

```json5
{
  "email": "grace@example.com",
  "password": "avalidpassword"
}
```

**Sample response**

```json
{
  "id": "424242-4242-42-4242-4242",
  "first_name": "Grace",
  "last_name": "Hopper",
  "token": "JWT_TOKEN"
}
```

## Endpoints

The grouping of endpoints reflects the high-level entities of the [Airy Core
Data Model](glossary.md).

### Users

Please refer to our [user](glossary.md#users) definition for more
information.

#### Signup

`POST /users.signup`

**Sample request**

```json
{
  "first_name": "Grace",
  "last_name": "Hopper",
  "password": "the_answer_is_42",
  "email": "grace@example.com"
}
```

The password **must** be at least 6 characters long.

**Sample response**

```json
{
  "id": "424242-4242-42-4242-4242",
  "first_name": "Grace",
  "last_name": "Hopper",
  "token": "JWT_TOKEN"
}
```

This endpoint returns the same response as `POST /login`.

### Conversations

Please refer to our [conversation](glossary.md#conversation) definition
for more information.

#### List conversations

`POST /conversations.list`

This is a [paginated](#pagination) endpoint.

**Filtering**

This endpoint allows you to query conversations using the human readable [Lucene
Query Syntax](https://lucene.apache.org/core/2_9_4/queryparsersyntax.html). You
can query on all fields defined in [this
class](https://github.com/airyhq/airy/blob/main/backend/api/communication/src/main/java/co/airy/core/api/communication/dto/ConversationIndex.java).

**Sample request**

Find users whose name ends with "Lovelace":

```json5
{
  "filters": "display_name:*Lovelace", // optional
  "cursor": "next-page-uuid",
  "page_size": 2
}
```

**Sample request**

```json5
{
  "cursor": "next-page-uuid",
  "page_size": 2
}
```

**Sample response**

```json5
{
  "data": [
    {
      "id": "a688d36c-a85e-44af-bc02-4248c2c97622",
      "channel": {
        "name": "Facebook page name",
        "source": "facebook",
        "id": "318efa04-7cc1-4200-988e-50699d0dd6e3"
      },
      "created_at": "2019-01-07T09:01:44.000Z",
      "contact": {
        // Additional data on the contact
        "avatar_url": "https://assets.airy.co/AirySupportIcon.jpg",
        "first_name": "Airy Support",
        "last_name": null,
        "id": "36d07b7b-e242-4612-a82c-76832cfd1026"
      },
      "tags": ["f339c325-8614-43cb-a70a-e83d81bf56fc"],
      "last_message": {
        id: "{UUID}",
        content: [
          {
            text: "{String}",
            type: "text"
            // Determines the schema of the content
          },
        ]
        // typed source message model
        state: "{String}",
        // delivery state of message, one of PENDING, FAILED, DELIVERED
        sender_type: "{string/enum}",
        // See glossary
        sent_at: "{string}"
        //'yyyy-MM-dd'T'HH:mm:ss.SSSZ' date in UTC form, to be localized by clients
      },
      "unread_message_count": 1
    }
  ],
  "response_metadata": {
    "previous_cursor": "",
    "next_cursor": "",
    "filtered_total": 1,
    "total": 1,
    "badge_unread_count": 1
  }
}
```

#### Conversation info

`POST /conversations.info`

**Sample request**

```json
{
  "conversation_id": "a688d36c-a85e-44af-bc02-4248c2c97622"
}
```

**Sample response**

```json5
{
  "id": "a688d36c-a85e-44af-bc02-4248c2c97622",
  "channel": {
    "name": "facebook",
    "id": "318efa04-7cc1-4200-988e-50699d0dd6e3"
  },
  "created_at": "2019-01-07T09:01:44.000Z",
  "contact": {
    "avatar_url": "https://assets.airy.co/AirySupportIcon.jpg",
    // optional
    "first_name": "Airy Support",
    // optional
    "last_name": null,
    // optional
    "id": "36d07b7b-e242-4612-a82c-76832cfd1026"
  },
  "tags": ["f339c325-8614-43cb-a70a-e83d81bf56fc"],
  "last_message": {
    "id": "{UUID}",
    "content": [
      {
        "text": "{String}",
        "type": "text"
        // Determines the schema of the content
      }
    ],
    // typed source message model
    "delivery_state": "{String}",
    // delivery state of message, one of PENDING, FAILED, DELIVERED
    "sender_type": "{string/enum}",
    // See glossary
    "sent_at": "{string}"
    //'yyyy-MM-dd'T'HH:mm:ss.SSSZ' date in UTC form, to be localized by clients
  },
  "unread_message_count": 1
}
```

#### Mark conversation as read

`POST /conversations.read`

Resets the unread count of a conversation and returns status code `202 (Accepted)`.

**Sample request**

```json
{
  "conversation_id": "a688d36c-a85e-44af-bc02-4248c2c97622"
}
```

**Sample response**

```json5
{}
```

#### Tag a conversation

Tags an existing conversation with [an existing tag](#creating-a-tag). Returns
status code `200` if successful.

`POST /conversations.tag`

**Sample request**

```json5
{
  "conversation_id": "CONVERSATION_ID",
  "tag_id": "TAG_ID"
}
```

**Sample response**

```json5
{}
```

#### Untag a conversation

`POST /conversations.untag`

**Sample request**

```json5
{
  "conversation_id": "CONVERSATION_ID",
  "tag_id": "TAG_ID"
}
```

**Sample response**

```json5
{}
```

### Messages

Please refer to our [messages](glossary.md#message) definition for more
information.

#### List messages

`POST /messages.list`

This is a [paginated](#pagination) endpoint. Messages are sorted from oldest to latest.

**Sample request**

```json5
{
  "conversation_id": "4242-4242-4242-424242",
  "cursor": "next-page-uuid", // optional
  "page_size": 2 // optional
}
```

**Sample response**

```json5
{
  "data": [
    {
      "id": "{UUID}",
      "content": [
        {
          "text": "{String}",
          "type": "text"
          // Determines the schema of the content
        }
      ],
      // typed source message model
      "state": "{String}",
      // delivery state of message, one of PENDING, FAILED, DELIVERED
      "sender_type": "{string/enum}",
      // See glossary
      "sent_at": "{string}"
      //'yyyy-MM-dd'T'HH:mm:ss.SSSZ' date in UTC form, to be localized by clients
    }
  ],
  "response_metadata": {
    "previous_cursor": "",
    "next_cursor": "",
    "filtered_total": 1,
    "total": 1
  }
}
```

#### Send a message

`POST /messages.send`

Sends a message to a conversation and returns a payload.

**Sample request**

```json5
{
  "conversation_id": "a688d36c-a85e-44af-bc02-4248c2c97622",
  "message": {
    "text": "{String}"
  }
}
```

**Sample response**

```json5
{
  "id": "{UUID}",
  "content": [
    {
      "text": "{String}",
      "type": "text"
      // Determines the schema of the content
    }
  ],
  // typed source message model
  "state": "{String}",
  // delivery state of message, one of PENDING, FAILED, DELIVERED
  "sender_type": "{string/enum}",
  // See glossary
  "sent_at": "{string}"
  //'yyyy-MM-dd'T'HH:mm:ss.SSSZ' date in UTC form, to be localized by clients
}
```

### Channels

Please refer to our [channel](glossary.md#channel) definition for more
information.

#### List channels

`POST /channels.list`

**Sample response**

```json5
{
  "data": [
    {
      "id": "channel-uuid-1",
      "name": "my page 1",
      "source": "facebook",
      "source_channel_id": "fb-page-id-1",
      "image_url": "http://example.org/avatar.jpeg" // optional
    },
    {
      "id": "channel-uuid-2",
      "name": "my page 2",
      "source": "facebook",
      "source_channel_id": "fb-page-id-2"
    }
  ]
}
```

### Tags

Please refer to our [tag](glossary.md#tag) definition for more
information.

#### Creating a tag

`POST /tags.create`

**Sample request**

```json5
{
  "name": "Urgent",
  "color": "tag-red" // one of tag-red | tag-blue | tag-green | tag-purple
}
```

If the tag is successfully created, the request returns status code `201` (created) with the tag ID in the response body.

**Sample response**

```json5
{
  "id": "TAG-UUID"
}
```

#### Updating a tag

`POST /tags.update`

**Sample request**

```json
{
  "id": "TAG-ID",
  "name": "Urgent",
  "color": "tag-blue" // one of tag-red | tag-blue | tag-green | tag-purple
}
```

If action is successful, the request returns status code `200`.

**Sample response**

```json5
{}
```

#### Deleting a tag

`POST /tags.delete`

**Sample request**

```json
{
  "id": "ID-OF-THE-TAG"
}
```

If action is successful, returns HTTP status `200`.

**Sample response**

```json5
{}
```

#### Listing tags

`POST /tags.list`

**Sample response**

```json5
{
  "tags": [
    {
      "id": "TAG-ID",
      "name": "name of the tag",
      "color": "RED"
    }
  ]
}
```

### Metadata

Refer to our [metadata](glossary.md#metadata) definition for more
information.

### Setting metadata

`POST /metadata.set`

```json
{
  "conversation_id": "conversation-id",
  "key": "ad.id",
  "value": "Grace"
}
```

The endpoint returns status code `200` if the operation was successful, and `400` if not.

### Removing metadata

`POST /metadata.remove`

```json
{
  "conversation_id": "conversation-id",
  "key": "ad.id"
}
```

This endpoint returns status code `200` if the operation was successful, and `500` if not.

## Pagination

By default, paginated endpoints return a maximum of 20 elements on the first page.

The size of the returned page can be controlled by the `page_size` field of the
body. You can move back and forth between pages using the `cursor` field of the
body.

Paginated endpoints _always_ respond with the following JSON format:

```json
{
  "data": [
    {
      "id": "a688d36c-a85e-44af-bc02-4248c2c97622",
      "field1": "answer is 42",
      "field2": "this is fine"
    }
  ],
  "response_metadata": {
    "previous_cursor": "",
    "next_cursor": "",
    "filtered_total": 1,
    "total": 1
  }
}
```

The response comes in two parts:

- `data`

  An array of objects. Object specification depends on the endpoint.

- `response_metadata`
  An object with the following fields:

  - `previous_cursor`

    The ID of first elements in the previous page of data. Empty if the returned
    page is the first one.

  - `next_cursor`

    The ID of first elements in the next page of data. Empty if the returned
    page is the last one.

  - `filtered_total`

    The total number of elements across pages in the context of the current
    filter selection. Only applicable to paginated endpoints that can filter
    data.

  - `total`

    The total number of elements across all pages.
