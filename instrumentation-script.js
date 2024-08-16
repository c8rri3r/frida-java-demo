Java.perform(function () {
    console.log("Is Java Available: " + Java.available)
    var MyClass = Java.use('com.frida.App');

    // Hook the "someTest" function
    MyClass.someTest.implementation = function () {
        console.log('someTest() function called');
        return true;
    };
});