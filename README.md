## S.E.M.I.C.O.L.O.N.S.
<img align="left" src="https://meinwebserver.non3dd1yd61r0h6y.myfritz.net/for-external-access/semicolons-icon.png">
or: <br>
**S**uper <br>
**E**nhanced <br>
**M**ultithreaded <br>
**I**nteractive <br>
(**C**onstantly) **C**rashing <br>
**O**vercomplicated <br>
**L**inux-like <br>
**O**S-independent <br>
**N**ative-Java <br>
**S**hell

## Installation
***Note: Currently, there is no installer available and the overall "installation" process is only roughly implemented. \
If you really want to install this "Earliest Access" Version, here is some useful data:*** \
\
**File structure:**
<pre>
semicolons
|
|---data (dir)
    |---cmd_history (file)
    |
    |---cmd_history_bak (file)
    |
    |---cmd_history_max_length* (file)
    |
    |---motd* (file)
    |
    |---motd_bak (file)
    |
    |---semicolons-icon.[png,jpeg,bmp,...]* (file)
</pre>
*_Files that need valid content at start are listed below:_\
semicolons/data/cmd_history_max_length: The maximum number of commands stored in command history (default = 4096)\
semicolons/data/motd: The "message of the day", or the default message, that is displayed at startup (anything, but empty not recommended)\
semicolons/data/semicolons-icon.[anyImageFormat]: An image used as the app icon in your OS (can be any, but .jpg and .png are recommended)\
\
**_Files that are deprecated and due for removal:_\
semicolons/data/cmd_history_max_length\
-> Likely implemented into another file or hardcoded\
\
**If you want to use SEMICOLONS at another root directory than default, use the `--root-folder` parameter.**\
**The default location is `/etc/semicolons/` on Linux, and `C:\Program Files\SEMICOLONS\` on Windows.**

## Final note
This project is still in very early development and currently **not** suitable for most applications.\
You can use this shell for yourself and try / test it, but I won't guarantee anything is working or is usable to a certain degree.

*For Windows users:*\
The current main development is done on Linux and I am almost certain, that it won't work for you on Windows.

As a final word, I'd like to say, that any constructive criticism, feedback or improvement is welcome and greatly appreciated (Implying that I don't get overwhelmed).
