# foodtrucks-sf

San Francisco Food Truck Service

Version : 1.0.0

08/03/2015

Hosted at : http://ec2-52-27-9-65.us-west-2.compute.amazonaws.com:8080/foodtruckservice/v1/api-docs/sf

Github link : https://github.com/sanjanamaiya/foodtrucks-sf

What the project is about
-------------------------
The project is based on the Uber challenge : SF Food Trucks. The problem is to create a service which allows the user to find food trucks that can be found close to a particular location on a map.

Usage
-----
The REST documentation at  http://ec2-52-27-9-65.us-west-2.compute.amazonaws.com:8080/foodtruckservice/v1/api-docs/sf provides details of the endpoints which are available. The endpoints are also available on the same server.

To build the project and create the war file, run
mvn clean package

Solution
---------

I do not have any front-end experience, so I have decided to go with the back-end track, implemented in Java.
The data available is placed in a relational DB, MYSQL, since the data is well structured and of limited size. I initially wanted to use a lighter database like SQLite, but decided against it since it is primarily an embedded DB. The MYSQL database is set up on a separate RDS cloud machine.
 
A RESTful web service talks to the database through a datamodel layer, and serves requests to users. The RESTful service was developed using Jersey, and Data Access Objects are used  to access the database. I decided against using an ORM library for the project since, in its current state, the database has only 1 table and mapping between objects and the table is fairly straightforward. 

The main challenge in the problem statement is to efficiently find the food trucks which are closest to a given location on a map. The trivial solution entails calculating the geographic distance between each truck and the new location, and sorting these distances. To make this more efficient, the truck locations are saved in a 2D matrix which is organized such that it represents a map of all food trucks. Finding food trucks close to a point and within a given range is O(k), where k is the number of trucks that are within the range. Using a kd tree was another alternative initially considered, but the matrix implementation is simpler and provides a similar or better time complexity compared to kd tree.

The libraries/software used for the project
-------------------------------------------

1. Jersey (for REST)
2. Jackson (JSON provider)
3. Apache Tomcat 
4. s2-geometry-library-java (for calculating the geo distance between 2 points) : This is licensed under Apache 2.0 license
5. MYSQL database 
6. Swagger (documentation)
7. junit
8. Powermock
9. JerseyTest
10. Grizzly (container)
11. maven

I have used Jersey, Jackson, Tomcat, maven and junit to set up web services before in Java.

Improvements
-------------
1. More rest endpoints could have been developed, exposing more functionality to the user. This includes retrieving food trucks in a given range which serve certain food items.
2. Component test cases need to be added to tie up groups of classes
3. Load testing needs to be done
4. Given more time, would have tried my hand at front end development

About me
--------
https://www.linkedin.com/in/msanjana

https://github.com/sanjanamaiya

sanjana.maiya@gmail.com
