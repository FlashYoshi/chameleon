1. Extract the contents of this zip file into your project directory.

2. Copy default.properties to properties. Edit 'properties' by removing the warning at the top of the file, and uncommenting the property assignments. Then fill out correct values for these properties. For example, the build process must know where to find ivy.jar such that it can use Ivy to resolve the build dependencies. DO NOT ADD 'properties' TO VERSION CONTROL. The file will likely be different for all developers, which would lead to conflicts all the time.

3. Copy ivy/ivy.default.properties to ivy/ivy.properties. Uncomment the property assignment to specify the Ivy cache. If you do not yet have an Ivy cache, simply create a new directory somewhere (e.g. ~/.ivy/cache) and use that. DO NOT ADD 'ivy.properties' TO VERSION CONTROL. The file will likely be different for all developers, which would lead to conflicts all the time.
 
4. Run "ant bootstrap" this will download the ssh plugin for Ivy. This plugin is not installed by default, and is typically required to push your releases to a server.

5. Edit ivy/ivy.xml to add dependencies for your project. By default, the latest version of Chameleon is specified as a dependency. Note that dependencies are resolved recursively, so if you specify the Java module as a dependency, you do not have to add Chameleon as well. You will notice this by the amount of jar files that will be downloaded to the lib/ directory. You can find a list of available modules on the Chameleon website.

6. Run "ant" to download and resolve all required libraries. The dependencies are automatically added to the build path.

7. Start developing!
