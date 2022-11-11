# Eclipse SET Browser Component

This repository contains the embedded browser component for the Eclipse Signalling Engineering Toolbox (Eclipse SET) based on the Chromium Embedded Framework. This component is based upon an older component which was formerly integrated into the [Eclipse Platform](https://projects.eclipse.org/projects/eclipse.platform). It provides a simple to use and modern (Chromium 107+) embedded web browser component for SWT. 

## Usage within Eclipse SET

In Eclipse SET the browser component is used to quickly integrate several subcomponents (e.g. a modern PDF Viewer) by leveraging the wide Javascript ecosystem of available libraries. 

## Usage outside of Eclipse SET

In general this browser component can also be used separately from Eclipse SET. The easiest way to accomplish this is to use the p2 repositories made available from Eclipse SET. These can be found on the [Eclipse SET Download Site](https://download.eclipse.org/set/). Under each release directory, there are two relevant p2 repositories: `browser` and `browser-cef`. 

- `browser` contains the main interfacing code to communicate with CEF and provide a browser. 
- `browser-cef` is a repackaged variant of the official CEF Builds which easily integrates as an OSGi feature. This should be added to your product and provides CEF under the `bin/cef` subdirectory. 

From these two p2 sites, the following features should be added to the target platform:

- `browser`: `org.eclipse.set.browser.feature.feature.group`
- `browser-cef`: `org.eclipse.set.browser.cef.feature.feature.group`

Before using the browser, you first need to use initialize the library: 
- `org.eclipse.set.browser.lib.CEFLibrary.init("path to the cef files");` 

After doing so, you can use the `org.eclipse.set.browser.Browser` class to create a new browser instance. As this component is derived from the Eclipse Platform browser implementation it generally follows the same APIs, so most existing usages of a SWT browser can be reused. As a minimal example for the usage, see `java/org.eclipse.set.browser.example`. There's also further examples on [the SWT snippets website](https://www.eclipse.org/swt/snippets/) under Browser, which are generally applicable if you change the imports from `org.eclipse.swt` to `org.eclipse.set` and add the initialization call. 

**Note**: While we attempt to keep the browser up to date as much as possible, there may be delays towards getting a new CEF/Chromium version into this component. As a result we highly suggest to not use this browser component with untrusted websites (e.g. the internet). 

The CEF/Chromium version being used can be found in `CEF_VERSION.txt`.

### Supported Platforms

Currently only Windows x64 is supported.

## Build Instructions

### TODO

## Links

- [Main SET Repository](https://gitlab.eclipse.org/eclipse/set/set)
- [Eclipse Project Page](https://projects.eclipse.org/projects/technology.set)
- [License (EPL 2.0)](https://gitlab.eclipse.org/eclipse/set/browser/-/blob/main/LICENSE.md)
- [Jenkins (CI) Builds](https://gitlab.eclipse.org/eclipse/set/set)
