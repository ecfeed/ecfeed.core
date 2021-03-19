#!/bin/bash

if [ -z ${1+x} ]; then

    commit=$(git log --pretty=format:"%ce|%cd|%cn|%s|%H" -n 1)
    
    email=$(echo $commit | cut -d'|' -f1)

    date=$(echo $commit | cut -d'|' -f2)
    name=$(echo $commit | cut -d'|' -f3)
    desc=$(echo $commit | cut -d'|' -f4)
    hash=$(echo $commit | cut -d'|' -f5)

    explanation="Some unit tests failed and the updated version of the ecFeed service will not be deployed on the requested stage. To see the detailed test report go to the AWS console, i.e. https://console.aws.amazon.com/codesuite/codebuild/testReports/reportGroups."

    body="${explanation}<br/><br/>Date: ${date}<br/>Committer: ${name}<br/>Hash: ${hash}<br/>Description: ${desc}"
    
    aws ses send-email \
        --from k.skorupski@testify.no \
        --destination "{\"ToAddresses\":  [\"${email}\"], \"CcAddresses\": [], \"BccAddresses\": []}" \
        --message "{\"Subject\": {\"Data\": \"AWS - CodePipeline - Test - Core\" }, \"Body\": {\"Text\": {\"Data\": \"$body\", \"Charset\": \"UTF-8\"}, \"Html\": { \"Data\": \"$body\", \"Charset\": \"UTF-8\"}}}" \
        || : 

    aws ses send-email \
        --from k.skorupski@testify.no \
        --destination "{\"ToAddresses\":  [\"k.skorupski@testify.no\"], \"CcAddresses\": [], \"BccAddresses\": []}" \
        --message "{\"Subject\": {\"Data\": \"AWS - CodePipeline - Test - Core\" }, \"Body\": {\"Text\": {\"Data\": \"$body\", \"Charset\": \"UTF-8\"}, \"Html\": { \"Data\": \"$body\", \"Charset\": \"UTF-8\"}}}" \
        || : 


fi