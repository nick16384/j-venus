## S.E.M.I.C.O.L.O.N.S.
An advanced Java written shell / console for any operating system.

## What does the abbreviation mean?
<img align="right" src="https://meinwebserver.non3dd1yd61r0h6y.myfritz.net/for-external-access/semicolons-icon.png">
<!-- This text segment has to be HTML formatted, because it's right next to the image -->
Well, <br>
<b>S</b>uper <br>
<b>E</b>nhanced <br>
<b>M</b>ultithreaded <br>
<b>I</b>nteractive <br>
(<b>C</b>onstantly) <b>C</b>rashing <br>
<b>O</b>vercomplicated <br>
<b>L</b>inux-like <br>
<b>O</b>S-independent <br>
<b>N</b>ative-Java <br>
<b>S</b>hell

## What is "S.E.M.I.C.O.L.O.N.S."?
S.E.M.I.C.O.L.O.N.S. is a java shell project, which is planned to contain a lot of advanced and \
helpful features. (Probably more than any "usual" shell). Reaching this point is far in the future \
and I am programming just for fun, so don't wonder about the current pile of bugs, instabilities, \
lack of compatibility and features, etc.

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
-> Likely implemented into another file or hardcoded

**If you want to use SEMICOLONS at another root directory than default, use the `--root-folder` parameter.**\
**The default location is `/etc/semicolons/` on Linux, and `C:\Program Files\SEMICOLONS\` on Windows.**


## Current development status:
The following list lists all current issues and features to be implemented.
Be aware, that it might not be fully up to date, because this list isn't updated daily.
- [ ] 0. 🔁 <b>\[Bug/Issue] Separating entire project into shell and GUI part (due to increased architectural issues)</b>
- [ ] 1. ⛔ \[Feature] Add "undo" command for file operation (after e.g. accidental file deletion)
- [ ] 2. ⛔ \[Feature] Add support for command interaction (Bi-directional streams)
- [ ] 3. 🔁 \[Feature] Live syntax highlighting, as user types (JavaFX only, if.)
- [x] 4. ✔️ \[Bug/Issue] Make command executable, even if caret is on the middle of it
- [x] 5. ✔️ \[Bug/Issue] Don't allow shell text to be edited by the user
- [ ] 6. ⛔ \[Feature] Bi-Directional Network communication (User types and the other shell on another machine sees it)
- [ ] 7. ⛔ \[Feature] Slight transparency for window on systems, that support it
- [ ] 8. 🔁 \[Feature] Add keyboard shortcuts (e.g. CTRL + C for SIGTERM)
- [ ] 9. 🔁 \[Feature] Add CLI support (don't launch another window)
- [ ] 10. 🔁 \[Feature] Overlay suggesting possible command completions

**Legend**: \
✔️ = Fully implemented and functional \
✅ = Implemented / fixed, but to be tested \
🔁 = Currently being addressed \
⛔ = Currently not implemented / fixed \
*Special:* \
🔶 = Cancelled (Too complex or Java API doesn't support it) \
⬜ = Unknown (Bug/Issue not accepted / validated yet)


## A little bit of background for the project
In 2021, I wanted to start and maintain a new (and bigger) Java project. \
It would finally turn out, to be a shell / console with a lot of advanced feature ideas, \
but development was (and still is) quite slow and unregular. During the development, \
S.E.M.I.C.O.L.O.N.S. started as a tiny, bugged, unstable and alomost-unable-to-do-anything-shell, \
which was lost later due to improper backups and accidental deletion. \
Since the project was rebuilt from scratch (it was worth it, the 0.1 version was a huge mess), it \
now had a completely different command structure, more stability and moved on being developed. \
Aaaand there is another point I don't want to forget: S.E.M.I.C.O.L.O.N.S. had undergone a lot of \
renames to now. These include the following 4:
```
1. JDOS (Java Disk Operating System -> DOS was derived from Microsoft and more meant like "command line") [2020 - mid 2022]
2. J-Vexus (Ja-Va EXtensible Universal Shell) [mid 2022 - late mid 2022]
3. J-Venus (JaVa Extensible [The 'n' was never defined] Universal Shell) [late mid 2022 - early 2023]
4. SEMICOLONS (See above for abbreviation) [early 2023 - now] (Current)
```

## Final note
This project is still in very early development and currently **not** suitable for most applications.\
You can use this shell for yourself and try / test it, but I won't guarantee anything is working or is usable to a certain degree.

*For Windows users:*\
The current main development is done on Linux and I am almost certain, that it won't work for you on Windows.

As a final word, I'd like to say, that any constructive criticism, feedback or improvement is welcome and greatly appreciated (Implying that I don't get overwhelmed).
