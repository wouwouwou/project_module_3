# Moduleproject 3: Let's Talk
Moduleproject 3: Let's Talk is an application that supports chatting over a WiFi network without use of an actual WiFi connection or a server. The whole network is peer-to-peer!

## Getting started
In order to setup the network, your network card must be set to ad-hoc. The script to do this is located in "Project files/adhoc". Run adhoc_setup in root or sudo. Note that the group number should be changed to your own group number or an random number. Your computer number should also be changed to an unique number.
Now you're all set up! Open the program by running the Main-class!

## Simulation setup
Not the room and space or computers to test this entire network? You can run this application multiple times with different computer numbers (and let computers 'ignore' eachother). Add numbers to of nodes you do not want to be neighbours of eachother to the following in line in network/NetworkManager.java:
  private final byte[] excluded = new byte[]{};
You can manually set the computer id by uncommenting and changing the number of the following line in network/NetworkManager.java:
  //Protocol.CLIENT_ID = 4;




Version 1.0
