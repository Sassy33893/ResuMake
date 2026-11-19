ResuMake

On Windows, double-click ResuMake.bat to start the app with a JDK on PATH.

Build the executable JAR

From the repository root, run one of these commands with a Java JDK available.

    .\mvnw.cmd clean package    (Windows)
    ./mvnw clean package        (macOS or Linux)
    mvn clean package           (with Maven already installed)

The Maven wrapper downloads Maven on its first run. The build creates
target/ResuMake.jar with PDFBox included.

Run the JAR

The JAR requires Java 8 or later.

To use the same data as the Windows launcher, run from the src folder.

    cd src
    java -jar ../target/ResuMake.jar

You can also copy the JAR to a separate folder and run java -jar ResuMake.jar
from that folder. The app reads and writes Data and Resumes relative to the
directory where you start it.
