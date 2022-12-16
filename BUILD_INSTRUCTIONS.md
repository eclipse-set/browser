# Build instructions

As the requirements for local development and production deployments are different, this document contains two sets of instructions.
First, instructions on how to work on the component locally using the Eclipse IDE (e.g. for development) and secondly instructions on how to do a full production build locally via Maven are provided. 

## Prerequisites

- A Java Development Kit 17 (or higher)
- Maven (3.8+) 
- Eclipse IDE (2022-06+)
- Rust Toolchain (for Windows, either MSVC or MinGW, 1.62+)

# Development

This is the recommended way to build and debug for development. 

1. Download and extract a CEF build from `https://cef-builds.spotifycdn.com/index.html#windows64` into a new `cef` directory. 
   1. For the exact version required, check `CEF_VERSION.txt`. 
   2. The resulting directory structure should have the following file: `cef/Release/libcef.dll`. 
2. In the extracted folder, copy the contents from the `Resources/`-directory to the `Release/`-directory 
3. Run `cargo build --release` in the `native` directory
4. Copy `chromium_jni.dll` and `chromium_subp.exe` from the `native/target` directory to `java/org.eclipse.set.browser.lib/res`.
5. Create a new workspace in the Eclipse IDE
6. Import projects from `java/` via File -> Import -> Existing Projects into Workspace
7. Import the Checkstyle configuration from the SET repository (`releng/eclipse/CheckstyleEclipse.xml`) via Window -> Preferences -> Checkstyle
8. Build all projects

The example project is a great starting point for trying out new functionality. 

# Production build

This is the recommended way if you want a production-style build. This is also what we have implemented on the Jenkins instance. 

1. Download and extract a CEF build from `https://cef-builds.spotifycdn.com/index.html#windows64` into a new `cef` directory. 
   1. For the exact version required, check `CEF_VERSION.txt`. 
   2. The resulting directory structure should have the following file: `cef/Release/libcef.dll`. 
2. In the extracted folder, copy the contents from the `Resources/`-directory to the `Release/`-directory 
3. Run `cargo build --release` in the `native` directory
4. Copy `chromium_jni.dll` and `chromium_subp.exe` from the `native/target` directory to `java/org.eclipse.set.browser.lib/res`.
5. Build the Java part: `mvn clean verify`

The p2 site for the browser is now located in `java/bundles/org.eclipse.set.browser.releng.repository/target/repository`. 