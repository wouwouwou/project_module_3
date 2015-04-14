# Moduleproject 3: Let's Talk
Moduleproject 3: Let's Talk is an application that supports chatting over a WiFi network without use of an actual WiFi connection or a server. The whole network is peer-to-peer!

## Getting started
In order to setup the network, your network card must be set to ad-hoc. The script to do this is located in "Project files/adhoc". Run adhoc_setup in root or sudo. Note that the group number should be changed to your own group number or an random number. Your computer number should also be changed to an unique number.
Now you're all set up! Open the program by running the Main-class!

## Simulation setup
Not the room and space or computers to test this entire network? You can run this application multiple times with different computer numbers (and let computers 'ignore' eachother). Add numbers to of nodes you do not want to be neighbours of eachother to the following in line in network/NetworkManager.java:
```
  private final byte[] excluded = new byte[]{};
```  
to
```
  private final byte[] excluded = new byte[]{1, 2, X};
``` 
You can manually set the computer id by uncommenting and changing the number of the following line in network/NetworkManager.java:
```
  //Protocol.CLIENT_ID = X;
```
to
```
  Protocol.CLIENT_ID = X;
```

## License
This project is released under MIT License.
`
The MIT License (MIT)

Copyright (c) [year] [fullname]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
`

## Version
The lastest version is version 1.0
