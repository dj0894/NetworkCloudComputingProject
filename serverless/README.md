## Serverless Repository 

This repository contains code which is used in the Lambda Function. The Lambda Function's responsbility is to send a email 
when the book event is received. The type of the event for the book is either CREATE/DELETE. The lambda first checks whether the event is already handled by Lambda. So there is idempotence with the event. 


## Resources Required for Lambda Function
DynamoDB : It keeps a record of Emails sent for Book.  
SNS: A Subscription is created with Lambda, All SNS events triggers the Lambda. 


## Testing the Application

Deploy the Application code to the lambda, setup of DynamoDB is required for this. 

### Sample Test Event

The below test event can be configured and can be invoked manually in the lambda.

```
{
  "Records": [
    {
      "EventSource": "aws:sns",
      "EventVersion": "1.0",
      "EventSubscriptionArn": "arn:aws:sns:us-east-1:{{{accountId}}}:ExampleTopic",
      "Sns": {
        "Type": "Notification",
        "MessageId": "95df01b4-ee98-5cb9-9903-4c221d41eb5e",
        "Subject": "example subject",
        "Message": "{\"bookId\": \"bookId2\", \"email\": \"email1\", \"eventType\": \"CREATE\"}",
        "Timestamp": "1970-01-01T00:00:00.000Z",
        "SignatureVersion": "1",
        "Signature": "EXAMPLE",
        "SigningCertUrl": "EXAMPLE",
        "UnsubscribeUrl": "EXAMPLE",
        "MessageAttributes": {
          "Test": {
            "Type": "String",
            "Value": "TestString"
          },
          "TestBinary": {
            "Type": "Binary",
            "Value": "TestBinary"
          }
        }
      }
    }
  ]
}

```
