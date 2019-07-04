# Software Engineering project 2019 - Collini, Pons, Colazzo
## Gruppo: AM32

### Group Members:
* ### 10533327 Collini Luca ([@Lucaz97](https://github.com/Lucaz97)) - luca.collini@mail.polimi.it
* ### 10533390 Pons Davide ([@DPons97](https://github.com/DPons97)) - davide.pons@mail.polimi.it
* ### 10557158 Colazzo Michele ([@michelecolazzo01](https://github.com/michelecolazzo01)) - michele.colazzo@mail.polimi.it

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket |[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)|
| RMI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |
| Domination or Towers modes | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Terminator | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

### Extracting jar and running it:
We are extracting only one jar from the Launcher class in the package *launcher*. <br>
We are extracting it with the intellij tool from *Project Structure -> Artifacts -> Add JAR from modules with dependencies*.<br>
The main class to set is *it.polimi.ingsw.launcher.Launcher* <br>
*META-INF/MANIFEST.MF* needs to be placed in *src\main\java\resources*. <br>
Then we build the .jar from *Build -> Build artifacts* <br>

As we only extract one jar, we added parameters in order to change the startup behaviour of the program.<br>
If no parameters are passed it will start a gui launcher that lets the user select options from a form.<br>
Parameters can be written in any order and the read ones are:<br>
- **-m** (mode) followed by **c** for client or **s** for server<br>
- **-gui** to load a graphical user interface<br>
- **-cli** to load a command line interface (if both -cli and -gui are passed it will load only the gui) <br>
- **-p** (port) in case of client, to specify server port<br>
- **-c** (connection) in case of client, to specify connection type: followed by **r** for rmi or **s** for socket<br>
- **-n** (nickname) in case of client, followed by a string to specify client nickname<br>

for example:
- to launch a server in the cli just pass: -m s
- to launch a socket client with gui named Bob : -m c -n Bob -s <server_ip> -p <server_port> -c s -gui
