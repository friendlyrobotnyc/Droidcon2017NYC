

^theme:Fira Blue
```javascript
Query StartTalk{ slide(id: "1") {
    Title
    Authors
    Company
   }
}
```

```javascript
{
  Title: Landing Apollo on Android,
  Authors:  [“Brian Plummer”,”Mike Nakhimovich],
  Company: New York Times
}
```
---
^insert slides of articles, sections, recipes, comments

#[fit]We work at NYTimes 
#[fit]Where we do _**a lot**_ of data loading
---
#[fit]Data loading is 
#[fit]challenging on Android
---
#[fit]_**Challenges**_
#[fit]Threading
#[fit]Caching
#[fit]Rotation
#[fit]Memory
---

#We mitigate challenges wtih Open Source
#_**OKhttp | RxJava | Retrofit | Immutables| Gson | Guava | SqlDelight/Brite | Store | Curl | JsonViewer.hu**_
---

#Different libraries to fill gaps in REST data loading
---

#Here's how we used to load data
#Using all those great open source libraries
---


#[fit]_**Start with Inspection**_

```java
 curl -i "https://api.github.com/repos/vmg/redcarpet/issues?state=closed" >> closed_issues.json
```


![ inline](json_viewer.png)

---
^
#[fit]Data Modeling with Immutables
<br>
#While code gen helps there's still a lot of manual error prone work

---

^NOTE: show how poorly data is structured and how big it is/why we need reflection free parsing

#[fit] Parsing with Gson & Immutables

:+1: Reflection Free Parsing 
:-1: Another dependencies and boilerplate in each model

---

#[fit] Setting up Networking
^Setup Okhttp
^Setup Retrofit with Okhttp, Moshi, RxJava

---
#[fit]Disk Caching with SqlDelight/Brite 
#[fit]Why  don't we use room? Immutability
^ Find example from boxbee/anchor

---
#Store

:+1: Caching Policies, Request Routing
:-1: One more level of abstraction

---
#Threading with RxJava
---

#Thats a good architecture
#It's also not something we can expect a beginner to know

---
#Apollo Android GraphQL was developed as a culmination of tools, libraries, and patterns to assist in fetching data from modern GraphQL servers

---

#REST has problems
##No control over response (OOMs)
##Bad introspection(Curl? Plugins?)
##Lots of manual work
##Tough to load from multiple sources
---

#What if instead we use Apollo Android and Github's GraphQL API

---
^Brian: Mike is a hardass and expects all the above when I code
#[fit]Demo: Same with Apollo in 5 minutes
^add apollo dep
^instantiate apollo client
^use igraphql to discover + mold your query
^add to android project
^create rxApolloQuery and subscribe to it

---

#What just happened?
##Let’s break it all down!

---

#What’s A GraphQL?
##A query language for APIs and a runtime for fulfilling those queries with your existing data.
##Alternative for Rest-API
##Client driven - get only data you need
^Show chaining multiple queries

---
#Basics - Start with a query
## Queries have params and define shape of response 
```java
   organization(login:”nyTimes”){
     repositories(first:6 {
           Name
}}
```
---

#[fit]You can explore & build queries using graphiql
##Most Graphql Servers have a GUI
^[insert] Github Explorer Demo Gif/Video

---

#Explorer shows you anything that exists in the Schema
###Nullability Rules
###Enum values
###Data Types
###Anything Else?

---

#Fragments  = Partials 
##TODO Brian fill in code sample 
---
#What is Apollo-Android?
##A strongly-typed, caching GraphQL client for Android
##Created based on Facebook's GraphQl Spec
##Similar to Apollo JS + iOS but built for Android from the start
---
#Apollo-Android has 2 main parts
##*Apollo Code Gen - To generate code
##*Apollo Client  - For executing requests
---
#Using Apollo-Android
Add apollo dependencies

---
#Instantiate an Apollo Client
---
#**Add Schema & Query.graphql to your project**
##Apollo Gradle Plugin  will create for you RepoQuery.java a Java representation of Request|Response|Mapper
---

#MyQuery.Builder
##Builder to create your request object
^show demo/example

---
#MyQuery.Data
###Value objects of your query response
###Effective Java defined “Value Object”
###All nested models you need get generated
---
#MyQuery.Mapper
##Reflection Free parsing of a Graphql Response
##No Slower than AutoValue-Moshi (show generated code)
---

# Apollo’s api is very similar to Okhttp
##Stateless Apollo Client that can create an `ApolloCall`
##Which you can enqueue/clone/cancel 
^java show example of above

---


#Nullability
##Graphql has nullable fields (show example)
##Apollo can represent as @Nullable
##Or as Optional<T> (Java, Guava, Shaded)
---
#How About Caching
*HTTP
*Normalized
---
#Http Caching
##Similar to OKHTTP Cache but for POST requests
##Streams response to cache same time as parsing
##Can Set Cache Timeouts
---
#Prefetch into cache
##Useful for background updates of lots of data
---
#Apollo Store - Normalized Cache
##Post Parsing
##Caches each field individually
##  Allows multiple queries to share same cached values
---
#Two implementations of Normalized Cache
##In Memory using Guava Caches (useful for rotation)
##Persistent in SqlLite
##Configurable on a per request basis
---
#Apollo Is Reactive
##QueryWatcher will emit new response when there are changes to the normalized cache records this query depends on or when mutation call occurs

---
#RxJava 1 & 2 support is built in
```java
RxApollo.from(ApolloManager
       .repositories())
       .map(dataResponse -> dataResponse
       .data()
       .organization()
       .repositories())
       .subscribe(view::showRepositories, view::showError)
```
#RxApollo response can be transformed into LiveData
---
#Imperative Store
##Apollo can be your database
##You can update the normalized cache yourself
---
#Mutations
##Queries are for getting Data Mutations are for making changes on server
## Demo: Mutation
---
#Optimistic Updates
##Mutations can update data locally prior to request being sent
##If failure occurs Apollo Store will rollback changes
---
#How its Made:
##Gradle plugin with code gen written in Kotlin
##ApolloClient borrows heavily from OKHTTP (fill in details)
##ApolloCall is similar to OKhttpCall (interceptors all the way down)
---
#Version 1.0 ships today
##380 commits
##1000s of tests
##18 contributors including devs from Shopify, Airbnb, NY Times






