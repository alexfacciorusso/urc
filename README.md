# Urc
<a href="https://jitpack.io/#alexfacciorusso/urc">
    <img src="https://img.shields.io/github/release/alexfacciorusso/urc.svg?label=JitPack" alt="JitPack release">
</a>
![](https://img.shields.io/github/stars/alexfacciorusso/urc.svg)

Uniform Resource Creator. An Android (working for Java-generic) library that helps user to create URLs for RESTful apis.

The path syntax is taken from Slim framework, so it is in the form of `/static/:argument`.

## Example
    Urc myApi = Urc.with("https://my-api.com/v1");
    myApi.fromEndpoint("/:firstArgument/try/:second")
         .addParameter("firstArgument", "first")
         .addParameter("second", 2)
         .addParameter("noListed", "hello")
         .build();

The resulting url will be: `https://my-api.com/v1/first/try/2?noListed=hello`

**Note:** The query parameter generation is enabled by default. It can be disabled by the `.setQueryParametersEnabled(boolean)` method in the `UrcGenerator` class.

## Installation
### As library
Simply checkout this repo and import the library in Android Studio.

### As Gradle dependency
Follow instructions on https://jitpack.io/#alexfacciorusso/urc (go on that link
and then click on "Get it" button near the latest release).

## FAQ
#### Why this library uses two classes (`Urc` and `UrcGenerator`) to build the Url/Uri?
Because the method `Uri.with` returns the "service" builder. The system allows to build several uris from a single service.

Example:

    // This is our main service/api url.
    Urc ourService = Urc.with("https://ourservice.com");

    String listUrl = ourService.fromEndpoint("/list/:id")
        ...
        .build();
    String createUrl = ourService.fromEndpoint("/create/:name")
        ...
        .build();

## License
Created by Alex Facciorusso and licensed under Apache 2.0.

Please read the included LICENSE.txt file for the full terms and conditions.
