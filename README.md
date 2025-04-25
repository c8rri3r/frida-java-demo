# Frida Java Demo
A simple Java application to experiment with Frida's method hooking feature. 

## Background
This repo was originally created to assist with debugging an issue with Frida and (HotSpot) JDK17. Frida is able to hook Java methods while the bundled target application is running with [JDK11](https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.22%2B7/OpenJDK11U-jdk_x64_linux_hotspot_11.0.22_7.tar.gz) or [JDK16](https://github.com/adoptium/temurin16-binaries/releases/download/jdk-16.0.2%2B7/OpenJDK16U-jdk_x64_linux_hotspot_16.0.2_7.tar.gz).

When the bundled application is running with [JDK17](https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.12_7.tar.gz), hooking a method causes the JVM to crash with the following error message (trimmed for brevity):

```bash
└─$ $JAVA_HOME/bin/java -jar target/frida-java-demo-1.0.jar
[*] Checking status of someTest()...
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x0000000000000000, pid=4713, tid=4714
#
# JRE version: OpenJDK Runtime Environment Temurin-17.0.12+7 (17.0.12+7) (build 17.0.12+7)
# Java VM: OpenJDK 64-Bit Server VM Temurin-17.0.12+7 (17.0.12+7, mixed mode, sharing, tiered, compressed oops, compressed class ptrs, g1 gc, linux-amd64)
# Problematic frame:
# j  com.frida.App.main([Ljava/lang/String;)V+8
```

The Frida console provides a more descriptive error message:

```javascript
Is Java Available: true
Error: Unable to make thread_from_jni_environment() helper for the current architecture
    at <anonymous> (frida/node_modules/frida-java-bridge/lib/jvm.js:232)
    at <anonymous> (frida/node_modules/frida-java-bridge/lib/jvm.js:276)
    at <anonymous> (frida/node_modules/frida-java-bridge/lib/vm.js:12)
    at j (frida/node_modules/frida-java-bridge/lib/jvm.js:291)
```

This crash happens despite the fact that Frida indicates the JDK is a compatible version.

```javascript
[Local::PID::25154 ]-> Java.available
true
[Local::PID::25154 ]-> Java.api
{
    "$delete": "0x7efc27615450",
    "$new": "0x7efc264a04e0",
    "ClassLoaderDataGraph::classes_do": "0x7efc26c323c0",
    "ClassLoaderDataGraph::clean_deallocate_lists": "0x7efc26c336e0",
    "JNI_GetCreatedJavaVMs": "0x7efc26f60f80",
    "JVM_Sleep": "0x7efc26faf5a0",
    "Method::clear_native_function": "0x7efc27251d40",
    "Method::jmethod_id": "0x7efc272553f0",
    "Method::restore_unshareable_info": "0x7efc272523b0",
    "Method::set_native_function": "0x7efc27251c70",
    "Method::size": "0x7efc27250930",
    "NMethodSweeper::force_sweep": "0x7efc274a7910",
    "NMethodSweeper::sweep_code_cache": "0x7efc274a83f0",
    "OopMapCache::flush_obsolete_entries": "0x7efc272b8790",
    "ResolvedMethodTable::adjust_method_entries": "0x7efc2736b1b0",
    "VMThread::execute": "0x7efc275904f0",
    "VM_RedefineClasses::flush_dependent_code": "0x7efc270ecdd0",
    "VM_RedefineClasses::mark_dependent_code": "0x7efc270e8a70",
    "createNewDefaultVtableIndices": "0x7efc26ea33c0",
    "doKlass": "0x7efc270e4830",
    "flavor": "jvm",
    "jvmti": {
        "handle": "0x7efb500023e0",
        "vm": {
            "handle": "0x7efc279674e0"
        },
        "vtable": "0x7efc27970640"
    },
    "redefineClassesAllow": "0x7efc270f1c00",
    "redefineClassesDoIt": "0x7efc270ed6a0",
    "redefineClassesDoItEpilogue": "0x7efc270e8ce0",
    "redefineClassesDoItPrologue": "0x7efc270f1910",
    "redefineClassesOnError": "0x7efc270e4770",
    "shouldSweep": "0x7efc279cf189",
    "traversals": "0x7efc279cf1a8",
    "version": 17,
    "versionS": "17.0.12+7",
    "vm": "0x7efc279674e0",
    "vtableRedefineClasses": "0x7efc278b9ee8"
}
```

## Dependencies
* Maven 3.x (tested with 3.8.7)
* HostSpot JDK 17 with Debugging Symbols (tested with [Adoptium JDK 17.0.12_7](https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.12_7.tar.gz))
* Frida 16.x (tested with 16.4.8)
* Linux host, MAC will probably also work (tested on Ubuntu 22.02 and Kali Linux running under WSL)

## Compiling
Compile an executable jar file. Versions [11](https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.22%2B7/OpenJDK11U-jdk_x64_linux_hotspot_11.0.22_7.tar.gz), [16](https://github.com/adoptium/temurin16-binaries/releases/download/jdk-16.0.2%2B7/OpenJDK16U-jdk_x64_linux_hotspot_16.0.2_7.tar.gz) and [17](https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.12_7.tar.gz) of the JDK were tested. It doesn't seem to matter which version is used for compiling, although for consistency it's suggested to use the same one that'll be used during runtime.
```bash
export JAVA_HOME=<JDK installation directory>
mvn clean package
```

## Updates
Updates to this issue are being tracked in GitHub issue (333)[https://github.com/frida/frida-java-bridge/issues/333].

## Example Frida Instrumentation Script
The included Frida script `instrumentation-script.js` will hook the `someTest()` function and force it to always return `true`.


## Usage
1. Compile the application per the instructions above
2. Run the demo app using JDK17 to recreate the crash, or JDK11 (or JDK16) to successfully hook the method.
```bash
export JAVA_HOME=<JDK installation directory>
$JAVA_HOME/bin/java -jar target/frida-java-demo-1.0.jar
```
3. Use the `ps` command to find the PID of the demo app.
4. In a seperate terminal, execute Frida with the provided instrumentation script: 

`frida -p <pid> -l instrumentation-script.js`

If all goes well, we should see the "Congrats" message on the Demo app.
```bash
└─$ $JAVA_HOME/bin/java -jar target/frida-java-demo-1.0.jar
[*] Checking status of someTest()...
[*] Congrats, result has been changed!
```