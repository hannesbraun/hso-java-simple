# JavaSimple

This is an Eclipse plugin made for Offenburg University to help beginners get started with Java. When installed, you can run a wizard to create a new program. Select either the Java project itself, the source root, a package or a Java file in the project. The wizard can then be brought up in three different ways:

- Click the button with the Offenburg University logo in the toolbar.
- If you're using the Project Explorer, you can right-click your project/source root/... and you'll find an entry called "HSO Simple Program" underneath "New".
- If you're using the Package Explorer, the same applies as for the Project Explorer, with the exception that you have to select "Other...". You will then be able to select the wizard from within the new window.

Once the wizard is brought up, it asks for a program name. This will be used to create a new package underneath the source root of the project. A new class will be created within that package with "Main" appended to its name. Additionally, a library containing the class `JSimple` will be added to the classpath and imported into the newly created class. For example, it offers a lot of beginner-friendly methods to run tests.

## Repository overview

This project consists of four Eclipse projects:

- **hso.plugins.jsimple** - the actual Eclipse plugin
- **hso.jsimple** - the library containing the class `JSimple` (technically it's also a plugin)
- **hso.features.jsimple** - the feature containing both the plugin as well as the library
- **hso.updateSite** - the update site containing the feature

## Making a release

Install Eclipse PDE (if it is not present yet) and open the four projects in your Eclipse workspace. Make sure to adjust the version numbers for the plugin, the library, and the feature correctly. Try to follow Semantic Versioning here. The feature version number is incremented according to the most significant type of change in the plugins it contains.

Then go to [hso.updateSite/site.xml](hso.updateSite/site.xml). For simplicity's sake, we don't serve more than the latest version of the plugins. So just go to the plain XML file and edit the version of the feature to the most current one. Make sure to delete any leftover JAR files from a previous build. Now, click "Build All" in the tab "Site Map". The following files and directories make up your p2 repository:

- features
- plugins
- artifacts.jar
- content.jar
- site.xml

Serve those over HTTP and you should be good to go. Don't forget to set a tag in the Git repository with the version number of the feature.

## License

This project is licensed under the BSD 3-Clause License. See [LICENSE](LICENSE) for more details.
