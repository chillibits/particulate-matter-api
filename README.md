# Particulate Matter API
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d9a58b28e5294920b2aa6a24910f3187)](https://app.codacy.com/gh/ChilliBits/particulate-matter-api?utm_source=github.com&utm_medium=referral&utm_content=ChilliBits/particulate-matter-api&utm_campaign=Badge_Grade_Dashboard)
![Maven Build](https://github.com/ChilliBits/particulate-matter-api/workflows/Maven%20Build/badge.svg)
[![codecov](https://codecov.io/gh/ChilliBits/particulate-matter-api/branch/master/graph/badge.svg)](https://codecov.io/gh/ChilliBits/particulate-matter-api)
[![Uptime Robot ratio (7 days)](https://img.shields.io/uptimerobot/ratio/7/m785103971-22fa0dc3d91b97bed5bbc269)](https://status.pm.chillibits.com/)

**This is the ChilliBits Particulate Matter REST API.**

*Attention: This repository is still in development. Up to now, there's no stable API version.*

## Features
-   **Chart endpoint**
    -   Get chart from single sensor in a specific time span
    -   Get chart from country average in a specific time span with a certain period
    -   Get chart from city average in a specific time span with a certain period

-   **Client endpoint**
    -   Get all clients, which are registered in the system
    -   Get information about a single client by its name

-   **Data endpoint**
    -   Get data records from single sensor in a specific time span
    -   Get data records from single sensor in a specific time span in a compressed form to reduce transmission capacity
    -   Get the latest data record from single sensor
    -   Get average of the latest records of specific sensors
    -   Get data records from country in a specific time span
    -   Get average out of the latest data records of sensors within a specific country
    -   Get data records from city in a specific time span
    -   Get average out of the latest data records of sensors within a specific city

-   **Push endpoint**
    -   Endpoint for data transmissions (more details in the section 'push endpoint')

-   **Ranking endpoint**
    -   Get ranking by country
    -   Get ranking by country in a compressed form
    -   Get ranking by city
    -   Get ranking by city in a compressed form

-   **Sensor endpoint**
    -   Add a sensor to the database
    -   Update a sensor that already exists in the database
    -   Get all sensors
    -   Delete a sensor from the database

-   **Stats endpoint**
    -   Get stats about the api

-   **User endpoint**
    -   Add user to the database
    -   Update an existing user
    -   Get user by its email
    -   Delete user from the database

## Usage
The API documentation can be found at [api.pm.chillibits.com](https://api.pm.chillibits.com/swagger-ui/index.html).

If any questions occur, please feel free to reach out to us via [email](mailto:contact@chillibits.com?subject=Register%20application%20pmapi).

## Push endpoint
If you have a sensor from luftdaten.info / sensor.community, you simply have to enable the option 'Feinstaub-App' in the APIs section. The data gets transmitted to our server automatically.

If you have built your own IoT device, you can also send data to our system. Setup the connection as follows:

-   Request type: **POST request**
-   Host: **api.pm.chillibits.com**
-   Schema: **https**
-   Port: **443**

The required data format can be found in our API documentation at [api.pm.chillibits.com](https://api.pm.chillibits.com/swagger-ui/index.html#/push/pushDataUsingPOST)

## Register an application
If you're the developer of an application and you want to consume data from our API, you have to contact us via [email](mailto:contact@chillibits.com?subject=Register%20application%20pmapi).
The registration process is as follows:

-   We'll check the code of your application to ensure, that you're not using our data incorrectly or with a malicious intention. Therefore, we only accept open source applications.
-   After this verification, we send you a mail with all required information and ask you for required information about you / your organization and your application.
-   You answer the mail with all the required information
-   We'll create a client record, which will have the appropriate access rights for our endpoints
-   Let's go and start building your awesome application.

© ChilliBits 2019 - 2020 (Designed and developed by Marc Auberer)
