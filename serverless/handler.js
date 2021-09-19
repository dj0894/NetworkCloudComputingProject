'use strict';


console.log('Loading function');

// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
AWS.config.update({ region: 'us-east-1' });
var ses = new AWS.SES({ region: "us-east-1" });
var ddb = new AWS.DynamoDB({ apiVersion: '2012-08-10' });

module.exports.sendEmail = async (event, context) => {
  //console.log('Received event:', JSON.stringify(event, null, 2));

  let result;
  for (let i = 0; i < event.Records.length; i++) {

    const message = JSON.parse(event.Records[i].Sns.Message);
    console.log('From SNS:', message);

    const key = message.bookId + "_" + message.eventType
    const emailAddress = message.email
    var inputParams = {
      TableName: "email-tracking",
      Key: {
        'Id': { S: key }
      }
    };

    var createdAt = new Date().toString();
    var createParams = {
      TableName: "email-tracking",
      Item: {
        'Id': { S: key },
        'createdAt': { S: createdAt }
      }
    };

    console.log("Calling dynamo db");
    let emailBody;
    let emailSubject;

    if (message.eventType === "CREATE") {
      emailSubject = "Book Created Message"
      emailBody = "Your book is created. The link is http://" + process.env.domain + "/books/" + message.bookId;
    }
    if (message.eventType === "DELETE") {
      emailSubject = "Book Deleted Message"
      emailBody = "Your book is deleted. Book Id: " + message.bookId;
    }

    // Call DynamoDB to read the item from the table
    let result = await ddb.getItem(inputParams).promise();
    console.log(result)
    console.log(key)
    if (result.Item !== undefined && result.Item !== null) {
      console.log("Item exists")
    } else {
      console.log("Item does not exists in Dynamo")
      console.log("Sending email");
      let emailSendResult = await sendEmail(emailAddress, emailBody, emailSubject)
      console.log(emailSendResult)
      let dynamoUpdateResult = await ddb.putItem(createParams).promise()
      console.log(dynamoUpdateResult)
    }
  }
  return result;

};

async function sendEmail(emailAddress, emailBody, emailSubject) {
  console.log("sending email");
  var source = process.env.user + "@" + process.env.domain
  console.log(source)
  console.log(emailAddress)
  console.log(emailSubject)
  var params = {
    Destination: {
      ToAddresses: [emailAddress],
    },
    Message: {
      Body: {
        Text: { Data: emailBody },
      },
      Subject: { Data: emailSubject },
    },
    Source: source,
  };

  let result = await ses.sendEmail(params).promise()
  console.log("email sent")
  console.log(result)
}