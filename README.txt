# steam.namer
This is a Java based tool that parses BBCode lists of Stream games and extracts data from Steam servers.

Note that Steam limits the number of requests you can send to it APIs so the tool will make a pause when and error 500 is returned.
It uses an in memory Lucene index to find the game name, it can be a simple example on how to use Lucene.

I run this tool directly from Eclipse, you'll have to do the same, there is no command line args, you need to change variables in Namer class to change them.

This source code is released under WTFPL v2 license.



           DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
                   Version 2, December 2004

Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

Everyone is permitted to copy and distribute verbatim or modified
copies of this license document, and changing it is allowed as long
as the name is changed.

           DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
  TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

 0. You just DO WHAT THE FUCK YOU WANT TO.
 
 
 https://en.wikipedia.org/wiki/WTFPL
